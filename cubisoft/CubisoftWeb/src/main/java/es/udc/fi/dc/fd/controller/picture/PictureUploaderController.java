package es.udc.fi.dc.fd.controller.picture;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.Principal;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import es.udc.fi.dc.fd.controller.post.PostViewConstants;
import es.udc.fi.dc.fd.model.form.MultipartFileValidator;
import es.udc.fi.dc.fd.model.form.UploadPictureForm;
import es.udc.fi.dc.fd.service.PictureService;

/**
 * The Class PictureUploaderController.
 */
@Controller
@RequestMapping("/picture")
@MultipartConfig
public class PictureUploaderController {

	public static final String UPLOADS_FOLDER_NAME = "Pictures";

	/** The picture service. */
	@Autowired
	private PictureService pictureService;

	public PictureUploaderController() {
	}

	/**
	 * Upload. Process the upload GET request to get the image upload page.
	 *
	 * @param model         the model
	 * @param requestedWith the requested with
	 * @return the string
	 */
	@GetMapping("upload")
	String upload(Model model, @RequestHeader(value = "X-Requested-With", required = false) String requestedWith) {
		model.addAttribute(new UploadPictureForm());
		return PictureViewConstants.VIEW_PICTURE_FORM;
	}

	/**
	 * Submit. Process the uploadPicture POST request to upload an image into the
	 * server. In adition, a single post is created with the image.
	 *
	 * @param session           the session
	 * @param uploadPictureForm the upload picture form
	 * @param modelMap          the model map
	 * @param userAuthenticated the user authenticated
	 * @param errors            the errors
	 * @return the string
	 */
	@PostMapping("/uploadPicture")
	public String submit(HttpSession session, @Valid @ModelAttribute UploadPictureForm uploadPictureForm,
			ModelMap modelMap, Principal userAuthenticated, BindingResult errors) {

		MultipartFileValidator multipartFileValidator = new MultipartFileValidator();
		try {
			MultipartFile file = pictureService.uploadPicture(uploadPictureForm, userAuthenticated,
					session.getServletContext().getRealPath("/") + UPLOADS_FOLDER_NAME);
			multipartFileValidator.validate(file, errors);
		} catch (Exception e) {
			// TODO aqui no deberia de pasar nada
		}

		if (errors.hasErrors()) {
			return PictureViewConstants.VIEW_PICTURE_FORM;
		}

		return "redirect:../" + PostViewConstants.VIEW_POST_LIST;

	}

	/**
	 * Gets the image.
	 *
	 * @param imageName the image name
	 * @return the image
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@RequestMapping(value = "image/{imageName}")
	@ResponseBody
	public byte[] getImage(@PathVariable(value = "imageName") String imageName) throws IOException {

		File serverFile = new File("" + imageName + ".jpg");

		return Files.readAllBytes(serverFile.toPath());
	}

}
