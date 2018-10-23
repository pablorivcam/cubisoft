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

	/**
	 * Sets a like from the user to the post and returns it
	 * @param user the author of the like
	 * @param post the post to give a like
	 * @return the like
	 * @throws InstanceNotFoundException
	 * @throws AlreadyLikedException 
	 */
	@Transactional
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
		try {
			likesRepository.save(like);	
		}catch(Exception e) {
			throw new AlreadyLikedException("The post has been already liked");
		}
		
		return like;
	}

	/**
	 * Returns the user likes
	 * @param user the owner of the likes
	 * @return the list of likes
	 * @throws InstanceNotFoundException
	 */
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

	/**
	 * Returns the post likes
	 * @param post the post to find likes for
	 * @return the list of likes
	 * @throws InstanceNotFoundException
	 */
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

	/**
	 * Unlikes the post for the designed user
	 * @param user the author of the like
	 * @param post the liked post
	 * @throws InstanceNotFoundException
	 * @throws AlreadyLikedException 
	 */
	@Transactional
	public void deleteUserPostLikes(UserProfile user, Post post) throws InstanceNotFoundException, NotLikedYetException {
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
		Likes like = new Likes();
		try {
			like = likesRepository.findLikesByUserAndPost(user, post);
		}catch(Error e) {
			throw new NotLikedYetException("The post is not liked yet");
		}
		likesRepository.delete(like);
	}
	
	@Transactional
	public boolean existLikes(UserProfile user, Post post){
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
}
