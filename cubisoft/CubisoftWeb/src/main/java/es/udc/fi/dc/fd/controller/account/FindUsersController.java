package es.udc.fi.dc.fd.controller.account;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.udc.fi.dc.fd.model.persistence.UserProfile;
import es.udc.fi.dc.fd.model.persistence.UserProfile.UserType;
import es.udc.fi.dc.fd.repository.UserProfileRepository;
import es.udc.fi.dc.fd.service.FollowService;
import es.udc.fi.dc.fd.service.UserProfileService;

@Controller
@RequestMapping("/account")
public class FindUsersController {

	public static final String USERS_PARAM = "users";

	/** The user profile service. */
	@Autowired
	private UserProfileService userProfileService;

	@Autowired
	private UserProfileRepository userProfileRepository;

	@Autowired
	private FollowService followService;

	@GetMapping(path = "/list")
	public final String showPictureList(@RequestParam(required = false, defaultValue = "") String keywords,
			final ModelMap model, Principal userAuthenticated,
			@RequestParam(required = false, defaultValue = "0") int startIndex) {

		if (userAuthenticated != null) {
			UserProfile currentUser = userProfileRepository.findOneByEmail(userAuthenticated.getName());
			model.put("currentUser", currentUser);
		}

		if (keywords != null) {
			model.put(USERS_PARAM, userProfileService.findUserProfileByKeywords(keywords, startIndex, 5));
			model.put("followService", followService);
			model.put("keywords", keywords);
			model.put("startIndex", startIndex);
		}
		return AccountViewConstants.LIST_VIEW;
	}

	@PostMapping(path = "/followUser")
	public final String followUser(@RequestParam String keywords, @RequestParam Long user_id, final ModelMap model,
			Principal userAuthenticated, @RequestParam(required = false, defaultValue = "0") int startIndex) {

		UserProfile followed_user = userProfileRepository.findById(user_id).get();
		UserProfile currentUser = userProfileRepository.findOneByEmail(userAuthenticated.getName());

		model.put(USERS_PARAM, userProfileService.findUserProfileByKeywords(keywords, startIndex, 5));
		model.put("currentUser", currentUser);
		model.put("followService", followService);
		model.put("keywords", keywords);
		model.put("startIndex", startIndex);

		if (!followService.isUserAFollowingUserB(currentUser, followed_user)) {
			if (followed_user.getUserType() == UserType.PRIVATE) {
				followService.follow(currentUser, followed_user, Boolean.TRUE);

			} else {
				followService.follow(currentUser, followed_user, Boolean.FALSE);

			}
		}

		return AccountViewConstants.LIST_VIEW;
	}

	@PostMapping(path = "/unfollowUser")
	public final String unfollowUser(@RequestParam String keywords, @RequestParam Long user_id, final ModelMap model,
			Principal userAuthenticated, @RequestParam(required = false, defaultValue = "0") int startIndex) {

		UserProfile followed_user = userProfileRepository.findById(user_id).get();
		UserProfile currentUser = userProfileRepository.findOneByEmail(userAuthenticated.getName());

		model.put(USERS_PARAM, userProfileService.findUserProfileByKeywords(keywords, startIndex, 5));
		model.put("currentUser", currentUser);
		model.put("followService", followService);
		model.put("keywords", keywords);
		model.put("startIndex", startIndex);

		if (followService.isUserAFollowingUserB(currentUser, followed_user)) {
			followService.unfollow(currentUser, followed_user);
		}

		return AccountViewConstants.LIST_VIEW;
	}
}
