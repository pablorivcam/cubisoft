package es.udc.fi.dc.fd.controller.story;

import static com.google.common.base.Preconditions.checkNotNull;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.udc.fi.dc.fd.model.persistence.Story;
import es.udc.fi.dc.fd.model.persistence.UserProfile;
import es.udc.fi.dc.fd.service.PictureService;
import es.udc.fi.dc.fd.service.StoryService;
import es.udc.fi.dc.fd.service.UserProfileService;

@Controller
@RequestMapping("/story")
public class StoryListViewController {

	@Autowired
	private final PictureService pictureService;

	@Autowired
	private UserProfileService userProfileService;

	@Autowired
	private StoryService storyService;

	@Autowired
	public StoryListViewController(final PictureService servicePicture) {

		pictureService = checkNotNull(servicePicture, "Received a null pointer as service");
	}

	/**
	 * Show my feed.
	 *
	 * @param model             the model
	 * @param userAuthenticated the user authenticated
	 * @param user_id           the user id
	 * @return the string
	 */
	@GetMapping(path = "/myFeed{user_id}")
	public final String showMyFeed(final ModelMap model, Principal userAuthenticated,
			@RequestParam("user_id") Optional<Long> user_id) {
		loadViewModel(model, userAuthenticated, (user_id.isPresent()) ? user_id.get() : null);

		return "story/myFeed";
	}

	private final void loadViewModel(final ModelMap model, Principal userAuthenticated, Long user_id) {

		List<Story> stories = storyService.loadFeed(user_id, userAuthenticated);

		UserProfile userFound = null;

		if (userAuthenticated != null) {
			UserProfile user = userProfileService.findUserByEmail(userAuthenticated.getName());
			model.put("currentUser", user);
			userFound = userProfileService.findById(user_id);
		}
		model.put("userFound", userFound);

		model.put("pictureService", pictureService);
		model.put("storyService", storyService);
		model.put("stories", stories);

	}

}
