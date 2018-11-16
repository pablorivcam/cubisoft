package es.udc.fi.dc.fd.test.unit.persistence;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Optional;

import javax.management.InstanceNotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import es.udc.fi.dc.fd.controller.post.FeedViewController;
import es.udc.fi.dc.fd.model.persistence.Likes;
import es.udc.fi.dc.fd.model.persistence.Picture;
import es.udc.fi.dc.fd.model.persistence.Post;
import es.udc.fi.dc.fd.model.persistence.UserProfile;
import es.udc.fi.dc.fd.model.persistence.UserProfile.UserType;
import es.udc.fi.dc.fd.repository.LikesRepository;
import es.udc.fi.dc.fd.repository.PostRepository;
import es.udc.fi.dc.fd.repository.UserProfileRepository;
import es.udc.fi.dc.fd.service.AlreadyLikedException;
import es.udc.fi.dc.fd.service.LikesService;
import es.udc.fi.dc.fd.service.NotLikedYetException;

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
	private Post postA1;
	private Picture picture;
	private Likes like1, like2, like3;

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

		postA1 = new Post(Calendar.getInstance(), picture, userA, (long) 0, (long) 0, (long) 0, false);

		like1 = new Likes(userA, postA1);
		like2 = new Likes(userB, postA1);
		like3 = new Likes(userC, postA1);

	}

	@Test
	public void newLikesTest() throws InstanceNotFoundException, AlreadyLikedException {
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

	@Test(expected = NullPointerException.class)
	public void newNullUserLikesTest() throws InstanceNotFoundException, AlreadyLikedException {
		assertThat(likesService.newLikes(null, postA1), is(equalTo(null)));
	}

	@Test(expected = NullPointerException.class)
	public void newNullPostLikesTest() throws InstanceNotFoundException, AlreadyLikedException {
		assertThat(likesService.newLikes(userA, null), is(equalTo(null)));
	}

	@Test(expected = InstanceNotFoundException.class)
	public void newNonExistentUserLikesTest() throws InstanceNotFoundException, AlreadyLikedException {
		UserProfile user = new UserProfile();
		user.setEmail(userA.getEmail());
		Mockito.when(userProfileRepository.exists(user.getEmail())).thenReturn(false);

		assertThat(likesService.newLikes(user, postA1), is(equalTo(null)));
	}

	@Test(expected = InstanceNotFoundException.class)
	public void newNonExistentPostLikesTest() throws InstanceNotFoundException, AlreadyLikedException {
		Mockito.when(postRepository.existsById(postA1.getPost_id())).thenReturn(false);

		assertThat(likesService.newLikes(userA, postA1), is(equalTo(null)));
	}

	@Test(expected = AlreadyLikedException.class)
	public void newAlreadyLikedTest() throws InstanceNotFoundException, AlreadyLikedException {

		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(true);
		Mockito.when(postRepository.existsById(postA1.getPost_id())).thenReturn(true);
		Mockito.when(likesRepository.save(like1)).thenReturn(like1);
		Mockito.when(likesRepository.findLikesByUserAndPost(userA, postA1)).thenReturn(like1);

		likesService.newLikes(userA, postA1);
	}

	@Test
	public void findUserLikesTest() throws InstanceNotFoundException, AlreadyLikedException {
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

	@Test(expected = NullPointerException.class)
	public void findNullUserLikesTest() throws InstanceNotFoundException, AlreadyLikedException {
		likesService.findUserLikes(null);
	}

	@Test(expected = InstanceNotFoundException.class)
	public void findNonExistentUserLikesTest() throws InstanceNotFoundException, AlreadyLikedException {
		UserProfile user = new UserProfile();
		user.setEmail("");
		Mockito.when(userProfileRepository.exists(user.getEmail())).thenReturn(false);

		likesService.findUserLikes(user);
	}

	@Test
	public void findPostLikesTest() throws InstanceNotFoundException, AlreadyLikedException {
		ArrayList<Likes> likesA = new ArrayList<>();

		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(true);

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
	public void findNullPostLikesTest() throws InstanceNotFoundException, AlreadyLikedException {
		likesService.findPostLikes(null);
	}

	@Test(expected = InstanceNotFoundException.class)
	public void findNonExistentPostLikesTest() throws InstanceNotFoundException, AlreadyLikedException {
		Post post = new Post();
		post.setPost_id((long) 1);
		Mockito.when(postRepository.existsById(post.getPost_id())).thenReturn(false);

		likesService.findPostLikes(post);
	}

	@Test
	public void existLikesTest() throws InstanceNotFoundException, AlreadyLikedException {
		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(true);

		Mockito.when(postRepository.existsById(postA1.getPost_id())).thenReturn(true);

		assertThat(likesService.existLikes(userA, postA1), is(equalTo(false)));

		Mockito.when(likesRepository.save(like1)).thenReturn(like1);

		Mockito.when(likesRepository.findLikesByUserAndPost(userA, postA1)).thenReturn(like1);

		assertThat(likesService.existLikes(userA, postA1), is(equalTo(true)));
	}

	@Test
	public void existLikesNonExistentUserTest() throws InstanceNotFoundException, AlreadyLikedException {
		UserProfile user = new UserProfile();
		user.setEmail("");
		Mockito.when(userProfileRepository.exists(user.getEmail())).thenReturn(false);
		Mockito.when(postRepository.existsById(postA1.getPost_id())).thenReturn(true);
		assertThat(likesService.existLikes(user, postA1), is(equalTo(false)));

	}

	@Test
	public void existLikesNonExistentPostTest() throws InstanceNotFoundException, AlreadyLikedException {
		Post post = new Post();
		post.setPost_id((long) 1);
		Mockito.when(postRepository.existsById(post.getPost_id())).thenReturn(false);
		Mockito.when(userProfileRepository.exists(userA.getEmail())).thenReturn(true);
		assertThat(likesService.existLikes(userA, post), is(equalTo(false)));
	}

	@Test(expected = NullPointerException.class)
	public void existLikesNullUserTest() throws InstanceNotFoundException, AlreadyLikedException {
		Mockito.when(postRepository.existsById(postA1.getPost_id())).thenReturn(true);
		likesService.existLikes(null, postA1);
	}

	@Test(expected = NullPointerException.class)
	public void existLikesNullPostTest() throws InstanceNotFoundException, AlreadyLikedException {
		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(true);
		likesService.existLikes(userA, null);
	}

	@Test
	public void deleteUserPostLikesTest()
			throws InstanceNotFoundException, NotLikedYetException, AlreadyLikedException {

		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(true);
		Mockito.when(postRepository.existsById(postA1.getPost_id())).thenReturn(true);
		Mockito.when(likesRepository.save(like1)).thenReturn(like1);

		Likes likeT1 = likesService.newLikes(userA, postA1);

		Mockito.when(likesRepository.findLikesById(likeT1.getLikes_id())).thenReturn(likeT1);
		Mockito.when(likesRepository.findLikesByUserAndPost(userA, postA1)).thenReturn(likeT1);

		likesService.deleteUserPostLikes(userA, postA1);

		Mockito.when(likesRepository.findLikesById(likeT1.getLikes_id())).thenReturn(null);

		assertEquals(likesRepository.findLikesById(likeT1.getLikes_id()), null);

	}

	@Test(expected = NullPointerException.class)
	public void deleteNullUserPostLikesTest()
			throws InstanceNotFoundException, NotLikedYetException, AlreadyLikedException {
		likesService.deleteUserPostLikes(null, postA1);
	}

	@Test(expected = NullPointerException.class)
	public void deleteUserNullPostLikesTest()
			throws InstanceNotFoundException, NotLikedYetException, AlreadyLikedException {
		likesService.deleteUserPostLikes(userA, null);
	}

	@Test(expected = InstanceNotFoundException.class)
	public void deleteNonExistentUserPostLikesTest()
			throws InstanceNotFoundException, NotLikedYetException, AlreadyLikedException {
		UserProfile user = new UserProfile();
		user.setEmail("");
		Mockito.when(userProfileRepository.exists(user.getEmail())).thenReturn(false);
		Mockito.when(postRepository.existsById(postA1.getPost_id())).thenReturn(true);

		likesService.deleteUserPostLikes(user, postA1);
	}

	@Test(expected = InstanceNotFoundException.class)
	public void deleteUserNonExistentPostLikesTest()
			throws InstanceNotFoundException, NotLikedYetException, AlreadyLikedException {
		Post post = new Post();
		post.setPost_id((long) 1);
		Mockito.when(postRepository.existsById(post.getPost_id())).thenReturn(false);
		Mockito.when(userProfileRepository.exists(userA.getEmail())).thenReturn(true);

		likesService.deleteUserPostLikes(userA, post);
	}

	@Test(expected = NotLikedYetException.class)
	public void deleteUserPostLikesNonExistentLikeTest()
			throws InstanceNotFoundException, NotLikedYetException, AlreadyLikedException {

		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(true);
		Mockito.when(postRepository.existsById(postA1.getPost_id())).thenReturn(true);
		Mockito.when(likesRepository.findLikesByUserAndPost(userA, postA1)).thenReturn(null);

		likesService.deleteUserPostLikes(userA, postA1);
	}

	@Test
	public void deleteLikesTest() throws InstanceNotFoundException, AlreadyLikedException {

		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(true);

		Mockito.when(postRepository.existsById(postA1.getPost_id())).thenReturn(true);

		Mockito.when(likesRepository.save(like1)).thenReturn(like1);

		Likes likeT1 = likesService.newLikes(userA, postA1);

		Mockito.when(likesRepository.findLikesById(likeT1.getLikes_id())).thenReturn(likeT1);

		likesService.delete(likeT1);

		Mockito.when(likesRepository.findLikesById(likeT1.getLikes_id())).thenReturn(null);

		assertEquals(likesRepository.findLikesById(likeT1.getLikes_id()), null);

	}

	@Test
	public void likePostTest() throws InstanceNotFoundException {

		Post p2 = new Post();
		p2.setPost_id(235L);

		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(true);
		Mockito.when(postRepository.existsById(postA1.getPost_id())).thenReturn(true);

		Mockito.when(postRepository.findById(postA1.getPost_id())).thenReturn(Optional.of(postA1));
		Mockito.when(userProfileRepository.findOneByEmail(TEST_EMAIL)).thenReturn(userA);

		Mockito.when(postRepository.existsById(p2.getPost_id())).thenReturn(true);
		Mockito.when(postRepository.findById(p2.getPost_id())).thenReturn(Optional.of(p2));
		Mockito.when(likesRepository.findLikesByUserAndPost(userA, p2)).thenReturn(like1);

		assertThat(likesService.likePost(postA1.getPost_id(), TEST_EMAIL),
				is(equalTo(FeedViewController.SUCESS_LIKED_POST)));

		assertThat(likesService.likePost(p2.getPost_id(), TEST_EMAIL),
				is(equalTo(FeedViewController.ALREADY_LIKED_POST_ERROR)));
	}

	@Test(expected = InstanceNotFoundException.class)
	public void likeUnexistentPostTest() throws InstanceNotFoundException {
		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(true);
		Mockito.when(postRepository.existsById(postA1.getPost_id())).thenReturn(false);

		assertThat(likesService.likePost(postA1.getPost_id(), TEST_EMAIL),
				is(equalTo(FeedViewController.SUCESS_LIKED_POST)));

	}

	@Test(expected = InstanceNotFoundException.class)
	public void likePostWithUnexistentUserTest() throws InstanceNotFoundException {
		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(false);

		assertThat(likesService.likePost(postA1.getPost_id(), TEST_EMAIL),
				is(equalTo(FeedViewController.SUCESS_LIKED_POST)));

	}

	@Test
	public void unlikePostTest() throws InstanceNotFoundException, NotLikedYetException {

		Post p2 = new Post();
		p2.setPost_id(235L);

		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(true);
		Mockito.when(postRepository.existsById(postA1.getPost_id())).thenReturn(true);

		Mockito.when(postRepository.findById(postA1.getPost_id())).thenReturn(Optional.of(postA1));
		Mockito.when(userProfileRepository.findOneByEmail(TEST_EMAIL)).thenReturn(userA);

		Mockito.when(postRepository.existsById(p2.getPost_id())).thenReturn(true);

		Mockito.when(postRepository.findById(p2.getPost_id())).thenReturn(Optional.of(p2));
		Mockito.when(likesRepository.findLikesByUserAndPost(userA, postA1)).thenReturn(like1);

		assertThat(likesService.unlikePost(postA1.getPost_id(), TEST_EMAIL),
				is(equalTo(FeedViewController.SUCESS_UNLIKED_POST)));

		assertThat(likesService.unlikePost(p2.getPost_id(), TEST_EMAIL),
				is(equalTo(FeedViewController.POST_NOT_LIKED_YET_ERROR)));
	}

	@Test(expected = InstanceNotFoundException.class)
	public void unlikeUnexistentPostTest() throws InstanceNotFoundException, NotLikedYetException {
		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(true);
		Mockito.when(postRepository.existsById(postA1.getPost_id())).thenReturn(false);

		assertThat(likesService.unlikePost(postA1.getPost_id(), TEST_EMAIL),
				is(equalTo(FeedViewController.SUCESS_LIKED_POST)));

	}

	@Test(expected = InstanceNotFoundException.class)
	public void unlikePostWithUnexistentUserTest() throws InstanceNotFoundException, NotLikedYetException {
		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(false);

		assertThat(likesService.unlikePost(postA1.getPost_id(), TEST_EMAIL),
				is(equalTo(FeedViewController.SUCESS_LIKED_POST)));

	}

	@Test
	public void getSetLikesTest() {
		Likes likeD = new Likes();
		likeD.setLikes_id(4L);
		likeD.setPost(postA1);
		likeD.setUser(userA);

		Likes likeE = new Likes(userA, postA1);
		likeE.setLikes_id(likeD.getLikes_id());

		assertEquals(likeD.getLikes_id(), likeE.getLikes_id());
		assertEquals(likeD.getPost(), likeE.getPost());
		assertEquals(likeD.getUser(), likeE.getUser());
	}

}
