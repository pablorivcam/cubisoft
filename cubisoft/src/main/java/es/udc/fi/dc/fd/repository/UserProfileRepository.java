package es.udc.fi.dc.fd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import es.udc.fi.dc.fd.model.persistence.UserProfile;

/**
 * The Interface UserProfileRepository.
 */
@Repository("userProfileRepository")
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

	UserProfile findOneByEmail(String email);

	/**
	 * Exists.
	 *
	 * @param email
	 *            the email
	 * @return true, if successful
	 */
	@Query("select count(u) > 0 from UserProfile u where u.email = :email")
	boolean exists(@Param("email") String email);

	@Query("SELECT u FROM UserProfile u WHERE u.login LIKE CONCAT('%',:keywords,'%') ORDER BY u.login")
	List<UserProfile> findUserProfileByKeywords(@Param("keywords") String keywords);
}
