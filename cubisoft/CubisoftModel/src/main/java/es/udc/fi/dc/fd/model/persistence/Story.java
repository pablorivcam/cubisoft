package es.udc.fi.dc.fd.model.persistence;

import java.util.Calendar;

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
 * The Class Post. Class that model the posts shared by the users.
 */
@Entity
public class Story {

	/** The story id. */
	private Long story_id = -1L;

	/** The date. */
	private Calendar expiration;

	/** The picture. */
	private Picture picture;

	/** The user. */
	private UserProfile user;

	/**
	 * Instantiates a new story.
	 */
	public Story() {
	}

	/**
	 * Instantiates a new story.
	 *
	 * @param expirationDate the expiration date
	 * @param picture        the picture
	 * @param user           the user
	 */
	public Story(Calendar expiration, Picture picture, UserProfile user) {
		this.expiration = expiration;
		this.picture = picture;
		this.user = user;
		expiration.set(Calendar.MILLISECOND, 0);

	}

	/**
	 * Gets the story id.
	 *
	 * @return the story id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, unique = true)
	public Long getStory_id() {
		return story_id;
	}

	/**
	 * Sets the story id.
	 *
	 * @param post_id the new story id
	 */
	public void setStory_id(Long story_id) {
		this.story_id = story_id;
	}

	/**
	 * Gets the expiration date.
	 *
	 * @return the expiration date
	 */
	@Column(nullable = false)
	public Calendar getExpiration() {
		return expiration;
	}

	/**
	 * Sets the expiration date.
	 *
	 * @param date the new expiration date
	 */
	public void setExpiration(Calendar expiration) {
		this.expiration = expiration;
	}

	/**
	 * Gets the picture.
	 *
	 * @return the picture
	 */
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "picture_id")
	public Picture getPicture() {
		return picture;
	}

	/**
	 * Sets the picture.
	 *
	 * @param picture the new picture
	 */
	public void setPicture(Picture picture) {
		this.picture = picture;
	}

	/**
	 * Gets the user.
	 *
	 * @return the user
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	public UserProfile getUser() {
		return user;
	}

	/**
	 * Sets the user.
	 *
	 * @param user the new user
	 */
	public void setUser(UserProfile user) {
		this.user = user;
	}

}
