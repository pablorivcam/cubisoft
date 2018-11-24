package es.udc.fi.dc.fd.controller.picture;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.udc.fi.dc.fd.model.persistence.Picture;
import es.udc.fi.dc.fd.service.PictureService;

@RestController
@RequestMapping("picture")
public class PictureRestController {

	@Autowired
	private PictureService pictureService;

	@GetMapping(path = "/", produces = "application/json")
	public List<PictureDTO> findPicturesByDescription(@RequestParam("description") String description) {

		List<Picture> pictures = pictureService.getPicturesByDescription(description);
		List<PictureDTO> result = new ArrayList<>(pictures.size());
		for (Picture p : pictures) {
			result.add(new PictureDTO(p.getPicture_id(), p.getAuthor().getEmail(), p.getDescription(),
					p.getImage_path(), p.getDate()));
			p.setAuthor(null);
		}

		return result;
	}

	public static class PictureDTO {

		public PictureDTO(Long image_id, String author, String description, String image_path, Calendar date) {
			this.image_id = image_id;
			this.author = author;
			this.description = description;
			this.image_path = image_path;
			this.date = date;
		}

		private Long image_id;

		private String author;

		private String description;

		private String image_path;

		private Calendar date;

		public Long getImage_id() {
			return image_id;
		}

		public void setImage_id(Long image_id) {
			this.image_id = image_id;
		}

		public String getAuthor() {
			return author;
		}

		public void setAuthor(String author) {
			this.author = author;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getImage_path() {
			return image_path;
		}

		public void setImage_path(String image_path) {
			this.image_path = image_path;
		}

		public Calendar getDate() {
			return date;
		}

		public void setDate(Calendar date) {
			this.date = date;
		}

	}

}
