package es.udc.fi.dc.fd.controller.picture;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.Principal;
import java.util.Calendar;

import javax.servlet.annotation.MultipartConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import es.udc.fi.dc.fd.model.form.UploadPictureForm;
import es.udc.fi.dc.fd.model.persistence.Picture;
import es.udc.fi.dc.fd.model.persistence.UserProfile;
import es.udc.fi.dc.fd.repository.UserProfileRepository;
import es.udc.fi.dc.fd.service.PictureService;

/**
 * The Class PictureUploaderController. Encargada de controlar la página de
 * subida de imágenes.
 */
@Controller
@RequestMapping("/picture")
@MultipartConfig
public class PictureUploaderController {

	private static final String UPLOADS_FOLDER_NAME = "../Pictures";

	/** The picture service. */
	@Autowired
	private PictureService pictureService;

	@Autowired
	private UserProfileRepository userProfileRepository;

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
	 * server.
	 *
	 * @param uploadPictureForm
	 *            the upload picture form
	 * @param modelMap
	 *            the model map
	 * @param userAuthenticated
	 *            the user authenticated
	 * @return the string
	 */
	@PostMapping("/uploadPicture")
	public String submit(@ModelAttribute UploadPictureForm uploadPictureForm, ModelMap modelMap,
			Principal userAuthenticated) {

		MultipartFile file = uploadPictureForm.getPictureFile();

		InputStream inputStream = null;
		OutputStream outputStream = null;

		String folderPath = "";

		modelMap.addAttribute("uploadPictureForm", uploadPictureForm);

		// Obtenemos la dirección de la carpeta a guardar la imagen
		try {
			String path = this.getClass().getClassLoader().getResource("").getPath();
			String fullPath = URLDecoder.decode(path, "UTF-8");
			folderPath = fullPath + UPLOADS_FOLDER_NAME;
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Comprobamos que el fichero a subir no esté vacío
		if (!file.isEmpty()) {
			try {

				// Creamos la carpeta de recursos en caso de que no se haya creado antes
				File folder = new File(folderPath);
				if (!folder.exists()) {
					folder.mkdirs();
				}

				// Creamos el nuevo fichero para guardar la imagen
				String fileName = file.getOriginalFilename();
				inputStream = file.getInputStream();
				File newFile = new File(folderPath + "/" + fileName);

				// Si el archivo ya existe le asignamos una versión
				int version = 1;
				while (newFile.exists()) {
					newFile = new File(folderPath + "/" + fileName + version);
					version++;
				}

				newFile.createNewFile();

				// Guardamos la imagen en el nuevo fichero
				outputStream = new FileOutputStream(newFile);
				int read = 0;
				byte[] bytes = new byte[1024];

				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}

				System.out.println("Archivo guardado en: " + newFile.getAbsolutePath());

				// Obtenemos el autor asociado
				UserProfile author = userProfileRepository.findOneByEmail(userAuthenticated.getName());

				// Guardamos todo en la base de datos
				// newFile.getAbsolutePath() como posible alternativa a fileName
				// FIXME probablemente haya que cambiar el fileName para evitar movidas
				Picture p = new Picture(uploadPictureForm.getDescription(), Calendar.getInstance(),
						fileName, author);
				pictureService.save(p);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return PictureViewConstants.VIEW_PICTURE_FORM;
	}

}
