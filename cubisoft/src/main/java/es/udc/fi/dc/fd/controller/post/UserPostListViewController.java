package es.udc.fi.dc.fd.controller.post;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.security.Principal;

import javax.management.InstanceNotFoundException;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.udc.fi.dc.fd.model.persistence.Post;
import es.udc.fi.dc.fd.model.persistence.UserProfile;
import es.udc.fi.dc.fd.repository.UserProfileRepository;
import es.udc.fi.dc.fd.repository.PostRepository;
import es.udc.fi.dc.fd.service.PostService;

@Controller
@RequestMapping("/post")
public class UserPostListViewController {
	
	/** The Constant IMAGE_NOT_FOUND_ERROR. */
	public static final String POST_NOT_FOUND_ERROR = "That post doesn't exist.";

	/** The Constant NO_PERMISSION_TO_DELETE. */
	public static final String NO_PERMISSION_TO_DELETE = "You dont have permission to delete this post";

	/** The Constant SUCESS_DELETED_PICTURE. */
	public static final String SUCESS_DELETED_POST = "The post has been deleted sucessfully.";

	@Autowired
	private final PostService postService;

	@Autowired
	private UserProfileRepository userProfileRepository;
	
	@Autowired
	private PostRepository postRepository;

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
	
	/**
	 * Delete post post mapping.
	 *
	 * @param postId
	 *            the post id
	 * @param model
	 *            the model
	 * @param userAuthenticated
	 *            the user authenticated
	 * @return the string
	 */
	@PostMapping("deletePost")
	public final String deletePost(@RequestParam Long postId, final ModelMap model, 
			Principal userAuthenticated, HttpSession session) {
		
		String error_message = "";
		Boolean sucess = false;
		Post post = postRepository.findById(postId).get();
		UserProfile author = userProfileRepository.findOneByEmail(userAuthenticated.getName());


		// Eliminamos la imagen
		if (post == null) {
			error_message = POST_NOT_FOUND_ERROR;
		} else if (post.getUser().getUser_id() != author.getUser_id()) {
			error_message = NO_PERMISSION_TO_DELETE;
		} else {
			// Eliminamos la imagen de la BD
			postService.deletePost(post);

			error_message = SUCESS_DELETED_POST;
			sucess = true;
		}

		// Devolvemos el mensaje
		model.put("error_message", error_message);
		model.put("sucess", sucess);

		loadViewModel(model, userAuthenticated);

		return PostViewConstants.VIEW_POST_LIST;

	}

}