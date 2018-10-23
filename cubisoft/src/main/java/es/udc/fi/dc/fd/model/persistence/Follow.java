package es.udc.fi.dc.fd.model.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 * The Class Follow.
 */
@Entity
public class Follow {

	/** The follow id. */
	private Long follow_id = -1L;

	/** The user. */
	private UserProfile user;

	/** The followed user. */
	private UserProfile followed_user;

	/**
	 * Instantiates a new follow.
	 */
	public Follow() {
	}

	/**
	 * Instantiates a new follow.
	 *
	 * @param user
	 *            the user
	 * @param followed_user
	 *            the followed user
	 */
	public Follow(UserProfile user, UserProfile followed_user) {
		this.user = user;
		this.followed_user = followed_user;
	}

	/**
	 * Gets the follow id.
	 *
	 * @return the follow id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, unique = true)
	public Long getFollow_id() {
		return follow_id;
	}

	/**
	 * Sets the follow id.
	 *
	 * @param follow_id
	 *            the new follow id
	 */
	public void setFollow_id(Long follow_id) {
		this.follow_id = follow_id;
	}

	/**
	 * Gets the user.
	 *
	 * @return the user
	 */
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user")
	public UserProfile getUser() {
		return user;
	}

	/**
	 * Sets the user.
	 *
	 * @param user
	 *            the new user
	 */
	public void setUser(UserProfile user) {
		this.user = user;
	}

	/**
	 * Gets the followed user.
	 *
	 * @return the followed user
	 */
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "followed_user")
	public UserProfile getFollowed_user() {
		return followed_user;
	}

	/**
	 * Sets the followed user.
	 *
	 * @param followed_user
	 *            the new followed user
	 */
	public void setFollowed_user(UserProfile followed_user) {
		this.followed_user = followed_user;
	}

}
