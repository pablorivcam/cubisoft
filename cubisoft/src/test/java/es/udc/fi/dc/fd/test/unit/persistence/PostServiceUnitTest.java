package es.udc.fi.dc.fd.test.unit.persistence;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

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

import es.udc.fi.dc.fd.model.persistence.Follow;
import es.udc.fi.dc.fd.model.persistence.Picture;
import es.udc.fi.dc.fd.model.persistence.Post;
import es.udc.fi.dc.fd.model.persistence.UserProfile;
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

	@SuppressWarnings("unused")
	private Follow followAB, followAC, followCA;

	private Post postA1, postB1, postB2, postC1;

	private Picture picture;

	@Before
	public void initialize() {

		MockitoAnnotations.initMocks(this);

		userA = new UserProfile(TEST_LOGIN, TEST_FIRSTNAME, TEST_LASTNAME, TEST_PASSWORD, TEST_EMAIL, null, null);
		userA.setUser_id(1L);
		userB = new UserProfile(TEST_LOGIN + 2, TEST_FIRSTNAME + 2, TEST_LASTNAME + 2, TEST_PASSWORD, "2" + TEST_EMAIL,
				null, null);
		userB.setUser_id(2L);
		userC = new UserProfile(TEST_LOGIN + 3, TEST_FIRSTNAME + 3, TEST_LASTNAME + 3, TEST_PASSWORD, "3" + TEST_EMAIL,
				null, null);
		userC.setUser_id(3L);

		picture = new Picture(TEST_DESCRIPTION, Calendar.getInstance(), TEST_PATH, userA);

		postA1 = new Post(Calendar.getInstance(), picture, userA);
		postB1 = new Post(Calendar.getInstance(), picture, userB);
		postB2 = new Post(Calendar.getInstance(), picture, userB);
		postC1 = new Post(Calendar.getInstance(), picture, userC);

		followAB = new Follow(userA, userB);
		followAC = new Follow(userA, userC);
		followCA = new Follow(userC, userA);

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

		// Inicializamos lo que tienen que devolver las clases Repository en los métodos
		// utilizados por el servicio en los diferentes test
		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(true);
		Mockito.when(postRepository.findUserFollowersPosts(userA)).thenReturn(postsA);

		Mockito.when(userProfileRepository.exists("3" + TEST_EMAIL)).thenReturn(true);
		Mockito.when(postRepository.findUserFollowersPosts(userC)).thenReturn(postsC);

		// Ejecutamos los test
		try {
			assertThat(postService.findUserFollowersPosts(userA), is(equalTo(postsA)));
			assertThat(postService.findUserFollowersPosts(userC), is(equalTo(postsC)));

		} catch (InstanceNotFoundException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

	}

	@Test(expected = NullPointerException.class)
	public void findNullUserFollowersTest() throws InstanceNotFoundException, NullPointerException {
		assertThat(postService.findUserFollowersPosts(null), is(equalTo(null)));
	}

	@Test(expected = InstanceNotFoundException.class)
	public void findUnexistentUserFollowersTest() throws InstanceNotFoundException, NullPointerException {
		assertThat(postService.findUserFollowersPosts(new UserProfile()), is(equalTo(null)));
	}

}
