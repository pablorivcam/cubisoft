/**
 * 
 */
package es.udc.fi.dc.fd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import es.udc.fi.dc.fd.model.persistence.Post;
import es.udc.fi.dc.fd.model.persistence.PostView;
import es.udc.fi.dc.fd.model.persistence.UserProfile;

/**
 * The Interface PostViewRepository.
 */
@Repository("postViewRepository")
public interface PostViewRepository extends JpaRepository<PostView, Long> {

	/**
	 * Find views by post.
	 *
	 * @param post
	 *            the post
	 * @return the list of views
	 */
	@Query("select v from PostView v where v.post=:post")
	List<PostView> findViewsByPost(@Param("post") Post post);

	/**
	 * Find postView.
	 *
	 * @param post
	 *            the post
	 *
	 ** @param user
	 *            the user
	 * 
	 * @return the view
	 */
	@Query("select v from PostView v where v.post=:post AND v.user=:user")
	PostView findPostView(@Param("post") Post post, @Param("user") UserProfile user);

}
