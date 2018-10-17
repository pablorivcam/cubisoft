package es.udc.fi.dc.fd.controller.picture;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.security.Principal;

import javax.management.InstanceNotFoundException;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.udc.fi.dc.fd.model.persistence.Picture;
import es.udc.fi.dc.fd.model.persistence.UserProfile;
import es.udc.fi.dc.fd.repository.PictureRepository;
import es.udc.fi.dc.fd.repository.UserProfileRepository;
import es.udc.fi.dc.fd.service.PictureService;

/**
 * Controller for the example entities listing view.
 * <p>
 * This serves as an adapter between the UI and the services layer.
 * 
 * @author cubisoft
 */
@Controller
@RequestMapping("/picture")
public class PictureListViewController {

	/** The Constant IMAGE_NOT_FOUND_ERROR. */
	public static final String IMAGE_NOT_FOUND_ERROR = "That picture doesn't exist.";

	/** The Constant NO_PERMISSION_TO_DELETE. */
	public static final String NO_PERMISSION_TO_DELETE = "You dont have permission to delete this picture";

	/** The Constant SUCESS_DELETED_PICTURE. */
	public static final String SUCESS_DELETED_PICTURE = "The picture has been deleted sucessfully.";

	/**
	 * Picture service.
	 */
	@Autowired
	private final PictureService pictureService;

	/*
	 * UserProfile repository.
	 */
	@Autowired
	private UserProfileRepository userProfileRepository;

	/** The picture repository. */
	@Autowired
	private PictureRepository pictureRepository;

	/**
	 * Constructs a controller with the specified dependencies.
	 * 
	 * @param service
	 *            Picture service
	 */
	@Autowired
	public PictureListViewController(final PictureService service) {
		super();

		pictureService = checkNotNull(service, "Received a null pointer as service");
	}

	/**
	 * Shows the pictures listing view.
	 * <p>
	 * Actually it just returns the name of the view. Spring will take care of the
	 * rest.
	 * <p>
	 * Before returning the name the model should be loaded with all the data
	 * required by the view.
	 *
	 * @param model
	 *            model map
	 * @param userAuthenticated
	 *            the user authenticated
	 * @return the name for the pictures listing view
	 */
	@GetMapping(path = "/list")
	public final String showPictureList(final ModelMap model, Principal userAuthenticated) {
		// Loads required data into the model
		loadViewModel(model, userAuthenticated);

		return PictureViewConstants.VIEW_PICTURE_LIST;
	}

	/**
	 * Returns the picture service.
	 * 
	 * @return the picture service
	 */
	private final PictureService getPictureService() {
		return pictureService;
	}

	/**
	 * Loads the model data required for the pictures listing view.
	 * <p>
	 * As the view will list all the pictures, it requires these pictures as one of
	 * the parameters.
	 *
	 * @param model
	 *            model map
	 * @param userAuthenticated
	 *            the user authenticated
	 */
	private final void loadViewModel(final ModelMap model, Principal userAuthenticated) {
		UserProfile author = userProfileRepository.findOneByEmail(userAuthenticated.getName());

		try {
			model.put(PictureViewConstants.PARAM_PICTURES, getPictureService().getPicturesByAuthor(author));
		} catch (InstanceNotFoundException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Delete picture post mapping.
	 *
	 * @param pictureId
	 *            the picture id
	 * @param picturePath
	 *            the picture path
	 * @param model
	 *            the model
	 * @param userAuthenticated
	 *            the user authenticated
	 * @return the string
	 */
	@PostMapping("deletePicture")
	public final String deletePicture(@RequestParam Long pictureId, @RequestParam String picturePath,
			final ModelMap model, Principal userAuthenticated, HttpSession session) {
		String error_message = "";
		Boolean sucess = false;
		Picture p = pictureRepository.findById(pictureId).get();
		UserProfile author = userProfileRepository.findOneByEmail(userAuthenticated.getName());

		String folderPath = session.getServletContext().getRealPath("/")
				+ PictureUploaderController.UPLOADS_FOLDER_NAME;

		String imagePath = folderPath + "/" + picturePath;
		File pictureFile = new File(imagePath);

		// Eliminamos la imagen
		if (p == null || !pictureFile.exists()) {
			error_message = IMAGE_NOT_FOUND_ERROR;
		} else if (p.getAuthor().getUser_id() != author.getUser_id()) {
			error_message = NO_PERMISSION_TO_DELETE;
		} else {
			// Eliminamos la imagen de la BD
			pictureService.delete(p);

			// Eliminamos la imagen del servidor
			pictureFile.delete();

			error_message = SUCESS_DELETED_PICTURE;
			sucess = true;
		}

		// Devolvemos el mensaje
		model.put("error_message", error_message);
		model.put("sucess", sucess);

		loadViewModel(model, userAuthenticated);

		return PictureViewConstants.VIEW_PICTURE_LIST;

	}

}
