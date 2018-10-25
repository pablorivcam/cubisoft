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
import es.udc.fi.dc.fd.repository.PostRepository;
import es.udc.fi.dc.fd.repository.PostViewRepository;
import es.udc.fi.dc.fd.repository.UserProfileRepository;
import es.udc.fi.dc.fd.service.PostViewService;

public class PostViewServiceTest {

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
	private Post postA1, postA2;
	private Picture picture;
	private PostView pv1, pv2, pv3, pv4;

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

		postA1 = new Post(Calendar.getInstance(), picture, userA, (long) 0);
		postA2 = new Post(Calendar.getInstance(), picture, userA, (long) 0);

		pv1 = new PostView(userA, postA1);
		pv2 = new PostView(userB, postA1);
		pv3 = new PostView(userC, postA1);

		pv4 = new PostView(userC, postA2);

	}

	@Test
	public void findPostViewByPostUsertest() throws InstanceNotFoundException {

		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(true);

		Mockito.when(postRepository.existsById(postA1.getPost_id())).thenReturn(true);

		postViewService.save(pv1);

		Mockito.when(postViewRepository.findPostView(postA1, userA)).thenReturn(pv1);

		assertEquals(postViewService.findPostViewByPostUser(postA1, userA), pv1);

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

}
