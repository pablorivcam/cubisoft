package es.udc.fi.dc.fd.test.unit.persistence;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import javax.management.InstanceNotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import es.udc.fi.dc.fd.model.persistence.Blocks;
import es.udc.fi.dc.fd.model.persistence.Blocks.BlockType;
import es.udc.fi.dc.fd.model.persistence.UserProfile;
import es.udc.fi.dc.fd.model.persistence.UserProfile.UserType;
import es.udc.fi.dc.fd.repository.BlocksRepository;
import es.udc.fi.dc.fd.repository.UserProfileRepository;
import es.udc.fi.dc.fd.service.AlreadyBlockedException;
import es.udc.fi.dc.fd.service.BlocksService;
import es.udc.fi.dc.fd.service.NotBlockedYetException;

public class BlocksServiceUnitTest {
	public static final String TEST_LOGIN = "test";
	public static final String TEST_FIRSTNAME = "Test";
	public static final String TEST_LASTNAME = "Test";
	public static final String TEST_PASSWORD = "abc123..";
	public static final String TEST_EMAIL = "test@test.com";

	@Mock
	private UserProfileRepository userProfileRepository;
	@Mock
	private BlocksRepository blocksRepository;
	@InjectMocks
	private BlocksService blocksService;

	private UserProfile userA, userB, userC;

	private Blocks block_stories1, block_posts1;

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

		block_stories1 = new Blocks(userA, userB, BlockType.STORIES);
		block_posts1 = new Blocks(userB, userC, BlockType.PROFILE);

	}

	@Test
	public void newBlocksTest() throws InstanceNotFoundException, AlreadyBlockedException {
		// Datos esperados por el test
		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(true);
		Mockito.when(userProfileRepository.exists("2" + TEST_EMAIL)).thenReturn(true);
		Mockito.when(userProfileRepository.exists("3" + TEST_EMAIL)).thenReturn(true);

		// Inicializamos lo que tienen que devolver las clases Repository en los
		// métodos utilizados por el servicio en los diferentes test
		ArrayList<Blocks> blocksList = new ArrayList<>();

		Mockito.when(blocksRepository.findBlocksByUser(userA)).thenReturn(blocksList);

		Blocks blockT1 = blocksService.newBlocks(userA, userB, BlockType.PROFILE);
		Blocks blockT2 = blocksService.newBlocks(userA, userC, BlockType.STORIES);
		Blocks blockT3 = blocksService.newBlocks(userA, userC, BlockType.PROFILE);

		blocksList.add(blockT1);
		blocksList.add(blockT2);
		blocksList.add(blockT3);

		// Realizamos comprobaciones
		try {
			assertThat(blocksService.findUserBlocks(userA), is(equalTo(blocksList)));

		} catch (InstanceNotFoundException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	@Test(expected = NullPointerException.class)
	public void newNullUserBlocksTest() throws InstanceNotFoundException, AlreadyBlockedException {
		assertThat(blocksService.newBlocks(null, userA, BlockType.PROFILE), is(equalTo(null)));
	}

	@Test(expected = NullPointerException.class)
	public void newNullBlockedUserTest() throws InstanceNotFoundException, AlreadyBlockedException {
		assertThat(blocksService.newBlocks(userA, null, BlockType.PROFILE), is(equalTo(null)));
	}

	@Test(expected = InstanceNotFoundException.class)
	public void newNonExistentUserBlocksTest() throws InstanceNotFoundException, AlreadyBlockedException {
		UserProfile user = new UserProfile();
		user.setEmail(userA.getEmail());
		Mockito.when(userProfileRepository.exists(user.getEmail())).thenReturn(false);

		assertThat(blocksService.newBlocks(userA, userB, BlockType.PROFILE), is(equalTo(null)));
	}

	@Test(expected = InstanceNotFoundException.class)
	public void newNonExistentBlockedUserBlocksTest() throws InstanceNotFoundException, AlreadyBlockedException {
		Mockito.when(userProfileRepository.exists(userA.getEmail())).thenReturn(true);
		UserProfile user = new UserProfile();
		user.setEmail(userB.getEmail());
		Mockito.when(userProfileRepository.exists(user.getEmail())).thenReturn(false);

		assertThat(blocksService.newBlocks(userA, userB, BlockType.PROFILE), is(equalTo(null)));
	}

	@Test(expected = AlreadyBlockedException.class)
	public void newAlreadyBlockedUserBlocksTest() throws InstanceNotFoundException, AlreadyBlockedException {
		Mockito.when(userProfileRepository.exists(userA.getEmail())).thenReturn(true);
		Mockito.when(userProfileRepository.exists(userB.getEmail())).thenReturn(true);
		Mockito.when(blocksRepository.checkBlock(userA, userB, BlockType.PROFILE)).thenReturn(true);

		blocksService.newBlocks(userA, userB, BlockType.PROFILE);
	}

	@Test
	public void existBlocksTest() {
		Mockito.when(blocksRepository.checkBlock(userA, userB, BlockType.PROFILE)).thenReturn(true);

		assertThat(blocksService.existBlocks(userA, userB, BlockType.PROFILE), is(equalTo(true)));
	}

	@Test
	public void findUserBlocksTest() throws InstanceNotFoundException, AlreadyBlockedException {
		ArrayList<Blocks> blocksA = new ArrayList<>();
		Mockito.when(userProfileRepository.exists(userA.getEmail())).thenReturn(true);
		Mockito.when(userProfileRepository.exists(userB.getEmail())).thenReturn(true);
		Mockito.when(blocksRepository.save(block_stories1)).thenReturn(block_stories1);

		Blocks blockT1 = blocksService.newBlocks(userA, userB, BlockType.STORIES);
		blocksA.add(blockT1);

		Mockito.when(blocksRepository.findBlocksByUser(userA)).thenReturn(blocksA);

		assertThat(blocksService.findUserBlocks(userA), is(equalTo(blocksA)));

	}

	@Test(expected = NullPointerException.class)
	public void findNullUserBlockTest() throws InstanceNotFoundException {
		blocksService.findUserBlocks(null);
	}

	@Test(expected = InstanceNotFoundException.class)
	public void findNonExistentUserBlockTest() throws InstanceNotFoundException {
		UserProfile user = new UserProfile();
		user.setEmail(userA.getEmail());
		Mockito.when(userProfileRepository.exists(user.getEmail())).thenReturn(false);

		blocksService.findUserBlocks(user);
	}

	@Test
	public void deleteUserBlockTest()
			throws InstanceNotFoundException, AlreadyBlockedException, NotBlockedYetException {
		Mockito.when(userProfileRepository.exists(userB.getEmail())).thenReturn(true);
		Mockito.when(userProfileRepository.exists(userC.getEmail())).thenReturn(true);
		Mockito.when(blocksRepository.save(block_posts1)).thenReturn(block_posts1);
		Mockito.when(blocksRepository.checkBlock(userB, userC, BlockType.PROFILE)).thenReturn(false);

		Blocks blockT1 = blocksService.newBlocks(userB, userC, BlockType.PROFILE);

		Mockito.when(blocksRepository.checkBlock(userB, userC, BlockType.PROFILE)).thenReturn(true);

		blocksService.deleteUserBlock(userB, userC, BlockType.PROFILE);

		Mockito.when(blocksRepository.checkBlock(userB, userC, BlockType.PROFILE)).thenReturn(false);

		assertThat(blocksRepository.checkBlock(userB, userC, BlockType.PROFILE), is(equalTo(false)));
	}

	@Test(expected = NullPointerException.class)
	public void deleteNullUserBlockTest() throws InstanceNotFoundException, NotBlockedYetException {
		blocksService.deleteUserBlock(null, userA, BlockType.PROFILE);
	}

	@Test(expected = NullPointerException.class)
	public void deleteNullBlockedUserBlockTest() throws InstanceNotFoundException, NotBlockedYetException {
		blocksService.deleteUserBlock(userA, null, BlockType.PROFILE);
	}

	@Test(expected = InstanceNotFoundException.class)
	public void deleteNonExistentUserPostLikesTest() throws InstanceNotFoundException, NotBlockedYetException {
		UserProfile user = new UserProfile();
		user.setEmail("");
		Mockito.when(userProfileRepository.exists(user.getEmail())).thenReturn(false);

		blocksService.deleteUserBlock(user, userB, BlockType.PROFILE);
	}

	@Test(expected = InstanceNotFoundException.class)
	public void deleteNonExistentBlockedUserPostLikesTest() throws InstanceNotFoundException, NotBlockedYetException {
		Mockito.when(userProfileRepository.exists(userB.getEmail())).thenReturn(true);
		UserProfile user = new UserProfile();
		user.setEmail("");
		Mockito.when(userProfileRepository.exists(user.getEmail())).thenReturn(false);

		blocksService.deleteUserBlock(userB, user, BlockType.PROFILE);
	}

	@Test(expected = NotBlockedYetException.class)
	public void deleteUserNonExistentBlockTest() throws InstanceNotFoundException, NotBlockedYetException {
		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(true);
		Mockito.when(userProfileRepository.exists("2" + TEST_EMAIL)).thenReturn(true);

		Mockito.when(blocksRepository.checkBlock(userB, userC, BlockType.PROFILE)).thenReturn(false);

		blocksService.deleteUserBlock(userA, userB, BlockType.PROFILE);
	}

	@Test
	public void blockStoriesTest() throws InstanceNotFoundException, AlreadyBlockedException {
		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(true);
		Mockito.when(userProfileRepository.exists("2" + TEST_EMAIL)).thenReturn(true);

		Mockito.when(userProfileRepository.findOneByEmail(TEST_EMAIL)).thenReturn(userA);
		Mockito.when(userProfileRepository.findOneByEmail("2" + TEST_EMAIL)).thenReturn(userB);

		Mockito.when(blocksRepository.checkBlock(userA, userB, BlockType.STORIES)).thenReturn(false);

		assertThat(blocksService.blockStories(TEST_EMAIL, "2" + TEST_EMAIL),
				is(equalTo(BlocksService.SUCESS_BLOCKED_USER_STORIES)));
	}

	@Test(expected = InstanceNotFoundException.class)
	public void blockStoriesNonExistentBlockerUserTest() throws InstanceNotFoundException, AlreadyBlockedException {
		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(false);

		assertThat(blocksService.blockStories(TEST_EMAIL, "2" + TEST_EMAIL),
				is(equalTo(BlocksService.SUCESS_BLOCKED_USER_STORIES)));
	}

	@Test(expected = InstanceNotFoundException.class)
	public void blockStoriesNonExistentBlockedUserTest() throws InstanceNotFoundException, AlreadyBlockedException {
		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(true);
		Mockito.when(userProfileRepository.exists("2" + TEST_EMAIL)).thenReturn(false);

		assertThat(blocksService.blockStories(TEST_EMAIL, "2" + TEST_EMAIL),
				is(equalTo(BlocksService.SUCESS_BLOCKED_USER_STORIES)));
	}

	@Test
	public void blockStoriesAlreadyBlockedTest() throws InstanceNotFoundException, AlreadyBlockedException {
		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(true);
		Mockito.when(userProfileRepository.exists("2" + TEST_EMAIL)).thenReturn(true);

		Mockito.when(userProfileRepository.findOneByEmail(TEST_EMAIL)).thenReturn(userA);
		Mockito.when(userProfileRepository.findOneByEmail("2" + TEST_EMAIL)).thenReturn(userB);

		Mockito.when(blocksRepository.checkBlock(userA, userB, BlockType.STORIES)).thenReturn(true);

		assertThat(blocksService.blockStories(TEST_EMAIL, "2" + TEST_EMAIL),
				is(equalTo(BlocksService.ALREADY_BLOCKED_USER_STORIES_ERROR)));
	}

	@Test
	public void blockPostsTest() throws InstanceNotFoundException, AlreadyBlockedException {
		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(true);
		Mockito.when(userProfileRepository.exists("2" + TEST_EMAIL)).thenReturn(true);

		Mockito.when(userProfileRepository.findOneByEmail(TEST_EMAIL)).thenReturn(userA);
		Mockito.when(userProfileRepository.findOneByEmail("2" + TEST_EMAIL)).thenReturn(userB);

		Mockito.when(blocksRepository.checkBlock(userA, userB, BlockType.PROFILE)).thenReturn(false);

		assertThat(blocksService.blockPosts(TEST_EMAIL, "2" + TEST_EMAIL),
				is(equalTo(BlocksService.SUCESS_BLOCKED_USER)));
	}

	@Test(expected = InstanceNotFoundException.class)
	public void blockPostsNonExistentBlockerUserTest() throws InstanceNotFoundException, AlreadyBlockedException {
		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(false);

		assertThat(blocksService.blockPosts(TEST_EMAIL, "2" + TEST_EMAIL),
				is(equalTo(BlocksService.SUCESS_BLOCKED_USER)));
	}

	@Test(expected = InstanceNotFoundException.class)
	public void blockPostsNonExistentBlockedUserTest() throws InstanceNotFoundException, AlreadyBlockedException {
		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(true);
		Mockito.when(userProfileRepository.exists("2" + TEST_EMAIL)).thenReturn(false);

		assertThat(blocksService.blockPosts(TEST_EMAIL, "2" + TEST_EMAIL),
				is(equalTo(BlocksService.SUCESS_BLOCKED_USER)));
	}

	@Test
	public void blockPostsAlreadyBlockedTest() throws InstanceNotFoundException, AlreadyBlockedException {
		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(true);
		Mockito.when(userProfileRepository.exists("2" + TEST_EMAIL)).thenReturn(true);

		Mockito.when(userProfileRepository.findOneByEmail(TEST_EMAIL)).thenReturn(userA);
		Mockito.when(userProfileRepository.findOneByEmail("2" + TEST_EMAIL)).thenReturn(userB);

		Mockito.when(blocksRepository.checkBlock(userA, userB, BlockType.PROFILE)).thenReturn(true);

		assertThat(blocksService.blockPosts(TEST_EMAIL, "2" + TEST_EMAIL),
				is(equalTo(BlocksService.ALREADY_BLOCKED_USER_ERROR)));
	}

	@Test
	public void unblockStoriesTest() throws InstanceNotFoundException, NotBlockedYetException {
		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(true);
		Mockito.when(userProfileRepository.exists("2" + TEST_EMAIL)).thenReturn(true);

		Mockito.when(userProfileRepository.findOneByEmail(TEST_EMAIL)).thenReturn(userA);
		Mockito.when(userProfileRepository.findOneByEmail("2" + TEST_EMAIL)).thenReturn(userB);

		Mockito.when(blocksRepository.checkBlock(userA, userB, BlockType.STORIES)).thenReturn(true);

		assertThat(blocksService.unblockStories(TEST_EMAIL, "2" + TEST_EMAIL),
				is(equalTo(BlocksService.SUCESS_UNBLOCKED_USER_STORIES)));
	}

	@Test(expected = InstanceNotFoundException.class)
	public void unblockStoriesNonExistentBlockerUserTest() throws InstanceNotFoundException, NotBlockedYetException {
		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(false);

		assertThat(blocksService.unblockStories(TEST_EMAIL, "2" + TEST_EMAIL),
				is(equalTo(BlocksService.SUCESS_UNBLOCKED_USER_STORIES)));
	}

	@Test(expected = InstanceNotFoundException.class)
	public void unblockStoriesNonExistentBlockedUserTest() throws InstanceNotFoundException, NotBlockedYetException {
		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(true);
		Mockito.when(userProfileRepository.exists("2" + TEST_EMAIL)).thenReturn(false);

		assertThat(blocksService.unblockStories(TEST_EMAIL, "2" + TEST_EMAIL),
				is(equalTo(BlocksService.SUCESS_UNBLOCKED_USER_STORIES)));
	}

	@Test
	public void unblockStoriesAlreadyBlockedTest() throws InstanceNotFoundException, NotBlockedYetException {
		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(true);
		Mockito.when(userProfileRepository.exists("2" + TEST_EMAIL)).thenReturn(true);

		Mockito.when(userProfileRepository.findOneByEmail(TEST_EMAIL)).thenReturn(userA);
		Mockito.when(userProfileRepository.findOneByEmail("2" + TEST_EMAIL)).thenReturn(userB);

		Mockito.when(blocksRepository.checkBlock(userA, userB, BlockType.STORIES)).thenReturn(false);

		assertThat(blocksService.unblockStories(TEST_EMAIL, "2" + TEST_EMAIL),
				is(equalTo(BlocksService.ALREADY_UNBLOCKED_USER_STORIES_ERROR)));
	}

	@Test
	public void unblockPostsTest() throws InstanceNotFoundException, NotBlockedYetException {
		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(true);
		Mockito.when(userProfileRepository.exists("2" + TEST_EMAIL)).thenReturn(true);

		Mockito.when(userProfileRepository.findOneByEmail(TEST_EMAIL)).thenReturn(userA);
		Mockito.when(userProfileRepository.findOneByEmail("2" + TEST_EMAIL)).thenReturn(userB);

		Mockito.when(blocksRepository.checkBlock(userA, userB, BlockType.PROFILE)).thenReturn(true);

		assertThat(blocksService.unblockPosts(TEST_EMAIL, "2" + TEST_EMAIL),
				is(equalTo(BlocksService.SUCESS_UNBLOCKED_USER)));
	}

	@Test(expected = InstanceNotFoundException.class)
	public void unblockPostsNonExistentBlockerUserTest() throws InstanceNotFoundException, NotBlockedYetException {
		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(false);

		assertThat(blocksService.unblockPosts(TEST_EMAIL, "2" + TEST_EMAIL),
				is(equalTo(BlocksService.SUCESS_UNBLOCKED_USER)));
	}

	@Test(expected = InstanceNotFoundException.class)
	public void unblockPostsNonExistentBlockedUserTest() throws InstanceNotFoundException, NotBlockedYetException {
		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(true);
		Mockito.when(userProfileRepository.exists("2" + TEST_EMAIL)).thenReturn(false);

		assertThat(blocksService.unblockPosts(TEST_EMAIL, "2" + TEST_EMAIL),
				is(equalTo(BlocksService.SUCESS_UNBLOCKED_USER)));
	}

	@Test
	public void unblockPostsAlreadyBlockedTest() throws InstanceNotFoundException, NotBlockedYetException {
		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(true);
		Mockito.when(userProfileRepository.exists("2" + TEST_EMAIL)).thenReturn(true);

		Mockito.when(userProfileRepository.findOneByEmail(TEST_EMAIL)).thenReturn(userA);
		Mockito.when(userProfileRepository.findOneByEmail("2" + TEST_EMAIL)).thenReturn(userB);

		Mockito.when(blocksRepository.checkBlock(userA, userB, BlockType.PROFILE)).thenReturn(false);

		assertThat(blocksService.unblockPosts(TEST_EMAIL, "2" + TEST_EMAIL),
				is(equalTo(BlocksService.ALREADY_UNBLOCKED_USER_ERROR)));
	}

	@Test
	public void getSetBlocksTest() {
		Blocks blockD = new Blocks();
		blockD.setBlocks_id(4L);
		blockD.setUser(userA);
		blockD.setTarget(userB);
		blockD.setBlockType(BlockType.PROFILE);

		Blocks blockE = new Blocks(userA, userB, BlockType.PROFILE);
		blockE.setBlocks_id(blockD.getBlocks_id());

		assertEquals(blockD.getBlocks_id(), blockE.getBlocks_id());
		assertEquals(blockD.getTarget(), blockE.getTarget());
		assertEquals(blockD.getUser(), blockE.getUser());
		assertEquals(blockD.getBlockType(), blockE.getBlockType());
	}

}
