package es.udc.fi.dc.fd.service;

import java.util.Calendar;
import java.util.List;

import javax.management.InstanceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.udc.fi.dc.fd.model.persistence.Picture;
import es.udc.fi.dc.fd.model.persistence.Post;
import es.udc.fi.dc.fd.model.persistence.UserProfile;
import es.udc.fi.dc.fd.repository.PostRepository;
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
	@Transactional
	public List<Post> findUserPosts(UserProfile user) throws InstanceNotFoundException {
		if (user == null) {
			throw new NullPointerException("The user param cannot be null.");
		}
		if (!userProfileRepository.exists(user.getEmail())) {
			throw new InstanceNotFoundException("The user with the mail" + user.getEmail() + " doesnt exists.");
		}
		return postRepository.findUserPosts(user);
	}

	@Transactional
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
	 * @param post
	 *            the post
	 */
	@Transactional
	public void deletePost(Post post) {
		postRepository.delete(post);
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

}
