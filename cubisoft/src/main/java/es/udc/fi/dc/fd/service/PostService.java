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
		Post p = postRepository.save(post);
		return p;
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
	@Transactional
	public List<Post> findUserFollowersPosts(UserProfile user) throws InstanceNotFoundException {

		if (user == null) {
			throw new NullPointerException("The user param cannot be null.");
		}
		if (!userProfileRepository.exists(user.getEmail())) {
			throw new InstanceNotFoundException("The user with the mail" + user.getEmail() + " doesnt exists.");
		}

		return postRepository.findUserFollowersPosts(user);
	}

	/**
	 * Find user posts.
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

	/**
	 * NewPost.
	 *
	 * @param picture
	 *            the picture
	 * @param user
	 *            the user
	 * @return post
	 * @throws InstanceNotFoundException
	 *             the instance not found exception
	 */
	@Transactional
	public Post newPost(Picture picture, UserProfile user) throws InstanceNotFoundException {
		if (user == null) {
			throw new NullPointerException("The user param cannot be null.");
		}
		if (!userProfileRepository.exists(user.getEmail())) {
			throw new InstanceNotFoundException("The user with email" + user.getEmail() + " doesnt exist.");
		}
		Post post = new Post(Calendar.getInstance(), picture, user, (long) 0);
		postRepository.save(post);
		return post;
	}

	/**
	 * DeletePost.
	 *
	 * @param post
	 *            the post
	 */
	@Transactional
	public void deletePost(Post post) {
		postRepository.delete(post);
	}

}
