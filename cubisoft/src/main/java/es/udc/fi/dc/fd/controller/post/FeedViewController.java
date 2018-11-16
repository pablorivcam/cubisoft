package es.udc.fi.dc.fd.controller.post;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.security.Principal;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import javax.management.InstanceNotFoundException;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.udc.fi.dc.fd.controller.picture.PictureUploaderController;
import es.udc.fi.dc.fd.model.persistence.Comment;
import es.udc.fi.dc.fd.model.persistence.Picture;
import es.udc.fi.dc.fd.model.persistence.Post;
import es.udc.fi.dc.fd.model.persistence.PostView;
import es.udc.fi.dc.fd.model.persistence.UserProfile;
import es.udc.fi.dc.fd.repository.CommentRepository;
import es.udc.fi.dc.fd.repository.PictureRepository;
import es.udc.fi.dc.fd.repository.PostRepository;
import es.udc.fi.dc.fd.repository.UserProfileRepository;
import es.udc.fi.dc.fd.service.AlreadyLikedException;
import es.udc.fi.dc.fd.service.CommentService;
import es.udc.fi.dc.fd.service.LikesService;
import es.udc.fi.dc.fd.service.NotLikedYetException;
import es.udc.fi.dc.fd.service.PictureService;
import es.udc.fi.dc.fd.service.PostService;
import es.udc.fi.dc.fd.service.PostViewService;

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
	private UserProfileRepository userProfileRepository;

	@Autowired
	private PictureRepository pictureRepository;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private CommentService commentService;

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
				(user_id.isPresent()) ? user_id.get() : null);

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
	public final String showGlobalFeed(final ModelMap model, Principal userAuthenticated) {
		// Loads required data into the model

		loadViewModel(model, userAuthenticated, PostViewConstants.VIEW_GLOBAL_FEED, null);

		return PostViewConstants.VIEW_GLOBAL_FEED;
	}

	private final PostService getPostService() {
		return postService;
	}

	private final PostViewService getPostViewService() {
		return postViewService;
	}

	private final void loadViewModel(final ModelMap model, Principal userAuthenticated, String view, Long user_id) {
		UserProfile user = null;
		if (userAuthenticated != null) {
			user = userProfileRepository.findOneByEmail(userAuthenticated.getName());
		}
		try {

			List<Post> posts = null;

			if (view.equals(PostViewConstants.VIEW_GLOBAL_FEED)) {
				posts = getPostService().findFollowsAndUserPosts(user);
			} else {
				if (user != null) {
					if (user_id == null || user_id == user.getUser_id()) {
						posts = getPostService().findUserPosts(user);
					} else {
						UserProfile userFound = userProfileRepository.findById(user_id).get();
						posts = getPostService().findUserPosts(userFound);
						model.put("userFound", userFound);
					}
				} else {
					/* The user is anonymous */
					UserProfile userFound = userProfileRepository.findById(user_id).get();
					posts = getPostService().findUserPosts(userFound);
					model.put("userFound", userFound);
				}
			}
			if (user != null) {
				for (Post post : posts) {
					if (getPostViewService().findPostViewByPostUser(post, user) == null) {
						getPostViewService().save(new PostView(user, post));
					}
				}

				model.put("currentUser", user);
			} else {
				for (Post post : posts) {
					post.setAnonymousViews(post.getAnonymousViews() + 1);
					postService.save(post);
				}
			}

			model.put("likesService", likesService);
			model.put(PostViewConstants.PARAM_POSTS, posts);
			model.put("postService", getPostService());
			model.put(PostViewConstants.PARAM_POSTVIEWS, getPostViewService().findPostsViews(posts));
			model.put("commentService", commentService);
		} catch (InstanceNotFoundException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
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
		Boolean sucess = false;
		Picture p = pictureRepository.findById(modifyId).get();

		// FIXME: de esta manera si modificamos una imagen desde el perfil de
		// otro
		// usuario nos lleva al nuestro.

		// Modificamos la descripcion de la imagen en la BD
		// pictureService.modifyPicture(p, pictureDescription);
		p.setDescription(description);
		pictureService.save(p);
		error_message = SUCESS_EDITED_PICTURE;
		sucess = true;

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

			if (post.getPicture().getAuthor().getUser_id() == author.getUser_id()) {

				pictureService.delete(post.getPicture());

				String folderPath = session.getServletContext().getRealPath("/")
						+ PictureUploaderController.UPLOADS_FOLDER_NAME;

				String imagePath = folderPath + "/" + post.getPicture().getImage_path();
				File pictureFile = new File(imagePath);

				if (pictureFile.exists()) {
					boolean deleted = pictureFile.delete();
					if (!deleted) {
						error_message = PICTURE_DELETE_ERROR;
					}
				}

			}

			if (!error_message.equals("")) {
				error_message = SUCESS_DELETED_POST;
				sucess = true;
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

		Post post = postRepository.findById(postId).get();
		UserProfile author = userProfileRepository.findOneByEmail(userAuthenticated.getName());
		String error_message = "";
		Boolean sucess = false;

		if (post == null) {
			error_message = POST_NOT_FOUND_ERROR;
		} else if (author == null) {
			error_message = USER_NOT_FOUND_ERROR;
		} else if (likesService.existLikes(author, post)) {
			error_message = ALREADY_LIKED_POST_ERROR;
		} else {
			try {
				likesService.newLikes(author, post);
				post.setNumber_of_likes(post.getNumber_of_likes() + 1);
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

		Post post = postRepository.findById(postId).get();
		UserProfile author = userProfileRepository.findOneByEmail(userAuthenticated.getName());
		String error_message = "";
		Boolean sucess = false;

		if (post == null) {
			error_message = POST_NOT_FOUND_ERROR;
		} else if (author == null) {
			error_message = USER_NOT_FOUND_ERROR;
		} else if (!likesService.existLikes(author, post)) {
			error_message = POST_NOT_LIKED_YET_ERROR;
		} else {
			try {
				likesService.deleteUserPostLikes(author, post);
				post.setNumber_of_likes(post.getNumber_of_likes() - 1);
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

		Post post = postRepository.findById(postId).get();
		UserProfile author = userProfileRepository.findOneByEmail(userAuthenticated.getName());
		String error_message = "";
		boolean sucess = false;

		if (post == null) {
			error_message = POST_NOT_FOUND_ERROR;
		} else if (author == null) {
			error_message = USER_NOT_FOUND_ERROR;
		} else {
			try {
				postService.newReshare(post, author);
			} catch (InstanceNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			error_message = SUCESS_RESHARE_POST;
			sucess = true;
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

		Post p = postRepository.findById(postId).get();
		UserProfile author = userProfileRepository.findOneByEmail(userAuthenticated.getName());

		if (p != null) {

			Comment c = new Comment(text, Calendar.getInstance(), p, author, null);
			commentService.save(c);

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

		Comment c = commentService.findCommentByCommentId(commentId);
		UserProfile author = userProfileRepository.findOneByEmail(userAuthenticated.getName());

		if (c != null) {
			commentService.replyComment(c, text, author, Calendar.getInstance());
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
		Boolean sucess = Boolean.FALSE;
		Comment c = commentService.findCommentByCommentId(modifyCommentId);

		// Modifies the comment in DB
		if (c != null) {
			c.setText(newContent);
			commentService.save(c);
			error_message = SUCESS_EDITED_COMMENT;
			sucess = Boolean.TRUE;
		}

		// Devolvemos el mensaje
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
		Comment comment = commentRepository.findById(deleteCommentId).get();
		UserProfile author = userProfileRepository.findOneByEmail(userAuthenticated.getName());

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