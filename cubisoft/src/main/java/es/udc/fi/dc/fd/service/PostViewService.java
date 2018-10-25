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
		PostView view = postViewRepository.save(postView);
		return view;
	}

	@Transactional
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

	@Transactional
	public List<PostView> findPostView(Post post) throws InstanceNotFoundException {
		if (post == null) {
			throw new NullPointerException("The post param cannot be null.");
		}
		if (!postRepository.existsById(post.getPost_id())) {
			throw new InstanceNotFoundException("The post with the id" + post.getPost_id() + " doesnt exists.");
		}
		return postViewRepository.findViewsByPost(post);
	}

	@Transactional
	public List<List<PostView>> findPostsViews(List<Post> posts) throws InstanceNotFoundException {
		List<List<PostView>> viewsPosts = new ArrayList<List<PostView>>();

		if (posts == null) {
			throw new NullPointerException("The posts param cannot be null.");
		}
		for (Post post : posts) {
			viewsPosts.add(postViewRepository.findViewsByPost(post));
		}
		return viewsPosts;
	}
}
