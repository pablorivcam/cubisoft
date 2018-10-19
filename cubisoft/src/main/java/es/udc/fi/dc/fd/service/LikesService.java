package es.udc.fi.dc.fd.service;

import java.util.List;

import javax.management.InstanceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
		Likes l = likesRepository.save(likes);
		return l;
	}

	@Transactional
	public Likes newLikes(UserProfile user, Post post) throws InstanceNotFoundException {
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
		likesRepository.save(like);
		return like;
	}

	// TODO revisar si esto necesita más comprobaciones
	@Transactional
	public List<Likes> findUserLikes(UserProfile user) throws InstanceNotFoundException {
		if (user == null) {
			throw new NullPointerException("The user param cannot be null.");
		}
		if (!userProfileRepository.exists(user.getEmail())) {
			throw new InstanceNotFoundException("The user with the id" + user.getUser_id() + " doesnt exists.");
		}
		return likesRepository.findLikesByUser(user);
	}

	// TODO revisar si esto necesita más comprobaciones
	@Transactional
	public List<Likes> findPostLikes(Post post) throws InstanceNotFoundException {
		if (post == null) {
			throw new NullPointerException("The post param cannot be null.");
		}
		if (!postRepository.existsById(post.getPost_id())) {
			throw new InstanceNotFoundException("The post with the id" + post.getPost_id() + " doesnt exists.");
		}
		return likesRepository.findLikesByPost(post);
	}
}
