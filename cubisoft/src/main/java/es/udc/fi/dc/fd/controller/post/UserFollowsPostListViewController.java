package es.udc.fi.dc.fd.controller.post;

import static com.google.common.base.Preconditions.checkNotNull;

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
import es.udc.fi.dc.fd.service.AlreadyLikedException;
import es.udc.fi.dc.fd.service.LikesService;
import es.udc.fi.dc.fd.service.NotLikedYetException;
import es.udc.fi.dc.fd.service.PostService;

@Controller
@RequestMapping("/followsPost")
public class UserFollowsPostListViewController {
	
	/** The Constant IMAGE_NOT_FOUND_ERROR. */
	public static final String POST_NOT_FOUND_ERROR = "That post doesn't exist.";

	/** The Constant NO_PERMISSION_TO_DELETE. */
	public static final String NO_PERMISSION_TO_DELETE = "You dont have permission to delete this post";

	/** The Constant SUCESS_DELETED_PICTURE. */
	public static final String SUCESS_DELETED_POST = "The post has been deleted sucessfully.";
	
	public static final String SUCESS_LIKED_POST = "The post has been liked sucessfully.";
	
	public static final String SUCESS_UNLIKED_POST = "The post has been unliked sucessfully.";
	
	public static final String USER_NOT_FOUND_ERROR = "That user doesn't exist.";
	
	public static final String ALREADY_LIKED_POST_ERROR= "That post was already liked.";
	
	public static final String POST_NOT_LIKED_YET_ERROR = "That post isn't liked yet.";


	@Autowired
	private final PostService postService;
	
	@Autowired
	private final LikesService likesService;

	@Autowired
	private UserProfileRepository userProfileRepository;
	
	@Autowired
	private PostRepository postRepository;

	@Autowired
	public UserFollowsPostListViewController(final PostService service, final LikesService serviceLikes) {
		super();

		postService = checkNotNull(service, "Received a null pointer as service");
		likesService = checkNotNull(serviceLikes, "Received a null pointer as service");
	}

	@GetMapping(path = "/list")
	public final String showPostList(final ModelMap model, Principal userAuthenticated) {
		// Loads required data into the model
		UserProfile user = userProfileRepository.findOneByEmail(userAuthenticated.getName());
		model.put("currentUser", user);
		model.put("likesService", likesService);
		loadViewModel(model, userAuthenticated);

		return PostViewConstants.VIEW_FOLLOWS_POST_LIST;
	}

	private final PostService getPostService() {
		return postService;
	}

	private final void loadViewModel(final ModelMap model, Principal userAuthenticated) {
		UserProfile user = userProfileRepository.findOneByEmail(userAuthenticated.getName());

		try {
			model.put(PostViewConstants.PARAM_FOLLOWS_POSTS, getPostService().findUserFollowsPosts(user));
		} catch (InstanceNotFoundException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Like post postmapping
	 * @param postId
	 * 			the post id
	 * @param model
	 * 			the model
	 * @param userAuthenticated
	 * 			the user authenticated
	 * @param session 
	 * 			the http session
	 * @return string
	 * @throws InstanceNotFoundException 
	 * @throws AlreadyLikedException 
	 */
	@PostMapping("likePost")
	public final String likePost(@RequestParam Long postId, final ModelMap model, 
			Principal userAuthenticated, HttpSession session){
		
		Post post = postRepository.findById(postId).get();
		UserProfile author = userProfileRepository.findOneByEmail(userAuthenticated.getName());
		String error_message = "";
		Boolean sucess = false;
		
		if (post == null) {
			error_message = POST_NOT_FOUND_ERROR;
		}else if(author == null){
			error_message = USER_NOT_FOUND_ERROR;
		}else if (likesService.existLikes(author, post)){
			error_message = ALREADY_LIKED_POST_ERROR;
		}else{
			try {
				likesService.newLikes(author, post);
				post.setNumber_of_likes(post.getNumber_of_likes()+1);
				postService.save(post);
			} catch (InstanceNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AlreadyLikedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			error_message = SUCESS_LIKED_POST;
			sucess = true;
		}
		model.put("currentUser", author);
		model.put("likesService", likesService);
		model.put("error_message", error_message);
		model.put("sucess", sucess);
		loadViewModel(model, userAuthenticated);

		return PostViewConstants.VIEW_FOLLOWS_POST_LIST;
	}
	
	/**
	 * Unlike post postmapping
	 * @param postId
	 * 			the post id
	 * @param model
	 * 			the model
	 * @param userAuthenticated
	 * 			the user authenticated
	 * @param session 
	 * 			the http session
	 * @return string
	 */
	@PostMapping("unlikePost")
	public final String unlikePost(@RequestParam Long postId, final ModelMap model, 
			Principal userAuthenticated, HttpSession session) {
		
		Post post = postRepository.findById(postId).get();
		UserProfile author = userProfileRepository.findOneByEmail(userAuthenticated.getName());
		String error_message = "";
		Boolean sucess = false;
		
		if (post == null) {
			error_message = POST_NOT_FOUND_ERROR;
		}else if(author == null){
			error_message = USER_NOT_FOUND_ERROR;
		}else if (!likesService.existLikes(author, post)){
			error_message = POST_NOT_LIKED_YET_ERROR;
		}else{
			try {
				likesService.deleteUserPostLikes(author, post);
				post.setNumber_of_likes(post.getNumber_of_likes()-1);
				postService.save(post);
			} catch (InstanceNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NotLikedYetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			error_message = SUCESS_UNLIKED_POST;
			sucess = true;
		}
		model.put("currentUser", author);
		model.put("likesService", likesService);
		model.put("error_message", error_message);
		model.put("sucess", sucess);
		loadViewModel(model, userAuthenticated);

		return PostViewConstants.VIEW_FOLLOWS_POST_LIST;
	}

}