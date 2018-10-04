package es.udc.fi.dc.fd.service;

import java.util.List;

import javax.management.InstanceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.udc.fi.dc.fd.model.persistence.Post;
import es.udc.fi.dc.fd.model.persistence.UserProfile;
import es.udc.fi.dc.fd.repository.PostRepository;
import es.udc.fi.dc.fd.repository.UserProfileRepository;

/**
 * The Class PostService. Class that implments the Post features.
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
		postRepository.save(post);
		return post;
	}

	/**
	 * Find user's followers posts.
	 *
	 * @param user
	 *            the user
	 * @return the list
	 * @throws InstanceNotFoundException
	 *             the instance not found exception
	 * @throws NullPointerException
	 *             the null pointer exception
	 */
	@Transactional
	public List<Post> findUserFollowersPosts(UserProfile user) throws InstanceNotFoundException, NullPointerException {

		if (user == null) {
			throw new NullPointerException("The user param cannot be null.");
		}
		if (!userProfileRepository.exists(user.getEmail())) {
			throw new InstanceNotFoundException("The user with the mail" + user.getEmail() + " doesnt exists.");
		}

		return postRepository.findUserFollowersPosts(user);
	}

}
