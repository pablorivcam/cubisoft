package es.udc.fi.dc.fd.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.udc.fi.dc.fd.model.Block;
import es.udc.fi.dc.fd.model.persistence.Comment;
import es.udc.fi.dc.fd.model.persistence.Post;
import es.udc.fi.dc.fd.repository.CommentRepository;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CommentService {

	/** The comment repository. */
	@Autowired
	private CommentRepository commentRepository;

	/**
	 * Method that adds or updates a comment into the database.
	 *
	 * @param comment
	 *            the comment
	 * @return the comment
	 */
	@Transactional
	public Comment save(Comment comment) {
		Comment c = commentRepository.save(comment);
		return c;
	}

	/**
	 * Find all the comments from a post.
	 *
	 * @param post
	 *            the post
	 * @param startIndex
	 *            the start index
	 * @param count
	 *            the count
	 * @return the list of comments from the post.
	 */
	public Block<Comment> findPostComments(Post post, int startIndex, int count) {

		List<Comment> comments;
		boolean existMoreItems = false;

		if (post == null) {
			throw new NullPointerException("The post param cannot be null");
		}

		comments = commentRepository.findCommentsByPost(post, PageRequest.of(startIndex, count + 1));

		if (comments.size() > count) {
			comments.remove(count);
			existMoreItems = true;
		}

		return new Block<>(comments, existMoreItems);
	}

	/**
	 * Find post parent comments without their responses.
	 *
	 * @param post
	 *            the post
	 * @param startIndex
	 *            the start index
	 * @param count
	 *            the count
	 * @return the list
	 */
	public Block<Comment> findPostParentComments(Post post, int startIndex, int count) {
		List<Comment> comments;
		boolean existMoreItems = false;

		if (post == null) {
			throw new NullPointerException("The post param cannot be null");
		}
		comments = commentRepository.findParentCommentsByPost(post, PageRequest.of(startIndex, count + 1));

		if (comments.size() > count) {
			comments.remove(count);
			existMoreItems = true;
		}

		return new Block<>(comments, existMoreItems);
	}

	/**
	 * Find one comment by its comment id.
	 *
	 * @param comment_id
	 *            the comment id
	 * @return the comment
	 */
	public Comment findCommentByCommentId(Long comment_id) {
		return commentRepository.findById(comment_id).get();
	}

	/**
	 * Method that deletes an existing comment from the database.
	 *
	 * @param comment
	 *            the comment
	 */
	@Transactional
	public void delete(Comment comment) {
		commentRepository.delete(comment);

	}
}
