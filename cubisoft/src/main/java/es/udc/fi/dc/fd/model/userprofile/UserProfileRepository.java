package es.udc.fi.dc.fd.model.userprofile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository("userProfileRepository")
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

	UserProfile findByEmail(String email);

	@Query("select count(a) > 0 from Account a where a.email = :email")
	boolean exists(@Param("email") String email);

}
