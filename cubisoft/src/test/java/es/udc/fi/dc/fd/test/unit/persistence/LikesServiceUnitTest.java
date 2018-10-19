package es.udc.fi.dc.fd.test.unit.persistence;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
import java.util.Calendar;

import javax.management.InstanceNotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import es.udc.fi.dc.fd.model.persistence.Likes;
import es.udc.fi.dc.fd.model.persistence.Picture;
import es.udc.fi.dc.fd.model.persistence.Post;
import es.udc.fi.dc.fd.model.persistence.UserProfile;
import es.udc.fi.dc.fd.repository.LikesRepository;
import es.udc.fi.dc.fd.repository.PostRepository;
import es.udc.fi.dc.fd.repository.UserProfileRepository;
import es.udc.fi.dc.fd.service.LikesService;

//TODO acabar todo el test y hacer commit
public class LikesServiceUnitTest {
	public static final String TEST_LOGIN = "test";
	public static final String TEST_FIRSTNAME = "Test";
	public static final String TEST_LASTNAME = "Test";
	public static final String TEST_PASSWORD = "abc123..";
	public static final String TEST_EMAIL = "test@test.com";

	public static final String TEST_PATH = "image.jpg";
	public static final String TEST_DESCRIPTION = "asdfgh";

	@Mock
	private UserProfileRepository userProfileRepository;
	@Mock
	private PostRepository postRepository;
	@Mock
	private LikesRepository likesRepository;
	@InjectMocks
	private LikesService likesService;

	private UserProfile userA, userB, userC;
	private Post postA1, postB1, postB2, postC1;
	private Picture picture;
	private Likes like1, like2, like3;

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

		like1 = new Likes(userA, postA1);
		like2 = new Likes(userB, postA1);
		like3 = new Likes(userC, postA1);

	}

	@Test
	public void newLikesTest() throws InstanceNotFoundException {
		// Datos esperados por el test
		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(true);
		Mockito.when(userProfileRepository.exists("2" + TEST_EMAIL)).thenReturn(true);
		Mockito.when(userProfileRepository.exists("3" + TEST_EMAIL)).thenReturn(true);

		Mockito.when(postRepository.existsById(postA1.getPost_id())).thenReturn(true);

		Mockito.when(likesRepository.save(like1)).thenReturn(like1);
		Mockito.when(likesRepository.save(like2)).thenReturn(like2);
		Mockito.when(likesRepository.save(like3)).thenReturn(like3);

		// Inicializamos lo que tienen que devolver las clases Repository en los
		// métodos utilizados por el servicio en los diferentes test

		ArrayList<Likes> likesList = new ArrayList<>();

		Mockito.when(likesRepository.findLikesByPost(postA1)).thenReturn(likesList);

		Likes likeT1 = likesService.newLikes(userA, postA1);
		Likes likeT2 = likesService.newLikes(userB, postA1);
		Likes likeT3 = likesService.newLikes(userC, postA1);

		likesList.add(likeT1);
		likesList.add(likeT2);
		likesList.add(likeT3);

		// Realizamos comprobaciones
		try {
			assertThat(likesService.findPostLikes(postA1), is(equalTo(likesList)));

		} catch (InstanceNotFoundException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void findUserLikesTest() throws InstanceNotFoundException { // Datos esperados por el test
		ArrayList<Likes> likesA = new ArrayList<>();

		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(true);
		Mockito.when(userProfileRepository.exists("2" + TEST_EMAIL)).thenReturn(true);
		Mockito.when(userProfileRepository.exists("3" + TEST_EMAIL)).thenReturn(true);

		Mockito.when(postRepository.existsById(postA1.getPost_id())).thenReturn(true);

		Mockito.when(likesRepository.save(like1)).thenReturn(like1);

		Likes likeT1 = likesService.newLikes(userA, postA1);
		likesA.add(likeT1);

		Mockito.when(likesRepository.findLikesByUser(userA)).thenReturn(likesA);

		try {
			assertThat(likesService.findUserLikes(userA), is(equalTo(likesA)));
		} catch (

		InstanceNotFoundException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void findPostLikesTest() throws InstanceNotFoundException {
		ArrayList<Likes> likesA = new ArrayList<>();

		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(true);
		Mockito.when(userProfileRepository.exists("2" + TEST_EMAIL)).thenReturn(true);
		Mockito.when(userProfileRepository.exists("3" + TEST_EMAIL)).thenReturn(true);

		Mockito.when(postRepository.existsById(postA1.getPost_id())).thenReturn(true);

		Mockito.when(likesRepository.save(like1)).thenReturn(like1);

		Likes likeT1 = likesService.newLikes(userA, postA1);
		likesA.add(likeT1);

		Mockito.when(likesRepository.findLikesByPost(postA1)).thenReturn(likesA);

		try {
			assertThat(likesService.findPostLikes(postA1), is(equalTo(likesA)));
		} catch (

		InstanceNotFoundException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	@Test(expected = NullPointerException.class)
	public void newNullUserLikesTest() throws InstanceNotFoundException {
		assertThat(likesService.newLikes(null, postA1), is(equalTo(null)));
	}

	@Test(expected = NullPointerException.class)
	public void newNullPostLikesTest() throws InstanceNotFoundException {
		assertThat(likesService.newLikes(userA, null), is(equalTo(null)));
	}
	// TODO revisar si hay que hacer más tests aquí

}
