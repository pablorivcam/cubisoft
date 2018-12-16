package es.udc.fi.dc.fd.test.unit.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

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
import es.udc.fi.dc.fd.model.persistence.UserProfile;
import es.udc.fi.dc.fd.model.persistence.UserProfile.UserType;
import es.udc.fi.dc.fd.repository.FollowRepository;
import es.udc.fi.dc.fd.repository.UserProfileRepository;
import es.udc.fi.dc.fd.service.FollowService;

@RunWith(MockitoJUnitRunner.class)
public class FollowServiceUnitTest {

	public static final String TEST_LOGIN = "test";
	public static final String TEST_FIRSTNAME = "Test";
	public static final String TEST_LASTNAME = "Test";
	public static final String TEST_PASSWORD = "abc123..";
	public static final String TEST_EMAIL = "test@test.com";

	public static final String TEST_PATH = "image.jpg";
	public static final String TEST_DESCRIPTION = "asdfgh";

	@Mock
	private FollowRepository followRepository;

	@Mock
	private UserProfileRepository userProfileRepository;

	@InjectMocks
	private FollowService followService;

	private UserProfile userA, userB, userC;

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
	}

	@Test
	public void followTest() {

		Follow follow = new Follow(userA, userB, false);
		Follow follow2 = new Follow(userB, userC, false);
		Follow follow3 = new Follow(userC, userA, false);

		Mockito.when(followRepository.findFollowByUsers(userA, userB)).thenReturn(follow);
		Mockito.when(followRepository.findFollowByUsers(userB, userC)).thenReturn(follow2);
		Mockito.when(followRepository.findFollowByUsers(userC, userA)).thenReturn(follow3);

		assertTrue(followService.isUserAFollowingUserB(userA, userB));
		assertTrue(followService.isUserAFollowingUserB(userB, userC));
		assertTrue(followService.isUserAFollowingUserB(userC, userA));

	}

	@Test
	public void unfollowTest() {

		followService.follow(userA, userB, false);
		Follow follow2 = new Follow(userB, userC, false);
		followService.follow(userC, userA, false);

		Mockito.when(followRepository.findFollowByUsers(userB, userC)).thenReturn(follow2);

		followService.unfollow(userA, userB);
		followService.unfollow(userC, userA);

		assertFalse(followService.isUserAFollowingUserB(userA, userB));
		assertTrue(followService.isUserAFollowingUserB(userB, userC));
		assertFalse(followService.isUserAFollowingUserB(userC, userA));

	}

	@Test
	public void getUserFollowedProfilesTest() {
		Follow follow = followService.follow(userA, userB, false);
		Follow follow2 = followService.follow(userA, userC, false);

		ArrayList<Follow> followList = new ArrayList<>();
		followList.add(follow);
		followList.add(follow2);

		Mockito.when(followRepository.findFollowsByUser(userA)).thenReturn(followList);

		try {
			assertEquals(followService.getUserFollows(userA), followList);
		} catch (InstanceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void isUserAFollowingUserBTest() {
		Follow follow = new Follow(userA, userB, false);

		Mockito.when(followRepository.findFollowByUsers(userA, userB)).thenReturn(follow);

		assertTrue(followService.isUserAFollowingUserB(userA, userB));
		assertFalse(followService.isUserAFollowingUserB(userB, userA));

	}

	@Test(expected = NullPointerException.class)
	public void isUserAFollowingUnexistentUserTest() {
		assertTrue(followService.isUserAFollowingUserB(userA, null));
	}

	@Test(expected = NullPointerException.class)
	public void isUnexistentUserFollowingUserBTest() {
		assertTrue(followService.isUserAFollowingUserB(null, userA));
	}

	@Test
	public void findFollowsPendingTest() {

		Follow follow = followService.follow(userA, userB, true);
		Follow follow2 = followService.follow(userA, userC, true);
		followService.follow(userB, userC, false);

		ArrayList<Follow> followList = new ArrayList<>();
		followList.add(follow);
		followList.add(follow2);

		Mockito.when(followRepository.findFollowsPending(userA)).thenReturn(followList);

		try {
			assertEquals(followService.findFollowsPending(userA), followList);
		} catch (InstanceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void processPendingFollowsTest() {

		Follow follow = new Follow(userA, userB, true);
		Follow follow2 = followService.follow(userA, userC, true);
		followService.follow(userB, userC, false);

		followService.processPendingFollows(follow, true);

		followService.processPendingFollows(follow2, false);

		Mockito.when(followRepository.findFollowByUsers(userA, userB)).thenReturn(follow);

		assertTrue(followService.isUserAFollowingUserB(userA, userB));
		assertFalse(followService.isUserAFollowingUserB(userA, userC));

	}

	@Test
	public void getSetFollowTest() {
		Follow followD = new Follow();
		followD.setFollow_id(4L);
		followD.setUser(userA);
		followD.setFollowed_user(userB);
		followD.setPending(false);

		Follow followE = new Follow(userA, userB, false);
		followE.setFollow_id(followD.getFollow_id());

		assertEquals(followD.getUser(), followE.getUser());
		assertEquals(followD.getFollowed_user(), followE.getFollowed_user());
		assertEquals(followD.getPending(), followE.getPending());
	}

}