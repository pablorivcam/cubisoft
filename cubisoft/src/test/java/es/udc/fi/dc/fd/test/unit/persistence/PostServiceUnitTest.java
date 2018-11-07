package es.udc.fi.dc.fd.test.unit.persistence;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Calendar;

import javax.management.InstanceNotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import es.udc.fi.dc.fd.model.persistence.Picture;
import es.udc.fi.dc.fd.model.persistence.Post;
import es.udc.fi.dc.fd.model.persistence.UserProfile;
import es.udc.fi.dc.fd.model.persistence.UserProfile.UserType;
import es.udc.fi.dc.fd.repository.PostRepository;
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

		picture = new Picture(TEST_DESCRIPTION, Calendar.getInstance(), TEST_PATH, userA);

		postA1 = new Post(Calendar.getInstance(), picture, userA, (long) 0, (long) 0, false);
		postB1 = new Post(Calendar.getInstance(), picture, userB, (long) 0, (long) 0, false);
		postB2 = new Post(Calendar.getInstance(), picture, userB, (long) 0, (long) 0, false);
		postC1 = new Post(Calendar.getInstance(), picture, userC, (long) 0, (long) 0, false);

	}

	@Test
	public void findUserFollowersPostsTest() {

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
		Post post = postService.newPost(picture, userA);

		Mockito.when(postRepository.findPostByPostId(post.getPost_id())).thenReturn(post);

		postService.deletePost(post);

		Mockito.when(postRepository.findPostByPostId(post.getPost_id())).thenReturn(null);

		assertEquals(postRepository.findPostByPostId(post.getPost_id()), null);

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
}
