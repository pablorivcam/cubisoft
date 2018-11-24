package es.udc.fi.dc.fd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.udc.fi.dc.fd.model.persistence.PictureTag;

@Repository("pictureTagrepository")
public interface PictureTagRepository extends JpaRepository<PictureTag, Long> {

}
