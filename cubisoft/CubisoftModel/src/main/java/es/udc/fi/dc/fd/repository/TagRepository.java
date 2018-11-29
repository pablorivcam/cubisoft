package es.udc.fi.dc.fd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import es.udc.fi.dc.fd.model.persistence.Tag;

@Repository("tagRepository")
public interface TagRepository extends JpaRepository<Tag, Long> {

	Tag findTagByText(String text);

	boolean existsByText(String text);

	@Query("SELECT t FROM Tag t WHERE t.text LIKE CONCAT('%',:keywords,'%')")
	List<Tag> findTagsByKeywords(@Param("keywords") String keywords);

}
