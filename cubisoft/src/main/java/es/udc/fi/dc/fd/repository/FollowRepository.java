package es.udc.fi.dc.fd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import es.udc.fi.dc.fd.model.persistence.Follow;
import es.udc.fi.dc.fd.model.persistence.UserProfile;

/**
 * The Interface FollowRepository.
 */
@Repository("followRepository")
public interface FollowRepository extends JpaRepository<Follow, Long> {

	/**
	 * Find follow by users
	 *
	 * @param user          the user
	 * @param followed_user the user followed
	 * @return the follow
	 */

	@Query("SELECT f FROM Follow f WHERE f.user=:user AND f.followed_user=:followed_user")
	Follow findFollowByUsers(@Param("user") UserProfile user, @Param("followed_user") UserProfile followed_user);

	@Query("SELECT f FROM Follow f WHERE f.user=:user")
	List<UserProfile> findFollowedProfilesByUser(@Param("user") UserProfile user);

}
