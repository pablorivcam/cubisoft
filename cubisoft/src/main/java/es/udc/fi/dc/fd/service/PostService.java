package es.udc.fi.dc.fd.service;

import java.io.File;
import java.security.Principal;
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

import es.udc.fi.dc.fd.controller.picture.PictureUploaderController;
import es.udc.fi.dc.fd.controller.post.FeedViewController;
import es.udc.fi.dc.fd.controller.post.PostViewConstants;
import es.udc.fi.dc.fd.model.persistence.Picture;
import es.udc.fi.dc.fd.model.persistence.Post;
import es.udc.fi.dc.fd.model.persistence.PostView;
import es.udc.fi.dc.fd.model.persistence.UserProfile;
import es.udc.fi.dc.fd.repository.PictureRepository;
import es.udc.fi.dc.fd.repository.PostRepository;
import es.udc.fi.dc.fd.repository.PostViewRepository;
import es.udc.fi.dc.fd.repository.UserProfileRepository;

/**
 * The Class PostService. Class that implements the Post features.
 */
@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class PostService {

	/** The post repository. */
	@Autowired
	private PostRepository postRepository;

	@Autowired
	private UserProfileRepository userProfileRepository;

	@Autowired
	private PictureRepository pictureRepository;

	@Autowired
	private PostViewRepository postViewRepository;

	private final static Logger logger = Logger.getLogger(PostService.class.getName());

	/**
	 * Save.
	 *
	 * @param post
	 *            the post
	 * @return the picture
	 */
	@Transactional
	public Post save(Post post) {
		return postRepository.save(post);
	}

	/**
	 * Find user followers posts.
	 *
	 * @param user
	 *            the user
	 * @return the list
	 * @throws InstanceNotFoundException
	 *             the instance not found exception
	 */
	public List<Post> findUserFollowsPosts(UserProfile user) throws InstanceNotFoundException {

		if (user == null) {
			throw new NullPointerException("The user param cannot be null.");
		}
		if (!userProfileRepository.exists(user.getEmail())) {
			throw new InstanceNotFoundException("The user with the mail" + user.getEmail() + " doesnt exists.");
		}

		return postRepository.findUserFollowsPosts(user);
	}

	/**
	 * Method that fins all the user's posts.
	 * 
	 * @param user
	 *            the user
	 * @return the list of posts belonging to the user
	 * @throws InstanceNotFoundException
	 *             If the user does not exists
	 */
	public List<Post> findUserPosts(UserProfile user) throws InstanceNotFoundException {
		if (user == null) {
			throw new NullPointerException("The user param cannot be null.");
		}
		if (!userProfileRepository.exists(user.getEmail())) {
			throw new InstanceNotFoundException("The user with the mail" + user.getEmail() + " doesnt exists.");
		}
		return postRepository.findUserPosts(user);
	}

	public List<Post> findFollowsAndUserPosts(UserProfile user) throws InstanceNotFoundException {
		if (user == null) {
			throw new NullPointerException("The user param cannot be null.");
		}
		if (!userProfileRepository.exists(user.getEmail())) {
			throw new InstanceNotFoundException("The user with the mail" + user.getEmail() + " doesnt exists.");
		}
		return postRepository.findFollowsAndUserPosts(user);
	}

	/**
	 * Method that creates a new post from an user and an existing picture.
	 *
	 * @param picture
	 *            the picture
	 * @param user
	 *            the user
	 * @return post
	 * @throws InstanceNotFoundException
	 *             the instance not found exception
	 */
	@Transactional(noRollbackFor = Exception.class)
	public Post newPost(Picture picture, UserProfile user) throws InstanceNotFoundException {
		if (user == null) {
			throw new NullPointerException("The user param cannot be null.");
		}
		if (!userProfileRepository.exists(user.getEmail())) {
			throw new InstanceNotFoundException("The user with email" + user.getEmail() + " doesnt exist.");
		}

		Post post = new Post(Calendar.getInstance(), picture, user, (long) 0, (long) 0, (long) 0, false);

		postRepository.save(post);
		return post;
	}

	/**
	 * Method that deletes an existing post from the database.
	 *
	 * @param sessionPath
	 *            the session path
	 * @param post
	 *            the post to delete.
	 * @return the error message if something unexpected happens.
	 */
	@Transactional
	public String deletePost(String sessionPath, Post post) {

		if (post.getPicture().getAuthor().getUser_id() == post.getUser().getUser_id()) {

			String folderPath = sessionPath + PictureUploaderController.UPLOADS_FOLDER_NAME;

			String imagePath = folderPath + "/" + post.getPicture().getImage_path();
			File pictureFile = new File(imagePath);

			if (pictureFile.exists()) {
				boolean deleted = pictureFile.delete();
				if (!deleted) {
					return FeedViewController.PICTURE_DELETE_ERROR;
				}
			}

			pictureRepository.delete(post.getPicture());
		} else {
			postRepository.delete(post);
		}

		return "";

	}

	/**
	 * NewReshare.
	 *
	 * @param post
	 *            the original post
	 * @param user
	 *            the user
	 * @return post reshared
	 * @throws InstanceNotFoundException
	 *             the instance not found exception
	 */
	@Transactional(noRollbackFor = Exception.class)
	public Post newReshare(Post post, UserProfile user) throws InstanceNotFoundException {
		// TODO falta comprobar que el post no sea null o que no exista
		if (user == null) {
			throw new NullPointerException("The user param cannot be null");
		}
		if (!userProfileRepository.exists(user.getEmail())) {
			throw new InstanceNotFoundException("The user with email" + user.getEmail() + " doesn't exist.");
		}

		Post postReshare = new Post(Calendar.getInstance(), post.getPicture(), user, (long) 0, (long) 0, (long) 0,
				true);
		postRepository.save(postReshare);
		return postReshare;
	}

	/**
	 * Load feed. Returns the global/single feed of an user and adds a anonymous
	 * vbiew to all the posts if the param userAuthenticated is false or creates the
	 * view in another case.
	 *
	 * @param feedUserId
	 *            The owner of the feed that we want to return.
	 * @param userAuthenticated
	 *            the user authenticated
	 * @param view
	 *            the view. This method will return the global feed if this param is
	 *            post/globalFeed or the single feed in another case.
	 * @return the post feed.
	 */
	public List<Post> loadFeed(Long feedUserId, Principal userAuthenticated, String view) {
		List<Post> result = null;

		UserProfile feedUser = null;

		if (userAuthenticated != null) {
			feedUser = userProfileRepository.findOneByEmail(userAuthenticated.getName());
		}

		try {
			// If the feed is the global feed we return the global feed posts.
			if (userAuthenticated != null && view.equals(PostViewConstants.VIEW_GLOBAL_FEED)) {
				result = findFollowsAndUserPosts(feedUser);
				// If the feed is the single feed
			} else {
				if (feedUserId == null) {
					feedUserId = feedUser.getUser_id();
				}
				UserProfile userFound = userProfileRepository.findById(feedUserId).get();
				result = findUserPosts(userFound);
				// model.put("userFound", userFound);
			}

			if (feedUser != null) {
				for (Post post : result) {
					if (postViewRepository.findPostView(post, feedUser) == null) {
						postViewRepository.save(new PostView(feedUser, post));
					}
				}
				// model.put("currentUser", user);
			} else {
				for (Post post : result) {
					post.setAnonymousViews(post.getAnonymousViews() + 1);
					save(post);
				}
			}

		} catch (InstanceNotFoundException e) {
			logger.log(Level.INFO, e.getMessage(), e);
		}

		return result;
	}

	/**
	 * Find a post by its ID.
	 *
	 * @param post_id
	 *            the post id
	 * @return the post
	 */
	public Post findByID(Long post_id) {
		return postRepository.findPostByPostId(post_id);
	}

}
