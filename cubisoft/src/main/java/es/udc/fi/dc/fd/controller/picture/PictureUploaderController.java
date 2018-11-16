package es.udc.fi.dc.fd.controller.picture;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.io.FilenameUtils;
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

import es.udc.fi.dc.fd.controller.post.FeedViewController;
import es.udc.fi.dc.fd.controller.post.PostViewConstants;
import es.udc.fi.dc.fd.model.form.MultipartFileValidator;
import es.udc.fi.dc.fd.model.form.UploadPictureForm;
import es.udc.fi.dc.fd.model.persistence.Picture;
import es.udc.fi.dc.fd.model.persistence.Post;
import es.udc.fi.dc.fd.model.persistence.UserProfile;
import es.udc.fi.dc.fd.repository.UserProfileRepository;
import es.udc.fi.dc.fd.service.PictureService;
import es.udc.fi.dc.fd.service.PostService;

/**
 * The Class PictureUploaderController. Encargada de controlar la página de
 * subida de imágenes.
 */
@Controller
@RequestMapping("/picture")
@MultipartConfig
public class PictureUploaderController {

	public static final String UPLOADS_FOLDER_NAME = "Pictures";

	/** The picture service. */
	@Autowired
	private PictureService pictureService;

	@Autowired
	private PostService postService;

	@Autowired
	private UserProfileRepository userProfileRepository;
	
	private final static Logger logger = Logger.getLogger(PictureUploaderController.class.getName());


	public PictureUploaderController() {
	}

	/**
	 * Upload. Process the upload GET request to get the image upload page.
	 *
	 * @param model
	 *            the model
	 * @param requestedWith
	 *            the requested with
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
	 * @param session
	 *            the session
	 * @param uploadPictureForm
	 *            the upload picture form
	 * @param modelMap
	 *            the model map
	 * @param userAuthenticated
	 *            the user authenticated
	 * @param errors
	 *            the errors
	 * @return the string
	 */
	@PostMapping("/uploadPicture")
	public String submit(HttpSession session, @Valid @ModelAttribute UploadPictureForm uploadPictureForm,
			ModelMap modelMap, Principal userAuthenticated, BindingResult errors) {

		MultipartFileValidator multipartFileValidator = new MultipartFileValidator();

		MultipartFile file = uploadPictureForm.getPictureFile();

		multipartFileValidator.validate(file, errors);

		if (errors.hasErrors()) {
			return PictureViewConstants.VIEW_PICTURE_FORM;
		}

		InputStream inputStream = null;
		OutputStream outputStream = null;

		String folderPath = "";

		modelMap.addAttribute("uploadPictureForm", uploadPictureForm);

		// Obtenemos la dirección de la carpeta a guardar la imagen
		folderPath = session.getServletContext().getRealPath("/") + UPLOADS_FOLDER_NAME;

		// Comprobamos que el fichero a subir no esté vacío
		if (!file.isEmpty()) {
			try {

				// Creamos la carpeta de recursos en caso de que no se haya
				// creado antes
				File folder = new File(folderPath);
				if (!folder.exists()) {
					if (!folder.mkdirs()) {
						System.out.println("Failed to create new directory");
					}
				}

				// Creamos el nuevo fichero para guardar la imagen
				String originalFileName = file.getOriginalFilename();
				String fileName = FilenameUtils.removeExtension(originalFileName);
				String extension = FilenameUtils.getExtension(originalFileName);
				String finalFileName = fileName + "." + extension;

				inputStream = file.getInputStream();
				File newFile = new File(folderPath + "/" + finalFileName);

				// Si el archivo ya existe le asignamos una versión
				int version = 1;
				while (newFile.exists()) {
					finalFileName = fileName + version + "." + extension;
					newFile = new File(folderPath + "/" + finalFileName);
					version++;
				}

				if (!newFile.createNewFile()) {
					System.out.println("Failed to create new file");
				}
				System.out.println("" + finalFileName);

				// Guardamos la imagen en el nuevo fichero
				outputStream = new FileOutputStream(newFile);
				int read = 0;
				byte[] bytes = new byte[1024];

				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}

				outputStream.close();
				System.out.println("Archivo guardado en: " + newFile.getAbsolutePath());

				// Obtenemos el autor asociado
				UserProfile author = userProfileRepository.findOneByEmail(userAuthenticated.getName());

				// Guardamos todo en la base de datos
				Picture p = new Picture(uploadPictureForm.getDescription(), Calendar.getInstance(), finalFileName,
						author);

				p = pictureService.save(p);

				Post post = new Post(Calendar.getInstance(), p, author, (long) 0, (long) 0, (long) 0, false);

				postService.save(post);

			} catch (IOException e) {
				logger.log(Level.INFO, e.getMessage(), e);		
			}
		}

		return "redirect:../" + PostViewConstants.VIEW_POST_LIST;

	}

	/**
	 * Gets the image.
	 *
	 * @param imageName
	 *            the image name
	 * @return the image
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@RequestMapping(value = "image/{imageName}")
	@ResponseBody
	public byte[] getImage(@PathVariable(value = "imageName") String imageName) throws IOException {

		File serverFile = new File("" + imageName + ".jpg");

		return Files.readAllBytes(serverFile.toPath());
	}

}
