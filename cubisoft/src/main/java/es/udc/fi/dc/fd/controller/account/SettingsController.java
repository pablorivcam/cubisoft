package es.udc.fi.dc.fd.controller.account;

import static com.google.common.base.Preconditions.checkNotNull;

import java.security.Principal;
import java.util.Optional;

import javax.management.InstanceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.udc.fi.dc.fd.controller.post.PostViewConstants;
import es.udc.fi.dc.fd.model.persistence.UserProfile;
import es.udc.fi.dc.fd.repository.UserProfileRepository;
import es.udc.fi.dc.fd.service.LikesService;
import es.udc.fi.dc.fd.service.PictureService;
import es.udc.fi.dc.fd.service.PostService;
import es.udc.fi.dc.fd.service.PostViewService;
import es.udc.fi.dc.fd.service.UserProfileService;



@Controller
@RequestMapping("/settings")
public class SettingsController {
	
	@Autowired
	private final UserProfileService userProfileService;
	
	@Autowired
	private UserProfileRepository userProfileRepository;

	@Autowired
	public SettingsController(final UserProfileService userService) {

		userProfileService = checkNotNull(userService, "Received a null pointer as service");
	}
	
	@GetMapping(path="/view")
	public final String showSettings(final ModelMap model, Principal userAuthenticated) {

		loadViewModel(model, userAuthenticated);

		return AccountViewConstants.VIEW_SETTINGS;
	}
	
	@PostMapping("/setToPrivate")
	public final String setToPrivate(final ModelMap model, Principal userAuthenticated) {
		UserProfile user = userProfileRepository.findOneByEmail(userAuthenticated.getName());
		userProfileService.changeUserProfileType(user, UserProfile.UserType.PRIVATE);
		loadViewModel(model, userAuthenticated);
		return AccountViewConstants.VIEW_SETTINGS;
	}
	
	@PostMapping("/setToPublic")
	public final String setToPublic(final ModelMap model, Principal userAuthenticated) {
		UserProfile user = userProfileRepository.findOneByEmail(userAuthenticated.getName());
		userProfileService.changeUserProfileType(user, UserProfile.UserType.PUBLIC);
		loadViewModel(model, userAuthenticated);
		return AccountViewConstants.VIEW_SETTINGS;
	}
	
	private final void loadViewModel(final ModelMap model, Principal userAuthenticated) {
		UserProfile user = userProfileRepository.findOneByEmail(userAuthenticated.getName());
		model.put("currentUser", user);
	}
}
