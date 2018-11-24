package es.udc.fi.dc.fd.test.unit.persistence;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Optional;

import javax.management.InstanceNotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import es.udc.fi.dc.fd.model.persistence.Picture;
import es.udc.fi.dc.fd.model.persistence.PictureTag;
import es.udc.fi.dc.fd.model.persistence.Tag;
import es.udc.fi.dc.fd.model.persistence.UserProfile;
import es.udc.fi.dc.fd.model.persistence.UserProfile.UserType;
import es.udc.fi.dc.fd.repository.PictureRepository;
import es.udc.fi.dc.fd.repository.PictureTagRepository;
import es.udc.fi.dc.fd.repository.TagRepository;
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

	public static final String TEST_TAG_NAME = "tag";

	@Mock
	private PictureRepository pictureRepository;

	@Mock
	private UserProfileRepository userProfileRepository;

	@Mock
	private PictureTagRepository pictureTagRepository;

	@Mock
	private TagRepository tagRepository;

	@Mock
	private Principal principal;

	@InjectMocks
	private PictureService pictureService;

	private UserProfile userA;

	private Picture pictureA, pictureB;

	private Tag tag1;

	@Before
	public void initialize() {
		userA = new UserProfile(TEST_LOGIN, TEST_FIRSTNAME, TEST_LASTNAME, TEST_PASSWORD, TEST_EMAIL, null, null,
				UserType.PUBLIC);
		userA.setUser_id(1L);

		ArrayList<PictureTag> pictureTags = new ArrayList<>();

		pictureA = new Picture(TEST_DESCRIPTION, Calendar.getInstance(), TEST_PATH, userA, pictureTags);
		pictureB = new Picture(TEST_DESCRIPTION + "B", Calendar.getInstance(), "2" + TEST_PATH, userA, null);

		tag1 = new Tag(TEST_TAG_NAME + "1", pictureTags);

		pictureTags.add(new PictureTag(pictureA, tag1));
		pictureA.setPicture_tags(pictureTags);

	}

	@Test
	public void getPicturesByAuthorTest() {

		pictureService.save(pictureA);
		pictureService.save(pictureB);

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

		pictureService.delete(pictureA);
		pictureService.delete(pictureB);
	}

	@Test(expected = NullPointerException.class)
	public void getPicturesByNullAuthorTest() throws InstanceNotFoundException {
		assertThat(pictureService.getPicturesByAuthor(null), is(equalTo(null)));
	}

	@Test(expected = InstanceNotFoundException.class)
	public void getPicturesByUnexistentAuthorTest() throws InstanceNotFoundException {
		assertThat(pictureService.getPicturesByAuthor(new UserProfile()), is(equalTo(null)));
	}

	@Test
	public void getPicturesByDescriptionTest() {

		pictureService.save(pictureA);
		pictureService.save(pictureB);
		ArrayList<Picture> pictures = new ArrayList<>();

		pictures.add(pictureA);
		pictures.add(pictureB);

		Mockito.when(pictureRepository.findPicturesByDescription(pictureA.getDescription())).thenReturn(pictures);
		assertThat(pictureService.getPicturesByDescription(pictureA.getDescription()), is(equalTo(pictures)));

		pictureService.delete(pictureA);
		pictureService.delete(pictureB);

	}

	@Test
	public void modifyPictureDescriptionTest() throws InstanceNotFoundException {

		Mockito.when(pictureRepository.existsById(pictureA.getPicture_id())).thenReturn(true);
		Mockito.when(pictureRepository.findById(pictureA.getPicture_id())).thenReturn(Optional.of(pictureA));
		Mockito.when(pictureRepository.save(pictureA)).thenReturn(pictureA);

		assertThat(pictureService.modifyPictureDescription(pictureA.getPicture_id(), TEST_DESCRIPTION),
				is(equalTo(pictureA)));

	}

	@Test(expected = InstanceNotFoundException.class)
	public void modifyUnexistentPictureDescriptionTest() throws InstanceNotFoundException {
		Picture p = new Picture();
		Mockito.when(pictureRepository.existsById(p.getPicture_id())).thenReturn(false);
		assertThat(pictureService.modifyPictureDescription(p.getPicture_id(), TEST_DESCRIPTION), is(equalTo(null)));

	}

	@Test
	public void getSetPictureTest() {
		Calendar date = Calendar.getInstance();
		Picture pictureD = new Picture();
		pictureD.setPicture_id(4L);
		pictureD.setDate(date);
		pictureD.setAuthor(userA);
		pictureD.setImage_path(TEST_PATH);
		pictureD.setDescription(TEST_DESCRIPTION);

		Picture pictureE = new Picture(TEST_DESCRIPTION, date, TEST_PATH, userA, null);
		pictureE.setPicture_id(pictureD.getPicture_id());

		assertEquals(pictureD.getPicture_id(), pictureE.getPicture_id());
		assertEquals(pictureD.getDate(), pictureE.getDate());
		assertEquals(pictureD.getAuthor(), pictureE.getAuthor());
		assertEquals(pictureD.getImage_path(), pictureE.getImage_path());
		assertEquals(pictureD.getDescription(), pictureE.getDescription());
	}

	@Test
	public void setPictureTagsTest() throws InstanceNotFoundException {

		ArrayList<String> tags_text = new ArrayList<>();
		tags_text.add(TEST_TAG_NAME + "1");
		tags_text.add(TEST_TAG_NAME + "2");

		Tag tag2 = new Tag();
		tag2.setText(TEST_TAG_NAME + "2");
		tag2.setPictureTags(new ArrayList<>());

		assertEquals(pictureA.getPicture_tags().size(), 1);
		assertEquals(pictureA.getPicture_tags().get(0).getTag().getText(), tag1.getText());

		Mockito.when(pictureRepository.findById(pictureA.getPicture_id())).thenReturn(Optional.of(pictureA));

		Mockito.when(pictureRepository.existsById(pictureA.getPicture_id())).thenReturn(true);
		Mockito.when(tagRepository.existsByText(TEST_TAG_NAME + "1")).thenReturn(true);
		Mockito.when(tagRepository.existsByText(TEST_TAG_NAME + "2")).thenReturn(false);

		Mockito.when(tagRepository.findTagByText(TEST_TAG_NAME + "1")).thenReturn(tag1);
		Mockito.when(tagRepository.findTagByText(TEST_TAG_NAME + "2")).thenReturn(tag2);

		pictureService.setPictureTags(pictureA.getPicture_id(), tags_text);

		assertThat(pictureA.getPicture_tags().size(), is(equalTo(2)));
		assertThat(pictureA.getPicture_tags().get(0).getTag().getText(), is(equalTo(tag1.getText())));
		assertThat(pictureA.getPicture_tags().get(1).getTag().getText(), is(equalTo(tag2.getText())));

	}

	@Test(expected = InstanceNotFoundException.class)
	public void setUnexistentPictureTagsTest() throws InstanceNotFoundException {
		Mockito.when(pictureRepository.existsById(1234L)).thenReturn(false);
		pictureService.setPictureTags(1234L, null);
	}

}
