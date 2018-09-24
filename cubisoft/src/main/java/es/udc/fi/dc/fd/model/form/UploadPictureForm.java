package es.udc.fi.dc.fd.model.form;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.base.MoreObjects;

/**
 * The Class UploadPictureForm. Class that encapsules the upload picture form.
 */
public class UploadPictureForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The picture file. */
	@NotEmpty
	private MultipartFile pictureFile;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		if (pictureFile == null) {
			if (other.pictureFile != null)
				return false;
		} else if (!pictureFile.equals(other.pictureFile))
			return false;
		return true;
	}

	@Override
	public final String toString() {
		return MoreObjects.toStringHelper(this).add("pictureFile", pictureFile).toString();
	}

}
