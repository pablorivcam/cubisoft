package es.udc.fi.dc.fd.controller.follow;

import static com.google.common.base.Preconditions.checkNotNull;

import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.InstanceNotFoundException;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.udc.fi.dc.fd.model.persistence.UserProfile;
import es.udc.fi.dc.fd.repository.UserProfileRepository;
import es.udc.fi.dc.fd.service.AlreadyBlockedException;
import es.udc.fi.dc.fd.service.BlocksService;
import es.udc.fi.dc.fd.service.FollowService;
import es.udc.fi.dc.fd.service.NotBlockedYetException;

@Controller
@RequestMapping("/followers")
public class FollowerListViewController {
	@Autowired
	private final FollowService followService;

	@Autowired
	private final BlocksService blocksService;

	@Autowired
	private UserProfileRepository userProfileRepository;

	private final static Logger logger = Logger.getLogger(FollowerListViewController.class.getName());

	public static final String SUCESS_KICKED_USER = "User kicked sucessfully.";

	@Autowired
	public FollowerListViewController(final FollowService service, final BlocksService blockService) {
		super();

		followService = checkNotNull(service, "Received a null pointer as service");
		blocksService = checkNotNull(blockService, "Received a null pointer as service");
	}

	/**
	 * Returns the page containing all the followers a user.
	 *
	 * @param model
	 *            model map
	 * @param userAuthenticated
	 *            The authenticated user
	 * @return the string
	 */
	@GetMapping(path = "/list")
	public final String showFollowerList(final ModelMap model, Principal userAuthenticated) {
		// Loads required data into the model
		loadViewModel(model, userAuthenticated);

		return FollowViewConstants.VIEW_FOLLOWER_LIST;
	}

	/**
	 * Loads the data required for showing all the followers of a user.
	 *
	 * @param model
	 *            model map
	 * @param userAuthenticated
	 *            the authenticated user
	 */
	private final void loadViewModel(final ModelMap model, Principal userAuthenticated) {
		UserProfile user = userProfileRepository.findOneByEmail(userAuthenticated.getName());

		try {
			model.put(FollowViewConstants.PARAM_FOLLOWERS, followService.getUserFollowers(user));
			model.put("blocksService", blocksService);
			model.put("viewer", user);
		} catch (InstanceNotFoundException e) {
			logger.log(Level.INFO, e.getMessage(), e);
		} catch (NullPointerException e) {
			logger.log(Level.INFO, e.getMessage(), e);
		}
	}

	@PostMapping("blockUserPosts")
	public final String blockPosts(@RequestParam Long user_id, final ModelMap model, Principal userAuthenticated,
			HttpSession session) {

		UserProfile user = userProfileRepository.findOneByEmail(userAuthenticated.getName());
		UserProfile userFollowed = userProfileRepository.findById(user_id).get();

		try {
			blocksService.blockPosts(user.getEmail(), userFollowed.getEmail());
		} catch (InstanceNotFoundException e) {
			logger.log(Level.INFO, e.getMessage(), e);
		} catch (AlreadyBlockedException e) {
			logger.log(Level.INFO, e.getMessage(), e);
		}

		loadViewModel(model, userAuthenticated);

		return FollowViewConstants.VIEW_FOLLOWER_LIST;
	}

	@PostMapping("blockUserStories")
	public final String blockStories(@RequestParam Long user_id, final ModelMap model, Principal userAuthenticated,
			HttpSession session) {

		UserProfile user = userProfileRepository.findOneByEmail(userAuthenticated.getName());
		UserProfile userFollowed = userProfileRepository.findById(user_id).get();

		try {
			blocksService.blockStories(user.getEmail(), userFollowed.getEmail());
		} catch (InstanceNotFoundException e) {
			logger.log(Level.INFO, e.getMessage(), e);
		} catch (AlreadyBlockedException e) {
			logger.log(Level.INFO, e.getMessage(), e);
		}

		loadViewModel(model, userAuthenticated);

		return FollowViewConstants.VIEW_FOLLOWER_LIST;
	}

	@PostMapping("unblockUserPosts")
	public final String unblockPosts(@RequestParam Long user_id, final ModelMap model, Principal userAuthenticated,
			HttpSession session) {

		UserProfile user = userProfileRepository.findOneByEmail(userAuthenticated.getName());
		UserProfile userFollowed = userProfileRepository.findById(user_id).get();

		try {
			blocksService.unblockPosts(user.getEmail(), userFollowed.getEmail());
		} catch (InstanceNotFoundException e) {
			logger.log(Level.INFO, e.getMessage(), e);
		} catch (NotBlockedYetException e) {
			logger.log(Level.INFO, e.getMessage(), e);
		}

		loadViewModel(model, userAuthenticated);

		return FollowViewConstants.VIEW_FOLLOWER_LIST;
	}

	@PostMapping("unblockUserStories")
	public final String unblockStories(@RequestParam Long user_id, final ModelMap model, Principal userAuthenticated,
			HttpSession session) {

		UserProfile user = userProfileRepository.findOneByEmail(userAuthenticated.getName());
		UserProfile userFollowed = userProfileRepository.findById(user_id).get();

		try {
			blocksService.unblockStories(user.getEmail(), userFollowed.getEmail());
		} catch (InstanceNotFoundException e) {
			logger.log(Level.INFO, e.getMessage(), e);
		} catch (NotBlockedYetException e) {
			logger.log(Level.INFO, e.getMessage(), e);
		}

		loadViewModel(model, userAuthenticated);

		return FollowViewConstants.VIEW_FOLLOWER_LIST;
	}

	/**
	 * Kick user from your followers
	 *
	 * @param userId
	 *            the user id
	 * @param model
	 *            the model
	 * @param userAuthenticated
	 *            the user authenticated
	 * @param session
	 *            the session
	 * @return the string
	 */
	@PostMapping("kickFollower")
	public final String kickFollower(@RequestParam Long userId, final ModelMap model, Principal userAuthenticated,
			HttpSession session) {

		String error_message = "";
		Boolean sucess = Boolean.FALSE;
		UserProfile kickedUser = userProfileRepository.getOne(userId);
		UserProfile user = userProfileRepository.findOneByEmail(userAuthenticated.getName());

		followService.unfollow(kickedUser, user);

		error_message = SUCESS_KICKED_USER;
		sucess = Boolean.TRUE;

		// Devolvemos el mensaje
		model.put("error_message", error_message);
		model.put("sucess", sucess);
		loadViewModel(model, userAuthenticated);

		return FollowViewConstants.VIEW_FOLLOWER_LIST;
	}

}