package es.udc.fi.dc.fd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import es.udc.fi.dc.fd.model.persistence.Picture;
import es.udc.fi.dc.fd.model.persistence.UserProfile;

/**
 * The Interface PictureRepository.
 */
@Repository("pictureRepository")
public interface PictureRepository extends JpaRepository<Picture, Long> {

	/**
	 * Find pictures by author.
	 *
	 * @param author
	 *            the author
	 * @return the list
	 */
	@Query("select p from Picture p where p.author=:author")
	List<Picture> findPicturesByAuthor(@Param("author") UserProfile author);

}
