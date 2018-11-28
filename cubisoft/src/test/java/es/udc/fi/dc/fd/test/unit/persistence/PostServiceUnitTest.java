package es.udc.fi.dc.fd.test.unit.persistence;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import javax.management.InstanceNotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import es.udc.fi.dc.fd.controller.post.PostViewConstants;
import es.udc.fi.dc.fd.model.persistence.Picture;
import es.udc.fi.dc.fd.model.persistence.Post;
import es.udc.fi.dc.fd.model.persistence.UserProfile;
import es.udc.fi.dc.fd.model.persistence.UserProfile.UserType;
import es.udc.fi.dc.fd.repository.PictureRepository;
import es.udc.fi.dc.fd.repository.PostRepository;
import es.udc.fi.dc.fd.repository.PostViewRepository;
import es.udc.fi.dc.fd.repository.UserProfileRepository;
import es.udc.fi.dc.fd.service.PostService;

@RunWith(MockitoJUnitRunner.class)
public class PostServiceUnitTest {

	public static final String TEST_LOGIN = "test";
	public static final String TEST_FIRSTNAME = "Test";
	public static final String TEST_LASTNAME = "Test";
	public static final String TEST_PASSWORD = "abc123..";
	public static final String TEST_EMAIL = "test@test.com";

	public static final String TEST_PATH = "image.jpg";
	public static final String TEST_DESCRIPTION = "asdfgh";

	@Mock
	private PostRepository postRepository;

	@Mock
	private UserProfileRepository userProfileRepository;

	@Mock
	private PostViewRepository postViewRepository;

	@Mock
	private PictureRepository pictureRepository;

	@InjectMocks
	private PostService postService;

	private UserProfile userA, userB, userC;

	private Post postA1, postB1, postB2, postC1;

	private Picture picture;

	@Before
	public void initialize() {

		MockitoAnnotations.initMocks(this);

		userA = new UserProfile(TEST_LOGIN, TEST_FIRSTNAME, TEST_LASTNAME, TEST_PASSWORD, TEST_EMAIL, null, null,
				UserType.PUBLIC);
		userA.setUser_id(1L);
		userB = new UserProfile(TEST_LOGIN + 2, TEST_FIRSTNAME + 2, TEST_LASTNAME + 2, TEST_PASSWORD, "2" + TEST_EMAIL,
				null, null, UserType.PUBLIC);
		userB.setUser_id(2L);
		userC = new UserProfile(TEST_LOGIN + 3, TEST_FIRSTNAME + 3, TEST_LASTNAME + 3, TEST_PASSWORD, "3" + TEST_EMAIL,
				null, null, UserType.PUBLIC);
		userC.setUser_id(3L);

		picture = new Picture(TEST_DESCRIPTION, Calendar.getInstance(), TEST_PATH, userA, null);

		postA1 = new Post(Calendar.getInstance(), picture, userA, (long) 0, (long) 0, (long) 0, false);
		postB1 = new Post(Calendar.getInstance(), picture, userB, (long) 0, (long) 0, (long) 0, false);
		postB2 = new Post(Calendar.getInstance(), picture, userB, (long) 0, (long) 0, (long) 0, false);
		postC1 = new Post(Calendar.getInstance(), picture, userC, (long) 0, (long) 0, (long) 0, false);

	}

	@Test
	public void saveTest() {
		Mockito.when(postRepository.save(postA1)).thenReturn(postA1);
		;
		assertThat(postService.save(postA1), is(equalTo(postA1)));
	}

	@Test
	public void findUserFollowsPostsTest() {

		// Datos esperados por el test
		ArrayList<Post> postsA = new ArrayList<>();
		postsA.add(postB1);
		postsA.add(postB2);
		postsA.add(postC1);

		ArrayList<Post> postsC = new ArrayList<>();
		postsC.add(postA1);

		// Inicializamos lo que tienen que devolver las clases Repository en los
		// métodos
		// utilizados por el servicio en los diferentes test
		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(true);
		Mockito.when(postRepository.findUserFollowsPosts(userA)).thenReturn(postsA);

		Mockito.when(userProfileRepository.exists("3" + TEST_EMAIL)).thenReturn(true);
		Mockito.when(postRepository.findUserFollowsPosts(userC)).thenReturn(postsC);

		// Ejecutamos los test
		try {
			assertThat(postService.findUserFollowsPosts(userA), is(equalTo(postsA)));
			assertThat(postService.findUserFollowsPosts(userC), is(equalTo(postsC)));

		} catch (InstanceNotFoundException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

	}

	@Test(expected = NullPointerException.class)
	public void findNullUserFollowersTest() throws InstanceNotFoundException {
		assertThat(postService.findUserFollowsPosts(null), is(equalTo(null)));
	}

	@Test(expected = InstanceNotFoundException.class)
	public void findUnexistentUserFollowersTest() throws InstanceNotFoundException {
		assertThat(postService.findUserFollowsPosts(new UserProfile()), is(equalTo(null)));
	}

	@Test
	public void findUserPostsTest() { // Datos esperados por el test
		ArrayList<Post> postsB = new ArrayList<>();
		postsB.add(postB1);
		postsB.add(postB2);

		Mockito.when(userProfileRepository.exists("2" + TEST_EMAIL)).thenReturn(true);
		Mockito.when(postRepository.findUserPosts(userB)).thenReturn(postsB);

		try {
			assertThat(postService.findUserPosts(userB), is(equalTo(postsB)));
		} catch (

		InstanceNotFoundException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	@Test(expected = NullPointerException.class)
	public void findNullUserPostsTest() throws InstanceNotFoundException {
		postService.findUserPosts(null);
	}

	@Test(expected = InstanceNotFoundException.class)
	public void findNonExistentUserPostsTest() throws InstanceNotFoundException {
		UserProfile user = new UserProfile();
		user.setEmail("");
		Mockito.when(userProfileRepository.exists(user.getEmail())).thenReturn(false);

		postService.findUserPosts(user);
	}

	@Test
	public void findFollowsAndUserPostsTest() {
		// debería meter también un post de un follow
		ArrayList<Post> postsB = new ArrayList<>();
		postsB.add(postB1);
		postsB.add(postB2);

		Mockito.when(userProfileRepository.exists("2" + TEST_EMAIL)).thenReturn(true);
		Mockito.when(postRepository.findFollowsAndUserPosts(userB)).thenReturn(postsB);

		try {
			assertThat(postService.findFollowsAndUserPosts(userB), is(equalTo(postsB)));
		} catch (

		InstanceNotFoundException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	@Test(expected = NullPointerException.class)
	public void findFollowsAndNullUserPostsTest() throws InstanceNotFoundException {
		postService.findFollowsAndUserPosts(null);
	}

	@Test(expected = InstanceNotFoundException.class)
	public void findFollowsAndNonExistentUserPostsTest() throws InstanceNotFoundException {
		UserProfile user = new UserProfile();
		user.setEmail("");
		Mockito.when(userProfileRepository.exists(user.getEmail())).thenReturn(false);

		postService.findFollowsAndUserPosts(user);
	}

	@Test
	public void newPostTest() throws InstanceNotFoundException {
		// Datos esperados por el test
		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(true);
		Mockito.when(userProfileRepository.exists("2" + TEST_EMAIL)).thenReturn(true);
		Mockito.when(userProfileRepository.exists("3" + TEST_EMAIL)).thenReturn(true);
		Post post = postService.newPost(picture, userA);
		Post post2 = postService.newPost(picture, userB);
		Post post3 = postService.newPost(picture, userC);

		// Inicializamos lo que tienen que devolver las clases Repository en los
		// métodos utilizados por el servicio en los diferentes test
		Mockito.when(postRepository.findPostByPostId(post.getPost_id())).thenReturn(post);
		Mockito.when(postRepository.findPostByPostId(post2.getPost_id())).thenReturn(post2);
		Mockito.when(postRepository.findPostByPostId(post3.getPost_id())).thenReturn(post3);

		// Realizamos comprobaciones
		assertEquals(postRepository.findPostByPostId(post.getPost_id()).getPost_id(), post.getPost_id());
		assertEquals(postRepository.findPostByPostId(post2.getPost_id()).getPost_id(), post2.getPost_id());
		assertEquals(postRepository.findPostByPostId(post3.getPost_id()).getPost_id(), post3.getPost_id());
	}

	@Test(expected = NullPointerException.class)
	public void newNullUserPostTest() throws InstanceNotFoundException {
		assertThat(postService.newPost(picture, null), is(equalTo(null)));
	}

	@Test(expected = NullPointerException.class)
	public void newNullPicturePostTest() throws InstanceNotFoundException {
		assertThat(postService.newPost(null, null), is(equalTo(null)));
	}

	@Test(expected = InstanceNotFoundException.class)
	public void newUnexistentUserPostTest() throws InstanceNotFoundException {
		assertThat(postService.newPost(picture, new UserProfile()), is(equalTo(null)));
	}

	// TODO Revisar para un reshare.
	@Test
	public void deletePostTest() throws InstanceNotFoundException {
		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(true);
		Mockito.when(userProfileRepository.exists("2" + TEST_EMAIL)).thenReturn(true);
		Post post = postService.newPost(picture, userA);
		Post post2 = postService.newPost(picture, userB);

		picture.setPicture_id(2L);

		Mockito.when(postRepository.findPostByPostId(post.getPost_id())).thenReturn(post);

		postService.deletePost("", post);
		postService.deletePost("", post2);

		Mockito.when(postRepository.findPostByPostId(post.getPost_id())).thenReturn(null);
		Mockito.when(postRepository.findPostByPostId(post2.getPost_id())).thenReturn(null);

		assertEquals(postRepository.findPostByPostId(post.getPost_id()), null);
		assertEquals(postRepository.findPostByPostId(post2.getPost_id()), null);
	}

	@Test
	public void newReshareTest() throws InstanceNotFoundException {
		// Datos esperados por el test
		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(true);
		Mockito.when(userProfileRepository.exists("2" + TEST_EMAIL)).thenReturn(true);
		Post post = postService.newPost(picture, userA);

		Post resharepost = postService.newReshare(post, userB);

		// Inicializamos lo que tienen que devolver las clases Repository en los
		// métodos utilizados por el servicio en los diferentes test
		Mockito.when(postRepository.findPostByPostId(resharepost.getPost_id())).thenReturn(resharepost);

		// Realizamos comprobaciones
		assertEquals(postRepository.findPostByPostId(resharepost.getPost_id()).getPost_id(), resharepost.getPost_id());
	}

	@Test(expected = NullPointerException.class)

	public void newReshareNullUserTest() throws InstanceNotFoundException {
		postService.newReshare(postA1, null);
	}

	@Test(expected = InstanceNotFoundException.class)
	public void newReshareNonExistentUserTest() throws InstanceNotFoundException {
		UserProfile user = new UserProfile();
		user.setEmail("");
		Mockito.when(userProfileRepository.exists(user.getEmail())).thenReturn(false);

		postService.newReshare(postA1, user);

	}

	@Test
	public void getSetPostTest() {
		Calendar date = Calendar.getInstance();

		Post postD = new Post();
		postD.setPicture(picture);
		postD.setUser(userA);
		postD.setDate(date);
		postD.setViews(0L);
		postD.setAnonymousViews(0L);
		postD.setReshare(false);

		Post postE = new Post(date, picture, userA, (long) 0, (long) 0, (long) 0, false);

		assertEquals(postD.getPicture(), postE.getPicture());
		assertEquals(postD.getDate(), postE.getDate());
		assertEquals(postD.getUser(), postE.getUser());
		assertEquals(postD.getViews(), postE.getViews());
		assertEquals(postD.getAnonymousViews(), postE.getAnonymousViews());
		assertEquals(postD.getReshare(), postE.getReshare());
	}

	@Test
	public void loadGlobalFeedTest() {

		List<Post> expected = new ArrayList<>();
		expected.add(postA1);
		expected.add(postB1);
		expected.add(postB2);
		expected.add(postC1);

		Principal userAuthenticated = new Principal() {

			@Override
			public String getName() {
				return userA.getEmail();
			}
		};

		Mockito.when(userProfileRepository.findOneByEmail(userAuthenticated.getName())).thenReturn(userA);
		Mockito.when(userProfileRepository.exists(userAuthenticated.getName())).thenReturn(true);
		Mockito.when(postRepository.findFollowsAndUserPosts(userA)).thenReturn(expected);

		for (Post p : expected) {
			Mockito.when(postViewRepository.findPostView(p, userA)).thenReturn(null);
		}

		List<Post> response = postService.loadFeed(0L, userAuthenticated, PostViewConstants.VIEW_GLOBAL_FEED);

		assertThat(response, is(equalTo(expected)));
		assertThat(response.size(), is(equalTo(expected.size())));
		assertThat(response.get(2), is(equalTo(expected.get(2))));
	}

	@Test
	public void loadAnonymousSingleFeedTest() {
		List<Post> expected = new ArrayList<>();
		expected.add(postB1);
		expected.add(postB2);

		Long views = postB1.getAnonymousViews();

		Mockito.when(userProfileRepository.findById(userB.getUser_id())).thenReturn(Optional.of(userB));
		Mockito.when(userProfileRepository.exists(userB.getEmail())).thenReturn(true);
		Mockito.when(postRepository.findUserPosts(userB)).thenReturn(expected);

		assertThat(postB1.getAnonymousViews(), is(equalTo(views)));
		List<Post> response = postService.loadFeed(userB.getUser_id(), null, PostViewConstants.VIEW_POST_LIST);
		assertThat(postB1.getAnonymousViews(), is(equalTo(views + 1)));

		assertThat(response, is(equalTo(expected)));
		assertThat(response.size(), is(equalTo(expected.size())));
		assertThat(response.get(0), is(equalTo(expected.get(0))));
	}

	@Test
	public void loadMyFeedTest() {
		List<Post> expected = new ArrayList<>();
		expected.add(postA1);

		userA.setUser_id(1L);

		Principal userAuthenticated = new Principal() {

			@Override
			public String getName() {
				return userA.getEmail();
			}
		};

		Mockito.when(userProfileRepository.findOneByEmail(userAuthenticated.getName())).thenReturn(userA);
		Mockito.when(userProfileRepository.exists(userAuthenticated.getName())).thenReturn(true);
		Mockito.when(userProfileRepository.findById(userA.getUser_id())).thenReturn(Optional.of(userA));
		Mockito.when(postRepository.findUserPosts(userA)).thenReturn(expected);

		for (Post p : expected) {
			Mockito.when(postViewRepository.findPostView(p, userA)).thenReturn(null);
		}

		List<Post> response = postService.loadFeed(null, userAuthenticated, PostViewConstants.VIEW_POST_LIST);

		assertThat(response, is(equalTo(expected)));
		assertThat(response.size(), is(equalTo(expected.size())));
		assertThat(response.get(0), is(equalTo(expected.get(0))));
	}

	@Test
	public void findByIdTest() {
		Mockito.when(postRepository.findPostByPostId(postA1.getPost_id())).thenReturn(postA1);
		assertThat(postService.findByID(postA1.getPost_id()), is(equalTo(postA1)));
	}

}
