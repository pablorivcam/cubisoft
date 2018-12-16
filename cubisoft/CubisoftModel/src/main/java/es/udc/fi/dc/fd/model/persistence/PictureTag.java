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

	private Long picture_tag_id = -1L;

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
	public Long getPicture_tag_id() {
		return picture_tag_id;
	}

	public void setPicture_tag_id(Long picture_tag_id) {
		this.picture_tag_id = picture_tag_id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "picture")
	public Picture getPicture() {
		return picture;
	}

	public void setPicture(Picture picture) {
		this.picture = picture;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tag")
	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}

}