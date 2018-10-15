package es.udc.fi.dc.fd.controller.follow;

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
import es.udc.fi.dc.fd.service.FollowService;

@Controller
@RequestMapping("/followed")
public class FollowedListViewController {
	@Autowired
	private final FollowService followService;

	@Autowired
	private UserProfileRepository userProfileRepository;

	@Autowired
	public FollowedListViewController(final FollowService service) {
		super();

		followService = checkNotNull(service, "Received a null pointer as service");
	}

	/**
	 * Returns the page containing all the followed user profiles of a user
	 * 
	 * @param model             model map
	 * @param userAuthenticated The authenticated user
	 */
	@GetMapping(path = "/list")
	public final String showFollowedList(final ModelMap model, Principal userAuthenticated) {
		// Loads required data into the model
		loadViewModel(model, userAuthenticated);

		return FollowViewConstants.VIEW_FOLLOWED_LIST;
	}

	/**
	 * Loads the data required for showing all the followed profiles of a user
	 * 
	 * @param model             model map
	 * @param userAuthenticated the authenticated user
	 */
	private final void loadViewModel(final ModelMap model, Principal userAuthenticated) {
		UserProfile user = userProfileRepository.findOneByEmail(userAuthenticated.getName());

		try {
			model.put(FollowViewConstants.VIEW_FOLLOWED_LIST, followService.getUserFollowedProfiles(user));
		} catch (InstanceNotFoundException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
}