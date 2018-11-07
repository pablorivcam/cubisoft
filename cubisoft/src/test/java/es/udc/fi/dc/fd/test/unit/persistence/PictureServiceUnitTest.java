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
import org.mockito.junit.MockitoJUnitRunner;

import es.udc.fi.dc.fd.model.persistence.Picture;
import es.udc.fi.dc.fd.model.persistence.UserProfile;
import es.udc.fi.dc.fd.model.persistence.UserProfile.UserType;
import es.udc.fi.dc.fd.repository.PictureRepository;
import es.udc.fi.dc.fd.repository.UserProfileRepository;
import es.udc.fi.dc.fd.service.PictureService;

@RunWith(MockitoJUnitRunner.class)
public class PictureServiceUnitTest {

	public static final String TEST_LOGIN = "test";
	public static final String TEST_FIRSTNAME = "Test";
	public static final String TEST_LASTNAME = "Test";
	public static final String TEST_PASSWORD = "abc123..";
	public static final String TEST_EMAIL = "test@test.com";

	public static final String TEST_PATH = "image.jpg";
	public static final String TEST_DESCRIPTION = "asdfgh";

	@Mock
	private PictureRepository pictureRepository;

	@Mock
	private UserProfileRepository userProfileRepository;

	@InjectMocks
	private PictureService pictureService;

	private UserProfile userA;

	private Picture pictureA, pictureB;

	@Before
	public void initialize() {
		userA = new UserProfile(TEST_LOGIN, TEST_FIRSTNAME, TEST_LASTNAME, TEST_PASSWORD, TEST_EMAIL, null, null,
				UserType.PUBLIC);
		userA.setUser_id(1L);

		pictureA = new Picture(TEST_DESCRIPTION, Calendar.getInstance(), TEST_PATH, userA);
		pictureB = new Picture(TEST_DESCRIPTION + "B", Calendar.getInstance(), "2" + TEST_PATH, userA);

	}

	@Test
	public void getPicturesByAuthorTest() {

		ArrayList<Picture> pictures = new ArrayList<>();
		pictures.add(pictureA);
		pictures.add(pictureB);

		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(true);
		Mockito.when(pictureRepository.findPicturesByAuthor(userA)).thenReturn(pictures);

		try {
			assertThat(pictureService.getPicturesByAuthor(userA), is(equalTo(pictures)));
		} catch (InstanceNotFoundException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	@Test(expected = NullPointerException.class)
	public void getPicturesByNullAuthorTest() throws InstanceNotFoundException {
		assertThat(pictureService.getPicturesByAuthor(null), is(equalTo(null)));
	}

	@Test(expected = InstanceNotFoundException.class)
	public void getPicturesByUnexistentAuthorTest() throws InstanceNotFoundException {
		assertThat(pictureService.getPicturesByAuthor(new UserProfile()), is(equalTo(null)));
	}

}
