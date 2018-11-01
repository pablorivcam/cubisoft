package es.udc.fi.dc.fd.model.persistence;

import java.util.Calendar;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Comment {

	/** The comment id. */
	private Long comment_id = -1L;

	/** The text. */
	private String text;

	/** The date. */
	private Calendar date;

	/** The post. */
	private Post post;

	/** The user. */
	private UserProfile user;

	/** The parent. */
	private Comment parent;

	/** The child comments. */
	private List<Comment> child_comments;

	/**
	 * Instantiates a new comment.
	 */
	public Comment() {

	}

	/**
	 * Instantiates a new comment.
	 *
	 * @param text
	 *            the text
	 * @param date
	 *            the date
	 * @param post
	 *            the post
	 * @param user
	 *            the user
	 * @param parent
	 *            the parent
	 */
	public Comment(String text, Calendar date, Post post, UserProfile user, Comment parent) {
		this.text = text;
		this.date = date;
		this.post = post;
		this.user = user;
		this.parent = parent;

		date.set(Calendar.MILLISECOND, 0);

	}

	/**
	 * Gets the comment id.
	 *
	 * @return the comment id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, unique = true)
	public Long getComment_id() {
		return comment_id;
	}

	/**
	 * Sets the comment id.
	 *
	 * @param comment_id
	 *            the new comment id
	 */
	public void setComment_id(Long comment_id) {
		this.comment_id = comment_id;
	}

	/**
	 * Gets the text.
	 *
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * Sets the text.
	 *
	 * @param text
	 *            the new text
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Gets the date.
	 *
	 * @return the date
	 */
	@Column(nullable = false)
	public Calendar getDate() {
		return date;
	}

	/**
	 * Sets the date.
	 *
	 * @param date
	 *            the new date
	 */
	public void setDate(Calendar date) {
		this.date = date;
	}

	/**
	 * Gets the post.
	 *
	 * @return the post
	 */
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "post_id")
	public Post getPost() {
		return post;
	}

	/**
	 * Sets the post.
	 *
	 * @param post
	 *            the new post
	 */
	public void setPost(Post post) {
		this.post = post;
	}

	/**
	 * Gets the user.
	 *
	 * @return the user
	 */
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id")
	public UserProfile getUser() {
		return user;
	}

	/**
	 * Sets the user.
	 *
	 * @param user
	 *            the new user
	 */
	public void setUser(UserProfile user) {
		this.user = user;
	}

	/**
	 * Gets the parent.
	 *
	 * @return the parent
	 */
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "parent_id")
	public Comment getParent() {
		return parent;
	}

	/**
	 * Sets the parent.
	 *
	 * @param parent
	 *            the new parent
	 */
	public void setParent(Comment parent) {
		this.parent = parent;
	}

	/**
	 * Gets the child comments.
	 *
	 * @return the child comments
	 */
	@OneToMany(mappedBy = "parent")
	public List<Comment> getChild_comments() {
		return child_comments;
	}

	/**
	 * Sets the child comments.
	 *
	 * @param child_comments
	 *            the new child comments
	 */
	public void setChild_comments(List<Comment> child_comments) {
		this.child_comments = child_comments;
	}
}
