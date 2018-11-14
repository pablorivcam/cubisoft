/**
 * 
 */
package es.udc.fi.dc.fd.controller.follow;

import static com.google.common.base.Preconditions.checkNotNull;

import java.security.Principal;

import javax.management.InstanceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.udc.fi.dc.fd.model.persistence.Follow;
import es.udc.fi.dc.fd.model.persistence.UserProfile;
import es.udc.fi.dc.fd.repository.FollowRepository;
import es.udc.fi.dc.fd.repository.UserProfileRepository;
import es.udc.fi.dc.fd.service.FollowService;

@Controller
@RequestMapping("/request")
public class RequestListViewController {

	@Autowired
	private final FollowService followService;

	@Autowired
	private FollowRepository followRepository;

	@Autowired
	private UserProfileRepository userProfileRepository;

	@Autowired
	public RequestListViewController(final FollowService service) {
		super();

		followService = checkNotNull(service, "Received a null pointer as service");
	}

	/**
	 * Returns the page containing all the followed user profiles of a user.
	 *
	 * @param model
	 *            model map
	 * @param userAuthenticated
	 *            The authenticated user
	 * @return the string
	 */
	@GetMapping(path = "/list")
	public final String showFollowedList(final ModelMap model, Principal userAuthenticated) {
		// Loads required data into the model
		loadViewModel(model, userAuthenticated);

		return FollowViewConstants.VIEW_REQUEST_LIST;
	}

	/**
	 * Loads the data required for showing all the followed requestd profiles of
	 * a user.
	 *
	 * @param model
	 *            model map
	 * @param userAuthenticated
	 *            the authenticated user
	 */
	private final void loadViewModel(final ModelMap model, Principal userAuthenticated) {
		UserProfile user = userProfileRepository.findOneByEmail(userAuthenticated.getName());

		try {
			model.put(FollowViewConstants.PARAM_REQUEST, followService.findFollowsPending(user));
		} catch (InstanceNotFoundException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	@PostMapping(path = "/acceptRequest")
	public final String acceptRequest(@RequestParam Long follow_id, final ModelMap model, Principal userAuthenticated) {

		Follow follow = followRepository.getOne(follow_id);

		followService.processPendingFollows(follow, true);

		loadViewModel(model, userAuthenticated);

		return FollowViewConstants.VIEW_REQUEST_LIST;
	}

	@PostMapping(path = "/cancelRequest")
	public final String cancelRequest(@RequestParam Long follow_id, final ModelMap model, Principal userAuthenticated) {

		Follow follow = followRepository.getOne(follow_id);

		followService.processPendingFollows(follow, false);

		loadViewModel(model, userAuthenticated);

		return FollowViewConstants.VIEW_REQUEST_LIST;
	}

}
