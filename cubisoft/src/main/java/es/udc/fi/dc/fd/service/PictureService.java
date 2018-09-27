package es.udc.fi.dc.fd.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.udc.fi.dc.fd.model.persistence.Picture;
import es.udc.fi.dc.fd.model.persistence.UserProfile;
import es.udc.fi.dc.fd.repository.PictureRepository;

/**
 * The Class PictureService.
 */
@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class PictureService {

	/** The picture repository. */
	@Autowired
	private PictureRepository pictureRepository;

	/**
	 * Save a picture into the database.
	 *
	 * @param picture
	 *            the picture
	 * @return the picture
	 */
	@Transactional
	public Picture save(Picture picture) {
		pictureRepository.save(picture);
		return picture;
	}

	/**
	 * Gets the pictures by author.
	 *
	 * @param author
	 *            the author
	 * @return the pictures by author
	 */
	public List<Picture> getPicturesByAuthor(UserProfile author) {

		return pictureRepository.findPicturesByAuthor(author);

	}

}
