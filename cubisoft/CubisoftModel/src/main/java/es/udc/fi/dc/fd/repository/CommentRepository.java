package es.udc.fi.dc.fd.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import es.udc.fi.dc.fd.model.persistence.Comment;
import es.udc.fi.dc.fd.model.persistence.Post;

@Repository("commentRepository")
public interface CommentRepository extends JpaRepository<Comment, Long> {

	/**
	 * Find comments by post.
	 *
	 * @param post
	 *            the post
	 * @param pageable
	 *            the pageable
	 * @return the list of comments
	 */
	@Query("SELECT c FROM Comment c WHERE c.post=:post ORDER BY c.date DESC")
	List<Comment> findCommentsByPost(@Param("post") Post post, Pageable pageable);

	/**
	 * Find comments by post.
	 *
	 * @param post
	 *            the post
	 * @return the list of comments
	 */
	@Query("SELECT c FROM Comment c WHERE c.post=:post ORDER BY c.date ASC")
	List<Comment> findAllCommentsByPost(@Param("post") Post post);

	/**
	 * Find all the comments by post without their responses.
	 *
	 * @param post
	 *            the post
	 * @param pageable
	 *            the pageable
	 * @return the list
	 */
	@Query("SELECT c FROM Comment c WHERE c.post=:post AND c.parent IS NULL ORDER BY c.date DESC")
	List<Comment> findParentCommentsByPost(@Param("post") Post post, Pageable pageable);
}
