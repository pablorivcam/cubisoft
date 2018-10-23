package es.udc.fi.dc.fd.service;

import java.util.List;

import javax.management.InstanceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.udc.fi.dc.fd.model.persistence.Follow;
import es.udc.fi.dc.fd.model.persistence.UserProfile;
import es.udc.fi.dc.fd.repository.FollowRepository;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class FollowService {

	/** The follow repository. */
	@Autowired
	private FollowRepository followRepository;

	/**
	 * Follow.
	 *
	 * @param user
	 *            the user
	 * @param followed_user
	 *            the followed_user
	 * @return the follow
	 */
	@Transactional
	public Follow follow(UserProfile user, UserProfile followed_user) {
		Follow follow = new Follow(user, followed_user);
		follow = followRepository.save(follow);
		return follow;
	}

	/**
	 * UnFollow.
	 *
	 * @param user
	 *            the user
	 * @param followed_user
	 *            the followed_user
	 */
	@Transactional
	public void unfollow(UserProfile user, UserProfile followed_user) {
		Follow follow = followRepository.findFollowByUsers(user, followed_user);
		followRepository.delete(follow);
	}

	/**
	 * Gets the user follows.
	 *
	 * @param user
	 *            the user
	 * @return the user follows
	 * @throws InstanceNotFoundException
	 *             the instance not found exception
	 */
	@Transactional
	public List<Follow> getUserFollows(UserProfile user) throws InstanceNotFoundException {
		return followRepository.findFollowsByUser(user);
	}

	/**
	 * Find follow by users.
	 *
	 * @param userA
	 *            the user A
	 * @param userB
	 *            the user B
	 * @return true, if successful
	 */
	public boolean isUserAFollowingUserB(UserProfile userA, UserProfile userB) {

		if (userA == null)
			throw new NullPointerException("The userA param cannot be null");
		if (userB == null)
			throw new NullPointerException("The userB param cannot be null.");

		Follow f = followRepository.findFollowByUsers(userA, userB);
		return f != null;

	}

}
