package es.udc.fi.dc.fd.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

import es.udc.fi.dc.fd.service.UserProfileService;

/**
 * The Class SecurityConfig. Encargada de controlar el acceso de los usuarios a
 * las diferentes páginas
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	/** The Constant DEFAULT_ROLE. */
	public static final String DEFAULT_ROLE = "USER";

	/** The user profile service. */
	@Autowired
	private UserProfileService userProfileService;

	@Bean
	public TokenBasedRememberMeServices rememberMeServices() {
		return new TokenBasedRememberMeServices("remember-me-key", userProfileService);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.eraseCredentials(true).userDetailsService(userProfileService).passwordEncoder(passwordEncoder());
	}

	/**
	 * Here we configure what resources and pages can be used by everyone or by
	 * authenticated users.
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/", "/rest/**", "/favicon.ico", "/resources/**", "/signup", "/static/**", "/about",
						"/account/list", "/Pictures/**", "/post/myFeed")
				.permitAll().anyRequest().authenticated().and().formLogin().loginPage("/signin").permitAll()
				.failureUrl("/signin?error=1").loginProcessingUrl("/authenticate").and().logout().logoutUrl("/logout")
				.permitAll().logoutSuccessUrl("/signin?logout").and().rememberMe()
				.rememberMeServices(rememberMeServices()).key("remember-me-key");
	}

	@Bean(name = "authenticationManager")
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	@Override
	public UserDetailsService userDetailsService() {
		UserDetails user = User.withUsername("email").password("password").roles(DEFAULT_ROLE).build();
		return new InMemoryUserDetailsManager(user);
	}
}