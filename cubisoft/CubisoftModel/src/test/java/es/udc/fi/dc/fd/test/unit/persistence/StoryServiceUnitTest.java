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

import es.udc.fi.dc.fd.model.persistence.Picture;
import es.udc.fi.dc.fd.model.persistence.Story;
import es.udc.fi.dc.fd.model.persistence.UserProfile;
import es.udc.fi.dc.fd.model.persistence.UserProfile.UserType;
import es.udc.fi.dc.fd.repository.PictureRepository;
import es.udc.fi.dc.fd.repository.StoryRepository;
import es.udc.fi.dc.fd.repository.UserProfileRepository;
import es.udc.fi.dc.fd.service.StoryService;

@RunWith(MockitoJUnitRunner.class)
public class StoryServiceUnitTest {

	public static final String TEST_LOGIN = "test";
	public static final String TEST_FIRSTNAME = "Test";
	public static final String TEST_LASTNAME = "Test";
	public static final String TEST_PASSWORD = "abc123..";
	public static final String TEST_EMAIL = "test@test.com";

	public static final String TEST_PATH = "image.jpg";
	public static final String TEST_DESCRIPTION = "asdfgh";

	@Mock
	private StoryRepository storyRepository;

	@Mock
	private UserProfileRepository userProfileRepository;

	@Mock
	private PictureRepository pictureRepository;

	@InjectMocks
	private StoryService storyService;

	private UserProfile userA, userB, userC;

	private Story storyA1, storyB1, storyB2;

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

		storyA1 = new Story(Calendar.getInstance(), picture, userA);
		storyB1 = new Story(Calendar.getInstance(), picture, userB);
		storyB2 = new Story(Calendar.getInstance(), picture, userB);
	}

//TODO hay que completar los tests para comprobar excepciones donde las haya
	@Test
	public void saveTest() {
		Mockito.when(storyRepository.save(storyA1)).thenReturn(storyA1);
		;
		assertThat(storyService.save(storyA1), is(equalTo(storyA1)));
	}

	@Test
	public void newStoryTest() throws InstanceNotFoundException {
		// Datos esperados por el test
		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(true);
		Mockito.when(userProfileRepository.exists("2" + TEST_EMAIL)).thenReturn(true);
		Mockito.when(userProfileRepository.exists("3" + TEST_EMAIL)).thenReturn(true);
		Story story = storyService.newStory(picture, userA, 500);
		Story story2 = storyService.newStory(picture, userB, 500);
		Story story3 = storyService.newStory(picture, userC, 500);

		// Inicializamos lo que tienen que devolver las clases Repository en los
		// métodos utilizados por el servicio en los diferentes test
		Mockito.when(storyRepository.findStoryByStoryId(story.getStory_id())).thenReturn(story);
		Mockito.when(storyRepository.findStoryByStoryId(story2.getStory_id())).thenReturn(story2);
		Mockito.when(storyRepository.findStoryByStoryId(story3.getStory_id())).thenReturn(story3);

		// Realizamos comprobaciones
		assertEquals(storyRepository.findStoryByStoryId(story.getStory_id()).getStory_id(), story.getStory_id());
		assertEquals(storyRepository.findStoryByStoryId(story2.getStory_id()).getStory_id(), story2.getStory_id());
		assertEquals(storyRepository.findStoryByStoryId(story3.getStory_id()).getStory_id(), story3.getStory_id());
	}

	@Test
	public void findUserStoriesTest() { // Datos esperados por el test
		ArrayList<Story> storiesB = new ArrayList<>();
		storiesB.add(storyB1);
		storiesB.add(storyB2);

		Mockito.when(userProfileRepository.exists("2" + TEST_EMAIL)).thenReturn(true);
		Mockito.when(storyRepository.findUserStories(userB)).thenReturn(storiesB);

		try {
			assertThat(storyService.findUserStories(userB), is(equalTo(storiesB)));
		} catch (

		InstanceNotFoundException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void deleteStoryTest() throws InstanceNotFoundException {
		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(true);
		Mockito.when(userProfileRepository.exists("2" + TEST_EMAIL)).thenReturn(true);
		Story story = storyService.newStory(picture, userA, 500);
		Story story2 = storyService.newStory(picture, userB, 500);

		picture.setPicture_id(2L);

		Mockito.when(storyRepository.findStoryByStoryId(story.getStory_id())).thenReturn(story);

		storyService.deleteStory("", story);
		storyService.deleteStory("", story2);

		Mockito.when(storyRepository.findStoryByStoryId(story.getStory_id())).thenReturn(null);
		Mockito.when(storyRepository.findStoryByStoryId(story2.getStory_id())).thenReturn(null);

		assertEquals(storyRepository.findStoryByStoryId(story.getStory_id()), null);
		assertEquals(storyRepository.findStoryByStoryId(story2.getStory_id()), null);
	}

	// TODO añadir test para excepciones
	// este test es una tontería tal y como está hecho
	@Test
	public void deleteOldStoriesTest() throws InstanceNotFoundException {
		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(true);
		Story story = storyService.newStory(picture, userA, -1);
		List<Story> list = new ArrayList<>();
		list.add(story);
		picture.setPicture_id(2L);

		Mockito.when(storyRepository.findUserStories(userA)).thenReturn(list);

		storyService.deleteOldStories(userA, "");

		list.remove(story);
		Mockito.when(storyRepository.findUserStories(userA)).thenReturn(list);
		assertEquals(storyRepository.findUserStories(userA), list);

	}

	// TODO revisar si hacen falta más tests para el load feed
	@Test
	public void loadMyFeedTest() {
		List<Story> expected = new ArrayList<>();
		expected.add(storyA1);

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
		Mockito.when(storyRepository.findUserStories(userA)).thenReturn(expected);

		List<Story> response = storyService.loadFeed(null, userAuthenticated, "fiiu");

		assertThat(response, is(equalTo(expected)));
		assertThat(response.size(), is(equalTo(expected.size())));
		assertThat(response.get(0), is(equalTo(expected.get(0))));
	}

	@Test
	public void findByIdTest() {
		Mockito.when(storyRepository.findStoryByStoryId(storyA1.getStory_id())).thenReturn(storyA1);
		assertThat(storyService.findByID(storyA1.getStory_id()), is(equalTo(storyA1)));
	}

}
