package es.udc.fi.dc.fd.controller.account;

import static com.google.common.base.Preconditions.checkNotNull;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import es.udc.fi.dc.fd.model.persistence.Follow;
import es.udc.fi.dc.fd.model.persistence.UserProfile;
import es.udc.fi.dc.fd.repository.FollowRepository;
import es.udc.fi.dc.fd.repository.UserProfileRepository;
import es.udc.fi.dc.fd.service.FollowService;
import es.udc.fi.dc.fd.service.UserProfileService;

@Controller
@RequestMapping("/settings")
public class SettingsController {

	@Autowired
	private final UserProfileService userProfileService;

	@Autowired
	private final FollowService followService;

	@Autowired
	private UserProfileRepository userProfileRepository;

	@Autowired
	private FollowRepository followRepository;

	@Autowired
	public SettingsController(final UserProfileService userService, final FollowService serviceFollow) {

		userProfileService = checkNotNull(userService, "Received a null pointer as service");
		followService = checkNotNull(serviceFollow, "Received a null pointer as service");
	}

	@GetMapping(path = "/view")
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
		List<Follow> follows = followRepository.findFollowsPending(user);

		for (Follow follow : follows) {
			followService.processPendingFollows(follow, true);
		}

		loadViewModel(model, userAuthenticated);
		return AccountViewConstants.VIEW_SETTINGS;
	}

	private final void loadViewModel(final ModelMap model, Principal userAuthenticated) {
		UserProfile user = userProfileRepository.findOneByEmail(userAuthenticated.getName());
		model.put("currentUser", user);
	}
}
