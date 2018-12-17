package es.udc.fi.dc.fd.controller.post;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.InstanceNotFoundException;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import es.udc.fi.dc.fd.model.persistence.Comment;
import es.udc.fi.dc.fd.model.persistence.Post;
import es.udc.fi.dc.fd.model.persistence.UserProfile;
import es.udc.fi.dc.fd.repository.PostRepository;
import es.udc.fi.dc.fd.service.CommentService;
import es.udc.fi.dc.fd.service.LikesService;
import es.udc.fi.dc.fd.service.NotLikedYetException;
import es.udc.fi.dc.fd.service.PictureService;
import es.udc.fi.dc.fd.service.PostService;
import es.udc.fi.dc.fd.service.PostViewService;
import es.udc.fi.dc.fd.service.UserProfileService;

@Controller
@RequestMapping("/post")
public class FeedViewController {

	/** The Constant IMAGE_NOT_FOUND_ERROR. */
	public static final String POST_NOT_FOUND_ERROR = "That post doesn't exist.";

	/** The Constant NO_PERMISSION_TO_DELETE. */
	public static final String NO_PERMISSION_TO_DELETE = "You dont have permission to delete this post";

	/** The Constant NO_PERMISSION_TO_DELETE_COMMENT. */
	public static final String NO_PERMISSION_TO_DELETE_COMMENT = "You dont have permission to delete this comment";

	/** The Constant SUCESS_DELETED_PICTURE. */
	public static final String SUCESS_DELETED_POST = "The post has been deleted sucessfully.";

	public static final String SUCESS_LIKED_POST = "The post has been liked sucessfully.";

	public static final String SUCESS_UNLIKED_POST = "The post has been unliked sucessfully.";

	public static final String USER_NOT_FOUND_ERROR = "That user doesn't exist.";

	public static final String COMMENT_NOT_FOUND_ERROR = "The comment doesn't exist";

	public static final String ALREADY_LIKED_POST_ERROR = "That post was already liked.";

	public static final String PICTURE_DELETE_ERROR = "Error deleting the picture";

	public static final String POST_NOT_LIKED_YET_ERROR = "That post isn't liked yet.";

	/** The Constant SUCESS_DELETED_PICTURE. */
	public static final String SUCESS_EDITED_PICTURE = "The picture has been modified sucessfully.";

	/** The Constant SUCESS_RESHARE_POST. */
	public static final String SUCESS_RESHARE_POST = "The post has been reshared sucessfully.";

	/** The Constant SUCESS_DELETED_PICTURE. */
	public static final String SUCESS_EDITED_COMMENT = "The comment has been edited sucessfully.";

	public static final String SUCESS_DELETED_COMMENT = "The comment has been deleted sucessfully.";

	@Autowired
	private final PostService postService;

	@Autowired
	private final PostViewService postViewService;

	@Autowired
	private final LikesService likesService;

	@Autowired
	private final PictureService pictureService;

	@Autowired
	private UserProfileService userProfileService;

	@Autowired
	private CommentService commentService;

	@Autowired
	private PostRepository postRepository;

	private final static Logger logger = Logger.getLogger(FeedViewController.class.getName());

	@Autowired
	public FeedViewController(final PostService service, final PictureService servicePicture,
			final LikesService serviceLikes, final PostViewService servicePostView) {

		postService = checkNotNull(service, "Received a null pointer as service");
		pictureService = checkNotNull(servicePicture, "Received a null pointer as service");
		likesService = checkNotNull(serviceLikes, "Received a null pointer as service");
		postViewService = checkNotNull(servicePostView, "Received a null pointer as service");
	}

	/**
	 * Show my feed.
	 *
	 * @param model
	 *            the model
	 * @param userAuthenticated
	 *            the user authenticated
	 * @param user_id
	 *            the user id
	 * @return the string
	 */
	@GetMapping(path = "/myFeed{user_id}")
	public final String showMyFeed(final ModelMap model, Principal userAuthenticated,
			@RequestParam("user_id") Optional<Long> user_id) {
		loadViewModel(model, userAuthenticated, PostViewConstants.VIEW_POST_LIST,
				(user_id.isPresent() ? user_id.get() : null));

		return PostViewConstants.VIEW_POST_LIST;
	}

	/**
	 * Show global feed.
	 *
	 * @param model
	 *            the model
	 * @param userAuthenticated
	 *            the user authenticated
	 * @return the string
	 */
	@GetMapping(path = "/globalFeed")
	public final String showGlobalFeed(final ModelMap model, Principal userAuthenticated,
			@RequestParam(value = "hashtags", required = false) String[] hashtags) {
		// Loads required data into the model
		List<Post> posts = new ArrayList<>();
		UserProfile userFound = null;

		if (hashtags != null && hashtags.length > 0) {
			String tags = "", hashtagsText = "";

			for (int i = 0; i < hashtags.length; i++) {
				if (i != 0) {
					tags = tags.concat(",");
				}
				tags = tags.concat(hashtags[i]);
				hashtagsText = hashtagsText.concat("#" + hashtags[i] + " ");
			}

			model.put("hashtagsText", hashtagsText);

			String url = "http://{enpointUrl}?hashtags={hashtags}&user={user}";
			URI expanded = new UriTemplate(url).expand("localhost:8080/cubisoft/rest/post", tags,
					userAuthenticated.getName()); // this is what

			try {
				url = URLDecoder.decode(expanded.toString(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				logger.log(Level.INFO, e.getMessage(), e);
			}

			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<Post[]> responseEntity = restTemplate.getForEntity(url, Post[].class);

			for (Post p : responseEntity.getBody()) {
				posts.add(postRepository.findPostByPostId(p.getPost_id()));
			}

		} else {
			posts = postService.loadFeed(null, userAuthenticated, PostViewConstants.VIEW_GLOBAL_FEED);

		}

		if (userAuthenticated != null) {
			UserProfile user = userProfileService.findUserByEmail(userAuthenticated.getName());
			model.put("currentUser", user);
			userFound = user;
		}

		model.put("userFound", userFound);
		model.put("likesService", likesService);
		model.put(PostViewConstants.PARAM_POSTS, posts);
		model.put("postService", getPostService());
		model.put("pictureService", pictureService);
		model.put(PostViewConstants.PARAM_POSTVIEWS, postViewService.findPostsViews(posts));
		model.put("commentService", commentService);

		return PostViewConstants.VIEW_GLOBAL_FEED;
	}

	private final PostService getPostService() {
		return postService;
	}

	private final void loadViewModel(final ModelMap model, Principal userAuthenticated, String view, Long user_id) {
		/*
		 * pillo los posts del usuario con id = user_id si no estoy autenticado,
		 * userFound = user_id si estoy autenticado currentUser = yo si quiero ver el
		 * feed global userFound = yo
		 * 
		 */
		List<Post> posts = postService.loadFeed(user_id, userAuthenticated, view);

		UserProfile userFound = null;

		if (userAuthenticated != null) {
			UserProfile user = userProfileService.findUserByEmail(userAuthenticated.getName());
			model.put("currentUser", user);
			if (PostViewConstants.VIEW_GLOBAL_FEED.equals(view)) {
				userFound = user;
			} else if (user_id != null) {
				userFound = userProfileService.findById(user_id);
			}
		} else if (!PostViewConstants.VIEW_GLOBAL_FEED.equals(view)) {
			userFound = userProfileService.findById(user_id);
		}
		model.put("userFound", userFound);

		model.put("likesService", likesService);
		model.put(PostViewConstants.PARAM_POSTS, posts);
		model.put("postService", getPostService());
		model.put("pictureService", pictureService);
		model.put(PostViewConstants.PARAM_POSTVIEWS, postViewService.findPostsViews(posts));
		model.put("commentService", commentService);

	}

	/**
	 * Modify image description.
	 *
	 * @param modifyId
	 *            the modify id
	 * @param view
	 *            the view
	 * @param description
	 *            the description
	 * @param model
	 *            the model
	 * @param userAuthenticated
	 *            the user authenticated
	 * @param session
	 *            the session
	 * @return the string
	 */
	@PostMapping("modifyPicture")
	public final String modifyImageDescription(@RequestParam Long modifyId, @RequestParam String view,
			@RequestParam String description, final ModelMap model, Principal userAuthenticated, HttpSession session) {

		String error_message = "";
		Boolean sucess = Boolean.FALSE;

		try {
			pictureService.modifyPictureDescription(modifyId, description);
		} catch (InstanceNotFoundException e) {
			logger.log(Level.INFO, e.getMessage(), e);
		}

		error_message = SUCESS_EDITED_PICTURE;
		sucess = Boolean.TRUE;

		// Devolvemos el mensaje
		model.put("error_message", error_message);
		model.put("sucess", sucess);
		loadViewModel(model, userAuthenticated, view, null);

		return view;
	}

	/**
	 * Delete post post mapping.
	 *
	 * @param view
	 *            the view
	 * @param postId
	 *            the post id
	 * @param model
	 *            the model
	 * @param userAuthenticated
	 *            the user authenticated
	 * @param session
	 *            the session
	 * @return the string
	 */
	@PostMapping("deletePost")
	public final String deletePost(@RequestParam String view, @RequestParam Long postId, final ModelMap model,
			Principal userAuthenticated, HttpSession session) {

		String error_message = "";
		Boolean sucess = Boolean.FALSE;
		Post post = postService.findByID(postId);
		UserProfile author = userProfileService.findUserByEmail(userAuthenticated.getName());

		// Eliminamos la imagen
		if (post == null) {
			error_message = POST_NOT_FOUND_ERROR;
		} else if (post.getUser().getUser_id() != author.getUser_id()) {
			error_message = NO_PERMISSION_TO_DELETE;
		} else {
			// Eliminamos la imagen de la BD
			error_message = postService.deletePost(session.getServletContext().getRealPath("/"), post);

			if (!"".equals(error_message)) {
				error_message = SUCESS_DELETED_POST;
				sucess = Boolean.TRUE;
			}
		}

		// Devolvemos el mensaje
		model.put("error_message", error_message);
		model.put("sucess", sucess);

		loadViewModel(model, userAuthenticated, view, null);

		return view;

	}

	/**
	 * Like post postmapping.
	 *
	 * @param view
	 *            the view
	 * @param postId
	 *            the post id
	 * @param model
	 *            the model
	 * @param userAuthenticated
	 *            the user authenticated
	 * @param session
	 *            the http session
	 * @return string
	 */
	@PostMapping("likePost")
	public final String likePost(@RequestParam String view, @RequestParam Long postId, final ModelMap model,
			Principal userAuthenticated, HttpSession session) {

		String error_message = "";

		try {
			error_message = likesService.likePost(postId, userAuthenticated.getName());
		} catch (InstanceNotFoundException e1) {
			logger.log(Level.INFO, e1.getMessage(), e1);
		}

		Boolean sucess = SUCESS_LIKED_POST.equals(error_message);

		model.put("error_message", error_message);
		model.put("sucess", sucess);
		loadViewModel(model, userAuthenticated, view, null);

		return view;
	}

	/**
	 * Unlike post postmapping.
	 *
	 * @param view
	 *            the view
	 * @param postId
	 *            the post id
	 * @param model
	 *            the model
	 * @param userAuthenticated
	 *            the user authenticated
	 * @param session
	 *            the http session
	 * @return string
	 */
	@PostMapping("unlikePost")
	public final String unlikePost(@RequestParam String view, @RequestParam Long postId, final ModelMap model,
			Principal userAuthenticated, HttpSession session) {

		String error_message = "";
		try {
			error_message = likesService.unlikePost(postId, userAuthenticated.getName());
		} catch (InstanceNotFoundException e) {
			logger.log(Level.INFO, e.getMessage(), e);
		} catch (NotLikedYetException e) {
			logger.log(Level.INFO, e.getMessage(), e);
		}
		Boolean sucess = SUCESS_UNLIKED_POST.equals(error_message);

		model.put("error_message", error_message);
		model.put("sucess", sucess);
		loadViewModel(model, userAuthenticated, view, null);

		return view;
	}

	/**
	 * Reshare post postmapping.
	 *
	 * @param postId
	 *            the post id
	 * @param model
	 *            the model
	 * @param userAuthenticated
	 *            the user authenticated
	 * @param session
	 *            the http session
	 * @return string
	 */
	@PostMapping("resharePost")
	public final String resharePost(@RequestParam Long postId, final ModelMap model, Principal userAuthenticated,
			HttpSession session) {

		Post post = postService.findByID(postId);
		UserProfile author = userProfileService.findUserByEmail(userAuthenticated.getName());

		String error_message = "";
		Boolean sucess = Boolean.FALSE;

		if (post == null) {
			error_message = POST_NOT_FOUND_ERROR;
		} else if (author == null) {
			error_message = USER_NOT_FOUND_ERROR;
		} else {
			try {
				postService.newReshare(post, author);
			} catch (InstanceNotFoundException e) {
				logger.log(Level.INFO, e.getMessage(), e);
			}
			error_message = SUCESS_RESHARE_POST;
			sucess = Boolean.TRUE;
		}
		model.put("currentUser", author);
		model.put("likesService", likesService);
		model.put("error_message", error_message);
		model.put("sucess", sucess);
		loadViewModel(model, userAuthenticated, PostViewConstants.VIEW_GLOBAL_FEED, null);

		return PostViewConstants.VIEW_GLOBAL_FEED;
	}

	/**
	 * Adds the comment.
	 *
	 * @param model
	 *            the model
	 * @param userAuthenticated
	 *            the user authenticated
	 * @param view
	 *            the view
	 * @param postId
	 *            the post id
	 * @param text
	 *            the text
	 * @return the string
	 */
	@PostMapping("addComment")
	public final String addComment(final ModelMap model, Principal userAuthenticated, @RequestParam String view,
			@RequestParam Long postId, @RequestParam String text) {

		try {
			commentService.addComment(text, postId, userAuthenticated.getName());
		} catch (InstanceNotFoundException e) {
			logger.log(Level.INFO, e.getMessage(), e);
		}

		loadViewModel(model, userAuthenticated, view, null);

		return view;
	}

	/**
	 * Replys the comment.
	 *
	 * @param model
	 *            the model
	 * @param userAuthenticated
	 *            the user authenticated
	 * @param view
	 *            the view
	 * @param commentId
	 *            the comment id
	 * @param text
	 *            the text
	 * @return the string
	 */
	@PostMapping("replyComment")
	public final String replyComment(final ModelMap model, Principal userAuthenticated, @RequestParam String view,
			@RequestParam Long commentId, @RequestParam String text) {

		try {
			commentService.replyComment(commentId, text, userAuthenticated.getName(), Calendar.getInstance());
		} catch (InstanceNotFoundException e) {
			logger.log(Level.INFO, e.getMessage(), e);
		}

		loadViewModel(model, userAuthenticated, view, null);

		return view;

	}

	/**
	 * Edit a comment.
	 *
	 * @param modifyCommentId
	 *            the modify id
	 * @param view
	 *            the view
	 * @param newContent
	 *            the new content for the commentary
	 * @param model
	 *            the model
	 * @param userAuthenticated
	 *            the user authenticated
	 * @param session
	 *            the session
	 * @return the string
	 */
	@PostMapping("modifyComment")
	public final String modifyComment(@RequestParam Long modifyCommentId, @RequestParam String view,
			@RequestParam String newContent, final ModelMap model, Principal userAuthenticated, HttpSession session) {

		String error_message = "";
		boolean sucess = false;

		Comment c = null;

		try {
			c = commentService.modifyComment(modifyCommentId, newContent);
		} catch (InstanceNotFoundException e) {
			logger.log(Level.INFO, e.getMessage(), e);
		}

		// Modifies the comment in DB
		if (c != null) {
			error_message = SUCESS_EDITED_COMMENT;
			sucess = true;
		}

		// Return the message
		model.put("error_message", error_message);
		model.put("sucess", sucess);
		loadViewModel(model, userAuthenticated, view, null);

		return view;
	}

	/**
	 * Delete comment post mapping.
	 *
	 * @param view
	 *            the view
	 * @param deleteCommentId
	 *            the comment id
	 * @param model
	 *            the model
	 * @param userAuthenticated
	 *            the user authenticated
	 * @param session
	 *            the session
	 * @return the string
	 */
	@PostMapping("deleteComment")
	public final String deleteComment(@RequestParam String view, @RequestParam Long deleteCommentId,
			final ModelMap model, Principal userAuthenticated, HttpSession session) {

		String error_message = "";
		Boolean sucess = Boolean.FALSE;
		Comment comment = commentService.findById(deleteCommentId);
		UserProfile author = userProfileService.findUserByEmail(userAuthenticated.getName());

		// We delete the comment
		if (comment == null) {
			error_message = COMMENT_NOT_FOUND_ERROR;
		} else if (comment.getUser().getUser_id() != author.getUser_id()) {
			error_message = NO_PERMISSION_TO_DELETE_COMMENT;
		} else {
			// Remove it from our DB
			commentService.delete(comment);
			error_message = SUCESS_DELETED_COMMENT;
			sucess = Boolean.TRUE;
		}

		// We return the message
		model.put("error_message", error_message);
		model.put("sucess", sucess);

		loadViewModel(model, userAuthenticated, view, null);

		return view;

	}

}