package es.udc.fi.dc.fd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import es.udc.fi.dc.fd.model.persistence.Post;
import es.udc.fi.dc.fd.model.persistence.UserProfile;

/**
 * The Interface PostRepository to access to the Post data stored.
 */
@Repository("postRepository")
public interface PostRepository extends JpaRepository<Post, Long> {

	/**
	 * Find user follows posts.
	 *
	 * @param user
	 *            the user
	 * @return the list
	 */

	@Query("SELECT p FROM Post p WHERE p.user "
			+ "IN (SELECT f.followed_user FROM Follow f WHERE f.user=:user AND f.pending = FALSE) ORDER BY p.date DESC")
	List<Post> findUserFollowsPosts(@Param("user") UserProfile user);

	/**
	 * Find user posts.
	 *
	 * @param user
	 *            the user
	 * @return the list
	 */
	@Query("SELECT p FROM Post p WHERE p.user=:user ORDER BY p.date DESC")
	List<Post> findUserPosts(@Param("user") UserProfile user);

	@Query("SELECT p FROM Post p WHERE p.user "
			+ "IN (SELECT f.followed_user FROM Follow f WHERE f.user=:user AND f.pending = FALSE)"
			+ "OR p.user=:user ORDER BY p.date DESC")
	List<Post> findFollowsAndUserPosts(@Param("user") UserProfile user);

	/**
	 * Find one by post id.
	 *
	 * @param post_id
	 *            the post id
	 * @return the post
	 */
	@Query("SELECT p FROM Post p where p.post_id=:post_id")
	Post findPostByPostId(@Param("post_id") Long post_id);
}
