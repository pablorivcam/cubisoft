package es.udc.fi.dc.fd.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserProfileService implements UserDetailsService {

	@Autowired
	private UserProfileRepository userProfileRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Transactional
	public UserProfile save(UserProfile userProfile) {
		userProfile.setPassword(passwordEncoder.encode(userProfile.getPassword()));
		userProfileRepository.save(userProfile);
		return userProfile;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserProfile account = userProfileRepository.findByEmail(username);
		if (account == null) {
			throw new UsernameNotFoundException("user not found");
		}
		return createUser(account);
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
