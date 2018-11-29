package es.udc.fi.dc.fd.model.persistence;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Tag {

	/** The tag id. */
	private Long tag_id = -1L;

	private String text;

	private List<PictureTag> pictureTags;

	public Tag() {

	}

	public Tag(String text, List<PictureTag> pictureTags) {
		this.text = text;
		this.pictureTags = pictureTags;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, unique = true)
	public Long getTag_id() {
		return tag_id;
	}

	public void setTag_id(Long tag_id) {
		this.tag_id = tag_id;
	}

	@Column(nullable = false, unique = true)
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@OneToMany(mappedBy = "tag")
	public List<PictureTag> getPictureTags() {
		return pictureTags;
	}

	public void setPictureTags(List<PictureTag> pictureTags) {
		this.pictureTags = pictureTags;
	}

}
