package es.udc.fi.dc.fd.model.persistence;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * The Class UserProfile.
 * 
 * @author Pablo Rivas
 */
@Entity
public class UserProfile {

	/** The user id. */
	private Long user_id = -1L;

	/** The login. */
	private String login;

	/** The first name. */
	private String firstName;

	/** The last name. */
	private String lastName;

	/** The password. */
	private String password;

	/** The email. */
	private String email;

	/** The follows. */
	private List<Follow> follows;

	/** The posts. */
	private List<Post> posts;

	/** The possible types of the users. */
	public enum UserType {
		PUBLIC, PRIVATE
	}

	/** Type of the user. */
	private UserType userType;

	/**
	 * Instantiates a new user profile.
	 */
	public UserProfile() {
	}

	/**
	 * Instantiates a new user profile.
	 *
	 * @param login
	 *            the login
	 * @param firstName
	 *            the first name
	 * @param lastName
	 *            the last name
	 * @param password
	 *            the password
	 * @param email
	 *            the email
	 * @param follows
	 *            the follows
	 * @param posts
	 *            the posts
	 * @param userType
	 *            the type of the user
	 */
	public UserProfile(String login, String firstName, String lastName, String password, String email,
			List<Follow> follows, List<Post> posts, UserType userType) {
		this.login = login;
		this.firstName = firstName;
		this.lastName = lastName;
		this.password = password;
		this.email = email;
		this.follows = follows;
		this.posts = posts;
		this.userType = userType;
	}

	/**
	 * Gets the user id.
	 *
	 * @return the user id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, unique = true)
	public long getUser_id() {
		return user_id;
	}

	/**
	 * Gets the login.
	 *
	 * @return the login
	 */
	@Column(nullable = false, unique = true)
	public String getLogin() {
		return login;
	}

	/**
	 * Gets the first name.
	 *
	 * @return the first name
	 */
	@Column(nullable = false)
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Gets the last name.
	 *
	 * @return the last name
	 */
	@Column(nullable = false)
	public String getLastName() {
		return lastName;
	}

	/**
	 * Gets the password.
	 *
	 * @return the password
	 */
	@Column(nullable = false)
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the login.
	 *
	 * @param login
	 *            the new login
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * Sets the first name.
	 *
	 * @param firstName
	 *            the new first name
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Sets the last name.
	 *
	 * @param lastName
	 *            the new last name
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * Sets the password.
	 *
	 * @param password
	 *            the new password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Gets the email.
	 *
	 * @return the email
	 */
	@Column(nullable = false, unique = true)
	public String getEmail() {
		return email;
	}

	/**
	 * Sets the email.
	 *
	 * @param email
	 *            the new email
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Sets the user id.
	 *
	 * @param user_id
	 *            the new user id
	 */
	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	/**
	 * Gets the follows.
	 *
	 * @return the follows
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
	public List<Follow> getFollows() {
		return follows;
	}

	/**
	 * Sets the follows.
	 *
	 * @param follows
	 *            the new follows
	 */
	public void setFollows(List<Follow> follows) {
		this.follows = follows;
	}

	/**
	 * Gets the posts.
	 *
	 * @return the posts
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "post_id")
	public List<Post> getPosts() {
		return posts;
	}

	/**
	 * Sets the posts.
	 *
	 * @param posts
	 *            the new posts
	 */
	public void setPosts(List<Post> posts) {
		this.posts = posts;
	}

	/**
	 * @return the userType
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public UserType getUserType() {
		return userType;
	}

	/**
	 * @param userType
	 *            the userType to set
	 */
	public void setUserType(UserType userType) {
		this.userType = userType;
	}

}
