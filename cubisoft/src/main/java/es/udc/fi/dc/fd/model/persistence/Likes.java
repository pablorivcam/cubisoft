package es.udc.fi.dc.fd.model.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Likes {

	/** The likes id. */
	private Long likes_id = -1L;

	/** The user. */
	private UserProfile user;

	/** The post. */
	private Post post;

	public Likes() {

	}

	public Likes(UserProfile user, Post post) {
		this.user = user;
		this.post = post;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, unique = true)
	public Long getLikes_id() {
		return likes_id;
	}

	public void setLikes_id(Long likes_id) {
		this.likes_id = likes_id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	public UserProfile getUser() {
		return user;
	}

	public void setUser(UserProfile user) {
		this.user = user;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id")
	public Post getPost() {
		return post;
	}

	public void setPost(Post post) {
		this.post = post;
	}
}
