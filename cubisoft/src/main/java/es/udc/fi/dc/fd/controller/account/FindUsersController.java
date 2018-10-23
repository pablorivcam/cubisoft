package es.udc.fi.dc.fd.controller.account;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.udc.fi.dc.fd.service.UserProfileService;

@Controller
@RequestMapping("/account")
public class FindUsersController {

	public static final String USERS_PARAM = "users";

	/** The user profile service. */
	@Autowired
	private UserProfileService userProfileService;

	@GetMapping(path = "/list")
	public final String showPictureList() {
		return AccountViewConstants.LIST_VIEW;
	}

	@PostMapping(path = "/list")
	public final String showPictureList(@RequestParam String keywords, final ModelMap model,
			Principal userAuthenticated) {

		model.put(USERS_PARAM, userProfileService.findUserProfileByKeywords(keywords));

		return AccountViewConstants.LIST_VIEW;
	}

}
