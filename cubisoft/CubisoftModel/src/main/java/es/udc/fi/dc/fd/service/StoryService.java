package es.udc.fi.dc.fd.service;

import java.io.File;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.InstanceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.udc.fi.dc.fd.model.persistence.Blocks.BlockType;
import es.udc.fi.dc.fd.model.persistence.Picture;
import es.udc.fi.dc.fd.model.persistence.Story;
import es.udc.fi.dc.fd.model.persistence.UserProfile;
import es.udc.fi.dc.fd.repository.BlocksRepository;
import es.udc.fi.dc.fd.repository.PictureRepository;
import es.udc.fi.dc.fd.repository.StoryRepository;
import es.udc.fi.dc.fd.repository.UserProfileRepository;

/**
 * The Class PostService. Class that implements the Post features.
 */
@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class StoryService {

	/** The story repository. */
	@Autowired
	private StoryRepository storyRepository;

	/** The post repository. */
	@Autowired
	private PictureRepository pictureRepository;

	@Autowired
	private UserProfileRepository userProfileRepository;

	@Autowired
	private BlocksRepository blocksRepository;

	private final static Logger logger = Logger.getLogger(StoryService.class.getName());

	public static final String UPLOADS_FOLDER_NAME = "Pictures";

	public static final String PICTURE_DELETE_ERROR = "Error deleting the picture";

	/**
	 * Save.
	 *
	 * @param story
	 *            the story
	 * @return the picture
	 */
	@Transactional
	public Story save(Story story) {
		return storyRepository.save(story);
	}

	/**
	 * Method that fins all the user's storys.
	 * 
	 * @param user
	 *            the user
	 * @param loggedUser
	 *            the currently loggedUser
	 * @return the list of storys belonging to the user
	 * @throws InstanceNotFoundException
	 *             If the user does not exists
	 */
	public List<Story> findUserStories(UserProfile user, UserProfile loggedUser) throws InstanceNotFoundException {
		if (user == null) {
			throw new NullPointerException("The user param cannot be null.");
		}
		if (!userProfileRepository.exists(user.getEmail())) {
			throw new InstanceNotFoundException("The user with the mail" + user.getEmail() + " doesnt exists.");
		}
		if (loggedUser == null || !userProfileRepository.exists(loggedUser.getEmail())) {
			storyRepository.findUserStories(user);
		}
		// Filter stories in case the user is blocked by the story owner
		List<Story> blockedStories = storyRepository.findUserStories(user);
		List<Story> notBlockedStories = new ArrayList<Story>();
		for (Story story : blockedStories) {
			if (!blocksRepository.checkBlock(story.getUser(), loggedUser, BlockType.STORIES)) {
				notBlockedStories.add(story);
			}
		}
		return notBlockedStories;

	}

	/**
	 * Method that creates a new story from an user and an existing picture.
	 *
	 * @param picture
	 *            the picture
	 * @param user
	 *            the user
	 * @param remainingTime
	 *            the remaining lifetime for the story in seconds
	 * @return story the story including the date it will expire
	 * @throws InstanceNotFoundException
	 *             the instance not found exception
	 */
	@Transactional(noRollbackFor = Exception.class)
	public Story newStory(Picture picture, UserProfile user, int remainingTime) throws InstanceNotFoundException {
		if (user == null) {
			throw new NullPointerException("The user param cannot be null.");
		}
		if (!userProfileRepository.exists(user.getEmail())) {
			throw new InstanceNotFoundException("The user with email" + user.getEmail() + " doesnt exist.");
		}
		Calendar expirationDate = Calendar.getInstance();
		expirationDate.add(Calendar.SECOND, remainingTime);
		Story story = new Story(expirationDate, picture, user);
		storyRepository.save(story);
		return story;
	}

	/**
	 * Method that deletes an existing story from the database.
	 *
	 * @param sessionPath
	 *            the session path
	 * @param story
	 *            the story to delete.
	 * @return the error message if something unexpected happens.
	 */

	@Transactional
	public String deleteStory(String sessionPath, Story story) {

		if (story.getPicture().getAuthor().getUser_id() == story.getUser().getUser_id()) {

			String folderPath = sessionPath + UPLOADS_FOLDER_NAME;

			String imagePath = folderPath + "/" + story.getPicture().getImage_path();
			File pictureFile = new File(imagePath);

			if (pictureFile.exists()) {
				boolean deleted = pictureFile.delete();
				if (!deleted) {
					return PICTURE_DELETE_ERROR;
				}
			}

			pictureRepository.delete(story.getPicture());
		} else {
			storyRepository.delete(story);
		}

		return "";

	}

	/**
	 * Load feed. Returns the single story feed of an user
	 *
	 * @param feedUserId
	 *            The owner of the feed that we want to return.
	 * @param userAuthenticated
	 *            the user authenticated
	 * @param sessionPath
	 *            the session Path
	 * @return the story feed.
	 */
	public List<Story> loadFeed(Long feedUserId, Principal userAuthenticated, String sessionPath) {
		List<Story> result = null;
		UserProfile feedUser = null;

		if (userAuthenticated != null) {
			feedUser = userProfileRepository.findOneByEmail(userAuthenticated.getName());
		}

		try {
			if (feedUserId == null) {
				feedUserId = feedUser.getUser_id();
			}
			UserProfile userFound = userProfileRepository.findById(feedUserId).get();
			deleteOldStories(userFound, sessionPath);
			result = findUserStories(userFound, feedUser);

		} catch (InstanceNotFoundException e) {
			logger.log(Level.INFO, e.getMessage(), e);
		}

		return result;
	}

	/**
	 * Deletes the expired stories in user's story feed
	 *
	 * @param user
	 *            The owner of the feed that we want to clean.
	 * @param sessionPath
	 *            the session Path
	 * 
	 * @throws InstanceNotFoundException
	 *             the instance not found exception*
	 * 
	 */
	public void deleteOldStories(UserProfile user, String sessionPath) throws InstanceNotFoundException {
		List<Story> list = findUserStories(user, null);
		Calendar currentTime = Calendar.getInstance();
		currentTime.set(Calendar.MILLISECOND, 0);
		for (Story story : list) {
			if (story.getExpiration().before(currentTime)) {
				deleteStory(sessionPath, story);
			}
		}
	}

	/**
	 * Find a story by its ID.
	 *
	 * @param story_id
	 *            the story id
	 * @return the story
	 */
	public Story findByID(Long story_id) {
		return storyRepository.findStoryByStoryId(story_id);
	}

}