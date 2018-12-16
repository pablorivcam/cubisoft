package es.udc.fi.dc.fd.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.udc.fi.dc.fd.model.Block;
import es.udc.fi.dc.fd.model.persistence.UserProfile;
import es.udc.fi.dc.fd.model.persistence.UserProfile.UserType;
import es.udc.fi.dc.fd.repository.UserProfileRepository;

/**
 * The Class UserProfileService.
 */
@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserProfileService implements UserDetailsService {

	public static final String DEFAULT_ROLE = "USER";
	
	/** The user profile repository. */
	@Autowired
	private UserProfileRepository userProfileRepository;
	/** The password encoder. */
	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * Save.
	 *
	 * @param userProfile
	 *            the user profile
	 * @return the user profile
	 */
	@Transactional
	public UserProfile save(UserProfile userProfile) {
		userProfile.setPassword(passwordEncoder.encode(userProfile.getPassword()));
		return userProfileRepository.save(userProfile);
	}

	/**
	 * Finds an user by email.
	 *
	 * @param email
	 *            the email
	 * @return the user profile
	 */
	public UserProfile findUserByEmail(String email) {
		return userProfileRepository.findOneByEmail(email);
	}

	/**
	 * Find by id.
	 *
	 * @param id
	 *            the id
	 * @return the user profile
	 */
	public UserProfile findById(Long id) {
		return userProfileRepository.findById(id).get();
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserProfile account = userProfileRepository.findOneByEmail(username);
		if (account == null) {
			throw new UsernameNotFoundException("user not found");
		}
		return createUser(account);
	}

	public UserProfile validateUser(String email, String password) {
		return userProfileRepository.findOneByEmail(email);
	}

	public void signin(UserProfile account) {
		SecurityContextHolder.getContext().setAuthentication(authenticate(account));
	}

	private User createUser(UserProfile account) {
		return new User(account.getEmail(), account.getPassword(), Collections.singleton(createAuthority()));
	}

	private Authentication authenticate(UserProfile account) {
		return new UsernamePasswordAuthenticationToken(createUser(account), null,
				Collections.singleton(createAuthority()));
	}

	private GrantedAuthority createAuthority() {
		return new SimpleGrantedAuthority(DEFAULT_ROLE);
	}

	/**
	 * Find user profile by keywords.
	 *
	 * @param keywords
	 *            the keywords
	 * @param startIndex
	 *            the start index
	 * @param count
	 *            the count
	 * @return the array list
	 */
	public Block<UserProfile> findUserProfileByKeywords(String keywords, int startIndex, int count) {

		Page<UserProfile> users = userProfileRepository.findUserProfileByKeywords(keywords,
				PageRequest.of(startIndex, count));

		return new Block<>(users.getContent(), users.hasNext());
	}

	public UserProfile changeUserProfileType(UserProfile account, UserType userType) {
		account.setUserType(userType);
		return userProfileRepository.save(account);
	}
}
