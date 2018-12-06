package es.udc.fi.dc.fd.repository;

import java.util.Calendar;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import es.udc.fi.dc.fd.model.persistence.Story;
import es.udc.fi.dc.fd.model.persistence.UserProfile;

/**
 * The Interface PostRepository to access to the Post data stored.
 */
@Repository("storyRepository")
public interface StoryRepository extends JpaRepository<Story, Long> {

	/**
	 * Find user stories.
	 *
	 * @param user the user
	 * @return the list
	 */
	@Query("SELECT s FROM Story s WHERE s.user=:user ORDER BY s.expiration DESC")
	List<Story> findUserStories(@Param("user") UserProfile user);

	/**
	 * Find one by story id.
	 *
	 * @param post_id the story id
	 * @return the story
	 */
	@Query("SELECT s FROM Story s where s.story_id=:story_id")
	Story findStoryByStoryId(@Param("story_id") Long story_id);

	/**
	 * Delete old stories.
	 *
	 * @param user the user
	 * @return the list
	 */
	@Query("DELETE FROM Story s WHERE s.expiration<=:expiration")
	void deleteOldStories(@Param("expiration") Calendar expiration);
}