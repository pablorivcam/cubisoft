package es.udc.fi.dc.fd.controller.picture;

import static com.google.common.base.Preconditions.checkNotNull;

import java.security.Principal;

import javax.management.InstanceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import es.udc.fi.dc.fd.model.persistence.UserProfile;
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

}
