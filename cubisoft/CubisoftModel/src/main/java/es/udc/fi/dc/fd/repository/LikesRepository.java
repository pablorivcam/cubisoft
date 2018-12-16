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
 * The Interface LikesRepository.
 */
@Repository("likesRepository")
public interface LikesRepository extends JpaRepository<Likes, Long> {

	/**
	 * Find likes by post.
	 *
	 * @param post
	 *            the post
	 * @return the list of likes
	 */
	@Query("select l from Likes l where l.post=:post")
	List<Likes> findLikesByPost(@Param("post") Post post);

	/**
	 * Find likes by user.
	 *
	 * @param user
	 *            the user profile
	 * @return the list of likes
	 */
	@Query("select l from Likes l where l.user=:user")
	List<Likes> findLikesByUser(@Param("user") UserProfile user);

	/**
	 * Find one by likes id.
	 *
	 * @param likes_id
	 *            the likes id
	 * @return the "likes" (only 1)
	 */
	@Query("SELECT l FROM Likes l where l.likes_id=:likes_id")
	Likes findLikesById(@Param("likes_id") Long likes_id);

	/**
	 * Find one by likes id.
	 *
	 * @param user
	 *            the user
	 * @param post
	 *            the post
	 * @return the "likes" (only 1)
	 */
	@Query("SELECT l FROM Likes l where l.user=:user and l.post=:post")
	Likes findLikesByUserAndPost(@Param("user") UserProfile user, @Param("post") Post post);

}
