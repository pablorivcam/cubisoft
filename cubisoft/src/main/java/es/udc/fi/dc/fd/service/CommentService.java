package es.udc.fi.dc.fd.service;

import java.util.Calendar;
import java.util.List;

import javax.management.InstanceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.udc.fi.dc.fd.model.Block;
import es.udc.fi.dc.fd.model.persistence.Comment;
import es.udc.fi.dc.fd.model.persistence.Post;
import es.udc.fi.dc.fd.model.persistence.UserProfile;
import es.udc.fi.dc.fd.repository.CommentRepository;
import es.udc.fi.dc.fd.repository.PostRepository;
import es.udc.fi.dc.fd.repository.UserProfileRepository;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CommentService {

	/** The comment repository. */
	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private UserProfileRepository userProfileRepository;

	@Autowired
	private PostRepository postRepository;

	/**
	 * Method that adds or updates a comment into the database.
	 *
	 * @param comment
	 *            the comment
	 * @return the comment
	 */
	@Transactional
	public Comment save(Comment comment) {
		return commentRepository.save(comment);
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

	/**
	 * Replies a comment with annother comment.
	 *
	 * @param commentId
	 *            the comment id
	 * @param text
	 *            the text of the reply.
	 * @param userName
	 *            the user name
	 * @param date
	 *            the date of the reply.
	 * @return the comment generated for the reply.
	 * @throws InstanceNotFoundException
	 *             the instance not found exception
	 */
	@Transactional(noRollbackFor = Exception.class)
	public Comment replyComment(Long commentId, String text, String userName, Calendar date)
			throws InstanceNotFoundException {

		if (!commentRepository.existsById(commentId)) {
			throw new InstanceNotFoundException("That post with that id doesn't exists.");
		}

		if (!userProfileRepository.exists(userName)) {
			throw new InstanceNotFoundException("That user doesn't exists.");
		}

		Comment comment = findCommentByCommentId(commentId);

		UserProfile user = userProfileRepository.findOneByEmail(userName);

		return save(new Comment(text, date, comment.getPost(), user, comment));
	}

	/**
	 * Adds the comment.
	 *
	 * @param text
	 *            the text
	 * @param postId
	 *            the post id
	 * @param authorEmail
	 *            the author email
	 * @return the comment
	 * @throws InstanceNotFoundException
	 *             the instance not found exception
	 */
	@Transactional(noRollbackFor = Exception.class)
	public Comment addComment(String text, Long postId, String authorEmail) throws InstanceNotFoundException {

		if (!userProfileRepository.exists(authorEmail)) {
			throw new InstanceNotFoundException("That user doesn't exists.");
		}

		if (!postRepository.existsById(postId)) {
			throw new InstanceNotFoundException("That post with that id doesn't exists.");
		}

		UserProfile author = userProfileRepository.findOneByEmail(authorEmail);
		Post p = postRepository.findPostByPostId(postId);

		Comment c = new Comment(text, Calendar.getInstance(), p, author, null);
		return save(c);

	}

	/**
	 * Modify comment.
	 *
	 * @param commentId
	 *            the comment id
	 * @param newContent
	 *            the new content of the comment
	 * @return the comment
	 */
	@Transactional(noRollbackFor = Exception.class)
	public Comment modifyComment(Long commentId, String newContent) throws InstanceNotFoundException {

		if (!commentRepository.existsById(commentId)) {
			throw new InstanceNotFoundException("That post with that id doesn't exists.");
		}

		Comment c = findCommentByCommentId(commentId);
		c.setText(newContent);
		return save(c);
	}
}
