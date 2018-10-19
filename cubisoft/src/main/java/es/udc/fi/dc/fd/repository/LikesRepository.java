package es.udc.fi.dc.fd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import es.udc.fi.dc.fd.model.persistence.Likes;
import es.udc.fi.dc.fd.model.persistence.Post;
import es.udc.fi.dc.fd.model.persistence.UserProfile;

/**
 * The Interface PictureRepository.
 */
@Repository("likesRepository")
public interface LikesRepository extends JpaRepository<Likes, Long> {

	/**
	 * Find likes by post.
	 *
	 * @param post the post
	 * @return the list of likes
	 */
	@Query("select l from Likes l where l.post=:post")
	List<Likes> findLikesByPost(@Param("post") Post post);

	/**
	 * Find likes by user.
	 *
	 * @param user the user profile
	 * @return the list of likes
	 */
	@Query("select l from Likes l where l.user=:user")
	List<Likes> findLikesByUser(@Param("user") UserProfile user);

}
