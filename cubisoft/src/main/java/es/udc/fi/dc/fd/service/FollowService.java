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
	 * @param user          the user
	 * @param followed_user the followed_user
	 * @return the follow
	 */
	@Transactional
	public Follow follow(UserProfile user, UserProfile followed_user) {
		Follow follow = new Follow(user, followed_user);
		followRepository.save(follow);
		return follow;
	}

	/**
	 * UnFollow.
	 *
	 * @param user          the user
	 * @param followed_user the followed_user
	 */
	@Transactional
	public void unfollow(UserProfile user, UserProfile followed_user) {
		Follow follow = followRepository.findFollowByUsers(user, followed_user);
		followRepository.delete(follow);
	}

	@Transactional
	public List<UserProfile> getUserFollowedProfiles(UserProfile user) throws InstanceNotFoundException {
		return followRepository.findFollowedProfilesByUser(user);
	}

}
