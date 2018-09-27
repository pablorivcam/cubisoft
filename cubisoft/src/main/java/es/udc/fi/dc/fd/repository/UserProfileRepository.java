package es.udc.fi.dc.fd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import es.udc.fi.dc.fd.model.persistence.UserProfile;

@Repository("userProfileRepository")
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

	@Query("select count(a) > 0 from UserProfile a where a.email = :email")
	UserProfile findByEmail(@Param("email") String email);

	@Query("select a from UserProfile a where a.email = :email and a.password = :password")
	UserProfile findUser(@Param("email") String email, @Param("password") String password);
	
}
