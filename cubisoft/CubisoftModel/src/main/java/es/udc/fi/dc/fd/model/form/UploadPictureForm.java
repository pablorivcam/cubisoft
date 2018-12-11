package es.udc.fi.dc.fd.model.form;

import java.io.Serializable;

import org.springframework.web.multipart.MultipartFile;

/**
 * The Class UploadPictureForm. Class that encapsules the upload picture form.
 */
public class UploadPictureForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The picture file. */
	private MultipartFile pictureFile;

	/** The description. */
	private String description;

	private int expiration;

	public UploadPictureForm() {
	}

	/**
	 * Gets the picture file.
	 *
	 * @return the picture file
	 */
	public MultipartFile getPictureFile() {
		return pictureFile;
	}

	/**
	 * Sets the picture file.
	 *
	 * @param pictureFile
	 *            the new picture file
	 */
	public void setPictureFile(MultipartFile pictureFile) {
		this.pictureFile = pictureFile;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
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
	 * Gets the expiration time.
	 *
	 * @return the expiration time
	 */
	public int getExpiration() {
		return expiration;
	}

	/**
	 * Sets the expiration time.
	 *
	 * @param expiration
	 *            the new expirationTime
	 */
	public void setExpiration(int expiration) {
		this.expiration = expiration;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((pictureFile == null) ? 0 : pictureFile.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UploadPictureForm other = (UploadPictureForm) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (pictureFile == null) {
			if (other.pictureFile != null)
				return false;
		} else if (!pictureFile.equals(other.pictureFile))
			return false;
		return true;
	}

}
