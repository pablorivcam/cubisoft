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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The Class Picture. Represents a picture uploaded by the user on the
 * application.
 */
@Entity
public class Picture {

	/** The picture id. */
	private Long picture_id = -1L;

	/** The description. */
	private String description;

	/** The date. */
	private Calendar date;

	/** The image path. */
	private String image_path;

	private UserProfile author;

	public Picture() {

	}

	/**
	 * Instantiates a new picture.
	 *
	 * @param description
	 *            the description
	 * @param date
	 *            the date
	 * @param image_path
	 *            the image path
	 * @param author
	 *            the author
	 */
	public Picture(String description, Calendar date, String image_path, UserProfile author) {
		this.description = description;
		this.date = date;
		this.image_path = image_path;
		this.author = author;
	}

	/**
	 * Gets the picture id.
	 *
	 * @return the picture id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, unique = true)
	public long getPicture_id() {
		return picture_id;
	}

	/**
	 * Sets the picture id.
	 *
	 * @param picture_id
	 *            the new picture id
	 */
	public void setPicture_id(long picture_id) {
		this.picture_id = picture_id;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	@Column(nullable = true)
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 *
	 * @param description
	 *            the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the date.
	 *
	 * @return the date
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
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
	 * Gets the image path.
	 *
	 * @return the image path
	 */
	@Column(nullable = false)
	public String getImage_path() {
		return image_path;
	}

	/**
	 * Sets the image path.
	 *
	 * @param image_path
	 *            the new image path
	 */
	public void setImage_path(String image_path) {
		this.image_path = image_path;
	}

	/**
	 * Gets the author.
	 *
	 * @return the author
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	public UserProfile getAuthor() {
		return author;
	}

	/**
	 * Sets the author.
	 *
	 * @param author
	 *            the new author
	 */
	public void setAuthor(UserProfile author) {
		this.author = author;
	}

}
