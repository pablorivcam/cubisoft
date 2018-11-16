package es.udc.fi.dc.fd.service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.InstanceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.udc.fi.dc.fd.controller.post.FeedViewController;
import es.udc.fi.dc.fd.model.persistence.Likes;
import es.udc.fi.dc.fd.model.persistence.Post;
import es.udc.fi.dc.fd.model.persistence.UserProfile;
import es.udc.fi.dc.fd.repository.LikesRepository;
import es.udc.fi.dc.fd.repository.PostRepository;
import es.udc.fi.dc.fd.repository.UserProfileRepository;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class LikesService {

	@Autowired
	private LikesRepository likesRepository;
	@Autowired
	private PostRepository postRepository;
	@Autowired
	private UserProfileRepository userProfileRepository;

	@Transactional
	public Likes save(Likes likes) {
		return likesRepository.save(likes);
	}

	/**
	 * Method that allows an user to like some existing post.
	 *
	 * @param user
	 *            the author of the like
	 * @param post
	 *            the post to give a like
	 * @return the like
	 * @throws InstanceNotFoundException
	 *             the instance not found exception
	 * @throws AlreadyLikedException
	 *             the already liked exception
	 */
	@Transactional(noRollbackFor = Exception.class)
	public Likes newLikes(UserProfile user, Post post) throws InstanceNotFoundException, AlreadyLikedException {
		if (user == null) {
			throw new NullPointerException("The user param cannot be null.");
		}
		if (!userProfileRepository.exists(user.getEmail())) {
			throw new InstanceNotFoundException("The user with email" + user.getEmail() + " doesnt exist.");
		}
		if (post == null) {
			throw new NullPointerException("The post param cannot be null.");
		}
		if (!postRepository.existsById(post.getPost_id())) {
			throw new InstanceNotFoundException("The post with the id" + post.getPost_id() + " doesnt exists.");
		}
		Likes like = new Likes(user, post);

		if (this.existLikes(user, post)) {
			throw new AlreadyLikedException("The post has been already liked");
		} else {
			likesRepository.save(like);
		}

		return like;
	}

	/**
	 * Method that returns all the likes made by a specific user.
	 *
	 * @param user
	 *            the owner of the likes
	 * @return the list of likes
	 * @throws InstanceNotFoundException
	 *             the instance not found exception
	 */
	@Transactional(noRollbackFor = Exception.class)
	public List<Likes> findUserLikes(UserProfile user) throws InstanceNotFoundException {
		if (user == null) {
			throw new NullPointerException("The user param cannot be null.");
		}
		if (!userProfileRepository.exists(user.getEmail())) {
			throw new InstanceNotFoundException("The user with the id" + user.getUser_id() + " doesnt exists.");
		}
		return likesRepository.findLikesByUser(user);
	}

	/**
	 * Method that returns all the likes corresponding to some post.
	 *
	 * @param post
	 *            the post to find likes for
	 * @return the list of likes
	 * @throws InstanceNotFoundException
	 *             the instance not found exception
	 */
	@Transactional(noRollbackFor = Exception.class)
	public List<Likes> findPostLikes(Post post) throws InstanceNotFoundException {
		if (post == null) {
			throw new NullPointerException("The post param cannot be null.");
		}
		if (!postRepository.existsById(post.getPost_id())) {
			throw new InstanceNotFoundException("The post with the id" + post.getPost_id() + " doesnt exists.");
		}
		return likesRepository.findLikesByPost(post);
	}

	/**
	 * Unlikes the post for the designed user.
	 *
	 * @param user
	 *            the author of the like
	 * @param post
	 *            the liked post
	 * @throws InstanceNotFoundException
	 *             the instance not found exception
	 * @throws NotLikedYetException
	 *             the not liked yet exception
	 */
	@Transactional(noRollbackFor = Exception.class)
	public void deleteUserPostLikes(UserProfile user, Post post)
			throws InstanceNotFoundException, NotLikedYetException {
		if (user == null) {
			throw new NullPointerException("The user param cannot be null.");
		}
		if (!userProfileRepository.exists(user.getEmail())) {
			throw new InstanceNotFoundException("The user with the id" + user.getUser_id() + " doesnt exists.");
		}
		if (post == null) {
			throw new NullPointerException("The post param cannot be null.");
		}
		if (!postRepository.existsById(post.getPost_id())) {
			throw new InstanceNotFoundException("The post with the id" + post.getPost_id() + " doesnt exists.");
		}
		if (this.existLikes(user, post)) {
			Likes like = likesRepository.findLikesByUserAndPost(user, post);
			likesRepository.delete(like);
		} else {
			throw new NotLikedYetException("The post is not liked yet");
		}
	}

	/**
	 * Method that returns true if the user made some like to an existing post.
	 *
	 * @param user
	 *            the user
	 * @param post
	 *            the post
	 * @return true, if successful
	 */
	public boolean existLikes(UserProfile user, Post post) {
		boolean result = false;
		if (user == null) {
			throw new NullPointerException("The user param cannot be null.");
		}
		if (!userProfileRepository.exists(user.getEmail())) {
			return result;
		}
		if (post == null) {
			throw new NullPointerException("The post param cannot be null.");
		}
		if (!postRepository.existsById(post.getPost_id())) {
			return result;
		}

		Likes like = likesRepository.findLikesByUserAndPost(user, post);
		if (like != null) {
			result = true;
		}

		return result;
	}

	@Transactional
	public void delete(Likes like) {
		likesRepository.delete(like);

	}

	/**
	 * Likes a post.
	 *
	 * @param postId
	 *            the post id
	 * @param authorEmail
	 *            the author of the like emails.
	 * @return the succes/error message.
	 * @throws InstanceNotFoundException
	 *             the instance not found exception
	 */
	@Transactional(rollbackFor = InstanceNotFoundException.class)
	public String likePost(Long postId, String authorEmail) throws InstanceNotFoundException {
		Logger logger = Logger.getLogger(PictureService.class.getName());

		if (!userProfileRepository.exists(authorEmail)) {
			throw new InstanceNotFoundException("The user with that email doesn't exists.");
		}

		if (!postRepository.existsById(postId)) {
			throw new InstanceNotFoundException("The post with that id doesn't exists.");
		}

		Post post = postRepository.findById(postId).get();
		UserProfile author = userProfileRepository.findOneByEmail(authorEmail);
		String error_message = "";

		if (existLikes(author, post)) {
			error_message = FeedViewController.ALREADY_LIKED_POST_ERROR;
		} else {
			try {
				newLikes(author, post);
				post.setNumber_of_likes(post.getNumber_of_likes() + 1);
				postRepository.save(post);
			} catch (InstanceNotFoundException e) {
				logger.log(Level.INFO, e.getMessage(), e);
			} catch (AlreadyLikedException e) {
				logger.log(Level.INFO, e.getMessage(), e);
			}
			error_message = FeedViewController.SUCESS_LIKED_POST;
		}

		return error_message;
	}

	/**
	 * Unlike post.
	 *
	 * @param postId
	 *            the post id
	 * @param authorEmail
	 *            the author email
	 * @return the string
	 * @throws InstanceNotFoundException
	 *             the instance not found exception
	 * @throws NotLikedYetException
	 */
	@Transactional(rollbackFor = InstanceNotFoundException.class)
	public String unlikePost(Long postId, String authorEmail) throws InstanceNotFoundException, NotLikedYetException {
		Logger logger = Logger.getLogger(PictureService.class.getName());

		if (!userProfileRepository.exists(authorEmail)) {
			throw new InstanceNotFoundException("The user with that email doesn't exists.");
		}

		if (!postRepository.existsById(postId)) {
			throw new InstanceNotFoundException("The post with that id doesn't exists.");
		}

		Post post = postRepository.findById(postId).get();
		UserProfile author = userProfileRepository.findOneByEmail(authorEmail);
		String error_message = "";

		if (!existLikes(author, post)) {
			error_message = FeedViewController.POST_NOT_LIKED_YET_ERROR;
		} else {
			try {
				deleteUserPostLikes(author, post);
				post.setNumber_of_likes(post.getNumber_of_likes() - 1);
				postRepository.save(post);
			} catch (InstanceNotFoundException e) {
				logger.log(Level.INFO, e.getMessage(), e);
			} catch (NotLikedYetException e) {
				logger.log(Level.INFO, e.getMessage(), e);
			}
			error_message = FeedViewController.SUCESS_UNLIKED_POST;
		}

		return error_message;
	}

}
