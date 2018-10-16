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
public class Post {

	/** The post id. */
	private Long post_id = -1L;

	/** The date. */
	private Calendar date;

	/** The picture. */
	private Picture picture;

	/** The user. */
	private UserProfile user;

	/**
	 * Instantiates a new post.
	 */
	public Post() {
	}

	/**
	 * Instantiates a new post.
	 *
	 * @param date
	 *            the date
	 * @param picture
	 *            the picture
	 * @param user
	 *            the user
	 */
	public Post(Calendar date, Picture picture, UserProfile user) {
		this.date = date;
		this.picture = picture;
		this.user = user;
		date.set(Calendar.MILLISECOND, 0);

	}

	/**
	 * Gets the post id.
	 *
	 * @return the post id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, unique = true)
	public Long getPost_id() {
		return post_id;
	}

	/**
	 * Sets the post id.
	 *
	 * @param post_id
	 *            the new post id
	 */
	public void setPost_id(Long post_id) {
		this.post_id = post_id;
	}

	/**
	 * Gets the date.
	 *
	 * @return the date
	 */
	@Column(nullable = true)
	public Calendar getDate() {
		return date;
	}

	/**
	 * Sets the date.
	 *
	 * @param date
	 *            the new date
	 */
	public void setDate(Calendar date) {
		this.date = date;
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
	 * @param picture
	 *            the new picture
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

}
