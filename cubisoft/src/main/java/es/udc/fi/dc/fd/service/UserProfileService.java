package es.udc.fi.dc.fd.service;

import java.util.Collections;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
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

import es.udc.fi.dc.fd.config.SecurityConfig;
import es.udc.fi.dc.fd.model.persistence.UserProfile;
import es.udc.fi.dc.fd.repository.UserProfileRepository;

/**
 * The Class UserProfileService.
 */
@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserProfileService implements UserDetailsService {

	/** The user profile repository. */
	@Autowired
	private UserProfileRepository userProfileRepository;

	/** The password encoder. */
	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * Initialize method to add the data the first time to the DB.
	 */
	@PostConstruct
	protected void initialize() {

		if (userProfileRepository.findOneByEmail("admin@admin.com") == null) {
			save(new UserProfile("admin", "Admin", "Admin", "admin", "admin@admin.com"));
		}
		if (userProfileRepository.findOneByEmail("user@user.com") == null) {
			save(new UserProfile("user", "User", "User", "user", "user@user.com"));
		}

	}

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
		userProfileRepository.save(userProfile);
		return userProfile;
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
		UserProfile account = userProfileRepository.findOneByEmail(email);
		return account;
	}

	public void signin(UserProfile account) {
		SecurityContextHolder.getContext().setAuthentication(authenticate(account));
	}

	private User createUser(UserProfile account) {
		return new User(account.getEmail(), account.getPassword(), Collections.singleton(createAuthority(account)));
	}

	private Authentication authenticate(UserProfile account) {
		return new UsernamePasswordAuthenticationToken(createUser(account), null,
				Collections.singleton(createAuthority(account)));
	}

	private GrantedAuthority createAuthority(UserProfile account) {
		return new SimpleGrantedAuthority(SecurityConfig.DEFAULT_ROLE);
	}

}
