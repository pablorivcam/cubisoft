package es.udc.fi.dc.fd.controller.post;

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
import es.udc.fi.dc.fd.service.PostService;

@Controller
@RequestMapping("/post")
public class UserPostListViewController {

	@Autowired
	private final PostService postService;

	@Autowired
	private UserProfileRepository userProfileRepository;

	@Autowired
	public UserPostListViewController(final PostService service) {
		super();

		postService = checkNotNull(service, "Received a null pointer as service");
	}

	@GetMapping(path = "/list")
	public final String showPostList(final ModelMap model, Principal userAuthenticated) {
		// Loads required data into the model
		loadViewModel(model, userAuthenticated);

		return PostViewConstants.VIEW_POST_LIST;
	}

	private final PostService getPostService() {
		return postService;
	}

	private final void loadViewModel(final ModelMap model, Principal userAuthenticated) {
		UserProfile user = userProfileRepository.findOneByEmail(userAuthenticated.getName());

		try {
			model.put(PostViewConstants.PARAM_POSTS, getPostService().findUserPosts(user));
		} catch (InstanceNotFoundException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

}