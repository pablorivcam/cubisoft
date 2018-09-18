package es.udc.fi.dc.fd.model.userprofile;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * The Class UserProfile.
 * 
 * @author Pablo Rivas
 */
@Entity
public class UserProfile {

	/** The user id. */
	private long user_id = -1;

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
	 */
	public UserProfile(String login, String firstName, String lastName, String password, String email) {
		this.login = login;
		this.firstName = firstName;
		this.lastName = lastName;
		this.password = password;
		this.email = email;
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

}
