package es.udc.fi.dc.fd.service;

import java.util.List;

import javax.management.InstanceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.udc.fi.dc.fd.model.persistence.Picture;
import es.udc.fi.dc.fd.model.persistence.UserProfile;
import es.udc.fi.dc.fd.repository.PictureRepository;
import es.udc.fi.dc.fd.repository.UserProfileRepository;

/**
 * The Class PictureService.
 */
@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class PictureService {

	/** The picture repository. */
	@Autowired
	private PictureRepository pictureRepository;

	@Autowired
	private UserProfileRepository userProfileRepository;

	/**
	 * Save a picture into the database.
	 *
	 * @param picture
	 *            the picture
	 * @return the picture
	 */
	@Transactional
	public Picture save(Picture picture) {
		Picture p = pictureRepository.save(picture);
		return p;
	}

	/**
	 * Gets the pictures by author.
	 *
	 * @param author
	 *            the author
	 * @return the pictures by author
	 * @throws InstanceNotFoundException
	 *             the instance not found exception
	 */
	public List<Picture> getPicturesByAuthor(UserProfile author) throws InstanceNotFoundException {

		if (author == null) {
			throw new NullPointerException("The author param cannot be null.");
		}

		if (!userProfileRepository.exists(author.getEmail())) {
			throw new InstanceNotFoundException("The user with the email" + author.getEmail() + " doestn exist.");
		}

		return pictureRepository.findPicturesByAuthor(author);

	}

}
