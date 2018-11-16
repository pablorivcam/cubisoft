package es.udc.fi.dc.fd.service;

import java.util.ArrayList;
import java.util.List;

import javax.management.InstanceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.udc.fi.dc.fd.model.persistence.Post;
import es.udc.fi.dc.fd.model.persistence.PostView;
import es.udc.fi.dc.fd.model.persistence.UserProfile;
import es.udc.fi.dc.fd.repository.PostRepository;
import es.udc.fi.dc.fd.repository.PostViewRepository;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class PostViewService {

	@Autowired
	private PostViewRepository postViewRepository;
	@Autowired
	private PostRepository postRepository;

	/**
	 * Save view into the database.
	 *
	 * @param postView
	 *            the view
	 * @return the view
	 */
	@Transactional
	public PostView save(PostView postView) {
		return postViewRepository.save(postView);
	}

	/**
	 * Find post view by post user.
	 *
	 * @param post
	 *            the post
	 * @param user
	 *            the user
	 * @return the post view
	 * @throws InstanceNotFoundException
	 *             the instance not found exception
	 */
	@Transactional(noRollbackFor = Exception.class)
	public PostView findPostViewByPostUser(Post post, UserProfile user) throws InstanceNotFoundException {
		if (post == null) {
			throw new NullPointerException("The post param cannot be null.");
		}
		if (user == null) {
			throw new NullPointerException("The user param cannot be null.");
		}
		if (!postRepository.existsById(post.getPost_id())) {
			throw new InstanceNotFoundException("The post with the id" + post.getPost_id() + " doesnt exists.");
		}
		return postViewRepository.findPostView(post, user);
	}

	/**
	 * Find post view.
	 *
	 * @param post
	 *            the post
	 * @return the list
	 * @throws InstanceNotFoundException
	 *             the instance not found exception
	 */
	@Transactional(noRollbackFor = Exception.class)
	public List<PostView> findPostView(Post post) throws InstanceNotFoundException {
		if (post == null) {
			throw new NullPointerException("The post param cannot be null.");
		}
		if (!postRepository.existsById(post.getPost_id())) {
			throw new InstanceNotFoundException("The post with the id" + post.getPost_id() + " doesnt exists.");
		}
		return postViewRepository.findViewsByPost(post);
	}

	/**
	 * Find posts views.
	 *
	 * @param posts
	 *            the posts
	 * @return the list
	 * @throws NullPointerException
	 *             the null pointer exception
	 */
	public List<PostView> findPostsViews(List<Post> posts) throws NullPointerException {
		List<PostView> viewsPosts = new ArrayList<PostView>();

		if (posts == null) {
			throw new NullPointerException("The posts param cannot be null.");
		}
		for (Post post : posts) {
			viewsPosts.addAll(postViewRepository.findViewsByPost(post));
		}
		return viewsPosts;
	}
}
