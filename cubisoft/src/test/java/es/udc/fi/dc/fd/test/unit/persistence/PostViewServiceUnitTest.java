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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import es.udc.fi.dc.fd.model.persistence.Picture;
import es.udc.fi.dc.fd.model.persistence.Post;
import es.udc.fi.dc.fd.model.persistence.PostView;
import es.udc.fi.dc.fd.model.persistence.UserProfile;
import es.udc.fi.dc.fd.model.persistence.UserProfile.UserType;
import es.udc.fi.dc.fd.repository.PostRepository;
import es.udc.fi.dc.fd.repository.PostViewRepository;
import es.udc.fi.dc.fd.repository.UserProfileRepository;
import es.udc.fi.dc.fd.service.PostViewService;

public class PostViewServiceUnitTest {

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
	private PostViewRepository postViewRepository;
	@InjectMocks
	private PostViewService postViewService;

	private UserProfile userA, userB, userC;
	private Post postA1, postB1, postB2, postC1;
	private Picture picture;
	private PostView pv1, pv2, pv3, pv4, pv5, pv6;

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
		postB1 = new Post(Calendar.getInstance(), picture, userB, (long) 0, (long) 0, (long) 0, false);
		postB2 = new Post(Calendar.getInstance(), picture, userB, (long) 0, (long) 0, (long) 0, false);
		postC1 = new Post(Calendar.getInstance(), picture, userC, (long) 0, (long) 0, (long) 0, false);

		pv1 = new PostView(userA, postA1);
		pv2 = new PostView(userB, postA1);
		pv3 = new PostView(userC, postB1);
		pv4 = new PostView(userC, postB2);
		pv5 = new PostView(userA, postC1);
		pv6 = new PostView(userB, postC1);

	}

	@Test
	public void findPostViewByPostUsertest() throws InstanceNotFoundException {

		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(true);

		Mockito.when(postRepository.existsById(postA1.getPost_id())).thenReturn(true);

		postViewService.save(pv1);

		Mockito.when(postViewRepository.findPostView(postA1, userA)).thenReturn(pv1);

		assertEquals(postViewService.findPostViewByPostUser(postA1, userA), pv1);

	}

	@Test(expected = NullPointerException.class)
	public void findPostViewByNullPostUserTest() throws InstanceNotFoundException {
		assertThat(postViewService.findPostViewByPostUser(null, userA), is(equalTo(null)));
	}

	@Test(expected = NullPointerException.class)
	public void FindPostViewByPostNullUserTest() throws InstanceNotFoundException {
		assertThat(postViewService.findPostViewByPostUser(postA1, null), is(equalTo(null)));
	}

	@Test(expected = InstanceNotFoundException.class)
	public void FindNonExistentPostViewTest() throws InstanceNotFoundException {
		UserProfile user = new UserProfile();
		user.setUser_id((long) 1);
		Mockito.when(postRepository.existsById(postA1.getPost_id())).thenReturn(false);

		assertThat(postViewService.findPostViewByPostUser(postA1, userA), is(equalTo(null)));
	}

	@Test
	public void findPostViewtest() throws InstanceNotFoundException {

		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(true);
		Mockito.when(userProfileRepository.exists("2" + TEST_EMAIL)).thenReturn(true);
		Mockito.when(userProfileRepository.exists("3" + TEST_EMAIL)).thenReturn(true);

		postViewService.save(pv1);
		postViewService.save(pv2);
		postViewService.save(pv3);

		Mockito.when(postRepository.existsById(postA1.getPost_id())).thenReturn(true);

		ArrayList<PostView> postViews = new ArrayList<>();
		postViews.add(pv1);
		postViews.add(pv2);
		postViews.add(pv3);

		Mockito.when(postViewRepository.findViewsByPost(postA1)).thenReturn(postViews);

		assertThat(postViewService.findPostView(postA1), is(equalTo(postViews)));

	}

	@Test(expected = NullPointerException.class)
	public void findNullPostsViewsTest() throws InstanceNotFoundException {
		assertThat(postViewService.findPostsViews(null), is(equalTo(null)));
	}

	@Test(expected = NullPointerException.class)
	public void findNullPostsViewTest() throws InstanceNotFoundException {
		assertThat(postViewService.findPostView(null), is(equalTo(null)));
	}

	@Test(expected = InstanceNotFoundException.class)
	public void findUnexistentPostsViewTest() throws InstanceNotFoundException {

		Post p = new Post();
		Mockito.when(postViewRepository.existsById(p.getPost_id())).thenReturn(false);

		assertThat(postViewService.findPostView(p), is(equalTo(null)));
	}

	@Test
	public void findPostsViewsTest() throws InstanceNotFoundException {

		ArrayList<Post> posts = new ArrayList<>();
		posts.add(postA1);
		posts.add(postB1);
		posts.add(postB2);
		posts.add(postC1);

		ArrayList<PostView> postViews = new ArrayList<>();
		postViews.add(pv1); // postA1
		postViews.add(pv2); // postA1
		postViews.add(pv3); // postB1
		postViews.add(pv4); // postB2
		postViews.add(pv5); // postC1
		postViews.add(pv6); // postC1

		ArrayList<PostView> postViewsPost1 = new ArrayList<>();
		postViewsPost1.add(pv1); // postA1
		postViewsPost1.add(pv2); // postA1

		ArrayList<PostView> postViewsPost2 = new ArrayList<>();
		postViewsPost2.add(pv3); // postB1

		ArrayList<PostView> postViewsPost3 = new ArrayList<>();
		postViewsPost3.add(pv4); // postB2

		ArrayList<PostView> postViewsPost4 = new ArrayList<>();
		postViewsPost4.add(pv5); // postC1
		postViewsPost4.add(pv6); // postC1

		Mockito.when(postViewRepository.findViewsByPost(postA1)).thenReturn(postViewsPost1);
		Mockito.when(postViewRepository.findViewsByPost(postB1)).thenReturn(postViewsPost2);
		Mockito.when(postViewRepository.findViewsByPost(postB2)).thenReturn(postViewsPost3);
		Mockito.when(postViewRepository.findViewsByPost(postC1)).thenReturn(postViewsPost4);

		assertThat(postViewService.findPostsViews(posts), is(equalTo(postViews)));

	}

	@Test
	public void getSetPostViewTest() {
		PostView pviewD = new PostView();
		pviewD.setView_id(4L);
		pviewD.setUser(userA);
		pviewD.setPost(postA1);

		PostView pviewE = new PostView(userA, postA1);
		pviewE.setView_id(pviewD.getView_id());

		assertEquals(pviewD.getView_id(), pviewE.getView_id());
		assertEquals(pviewD.getPost(), pviewE.getPost());
		assertEquals(pviewD.getUser(), pviewE.getUser());
	}
}
