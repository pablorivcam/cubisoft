package es.udc.fi.dc.fd.controller.picture;

import java.util.List;

import javax.management.InstanceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.udc.fi.dc.fd.model.persistence.Picture;
import es.udc.fi.dc.fd.model.persistence.Post;
import es.udc.fi.dc.fd.service.PictureService;
import es.udc.fi.dc.fd.service.PostService;

@RestController
@RequestMapping("rest")
public class PictureRestController {

	@Autowired
	private PictureService pictureService;

	@Autowired
	private PostService postService;

	@GetMapping(path = "picture")
	public List<Picture> findPicturesByDescription(
			@RequestParam(value = "description", required = false) String description,
			@RequestParam(value = "hashtags", required = false) String[] hashtags) {
		List<Picture> pictures = null;

		if (description != null) {
			pictures = pictureService.getPicturesByDescription(description);
		}

		if (hashtags != null) {
			pictures = pictureService.getPicturesByHashtags(hashtags);
		}

		return pictures;
	}

	@GetMapping(path = "post")
	public List<Post> findPostsByHashtags(@RequestParam(value = "user", required = true) String user,
			@RequestParam(value = "hashtags", required = true) String[] hashtags) throws InstanceNotFoundException {
		List<Post> posts = null;

		if (hashtags != null) {
			posts = postService.findGlobalUserPostsByHashtags(user, hashtags);
		}

		return posts;
	}

}
