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
public class PictureTag {

	private Long image_tag_id = -1L;

	private Picture picture;

	private Tag tag;

	public PictureTag() {

	}

	public PictureTag(Picture picture, Tag tag) {
		this.picture = picture;
		this.tag = tag;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, unique = true)
	public Long getImage_tag_id() {
		return image_tag_id;
	}

	public void setImage_tag_id(Long image_tag_id) {
		this.image_tag_id = image_tag_id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "picture_id")
	public Picture getPicture() {
		return picture;
	}

	public void setPicture(Picture picture) {
		this.picture = picture;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tag_id")
	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}

}
