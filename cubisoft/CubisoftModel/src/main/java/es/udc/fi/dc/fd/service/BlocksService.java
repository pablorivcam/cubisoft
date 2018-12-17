package es.udc.fi.dc.fd.service;

import java.util.List;

import javax.management.InstanceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.udc.fi.dc.fd.model.persistence.Blocks;
import es.udc.fi.dc.fd.model.persistence.Blocks.BlockType;
import es.udc.fi.dc.fd.model.persistence.UserProfile;
import es.udc.fi.dc.fd.repository.BlocksRepository;
import es.udc.fi.dc.fd.repository.UserProfileRepository;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class BlocksService {

	@Autowired
	private BlocksRepository blocksRepository;
	@Autowired
	private UserProfileRepository userProfileRepository;

	public static final String SUCESS_BLOCKED_USER_STORIES = "User blocked sucessfully from your stories.";

	public static final String ALREADY_BLOCKED_USER_STORIES_ERROR = "User already blocked from your stories.";

	public static final String SUCESS_UNBLOCKED_USER_STORIES = "User unblocked sucesfully from your stories.";

	public static final String ALREADY_UNBLOCKED_USER_STORIES_ERROR = "User unblocked from viewing your stories.";

	public static final String SUCESS_BLOCKED_USER = "User blocked sucessfully.";

	public static final String ALREADY_BLOCKED_USER_ERROR = "User already blocked.";

	public static final String SUCESS_UNBLOCKED_USER = "User unblocked sucesfully.";

	public static final String ALREADY_UNBLOCKED_USER_ERROR = "User already unblocked.";

	/**
	 * Method that returns true if the user tried to block another user and it's
	 * already blocked (either stories or profile).
	 *
	 * @param user
	 *            the user
	 * @param blockedUser
	 *            the blockedUser
	 * @param type
	 *            the type of block (either stories or profile)
	 * 
	 * @return true, if successful
	 */
	public boolean existBlocks(UserProfile user, UserProfile blockedUser, BlockType type) {
		return blocksRepository.checkBlock(user, blockedUser, type);
	}

	/**
	 * Method that allows an user to block some existing user.
	 *
	 * @param user
	 *            the blocking user
	 * @param blockedUser
	 *            the user to be blocked
	 * @param type
	 *            the block type
	 * @return the block
	 * 
	 * @throws InstanceNotFoundException
	 *             the instance not found exception
	 * @throws AlreadyBlockedException
	 *             the already blocked exception
	 */
	@Transactional(rollbackFor = Exception.class)
	public Blocks newBlocks(UserProfile user, UserProfile blockedUser, BlockType type)
			throws InstanceNotFoundException, AlreadyBlockedException {
		if (user == null) {
			throw new NullPointerException("The user param cannot be null.");
		}
		if (blockedUser == null) {
			throw new NullPointerException("The user param cannot be null.");
		}
		if (!userProfileRepository.exists(user.getEmail())) {
			throw new InstanceNotFoundException("The user with email" + user.getEmail() + " doesn't exist.");
		}
		if (!userProfileRepository.exists(blockedUser.getEmail())) {
			throw new InstanceNotFoundException("The user with email" + user.getEmail() + " doesn't exist.");
		}
		Blocks block = new Blocks(user, blockedUser, type);

		if (blocksRepository.checkBlock(user, blockedUser, type)) {
			throw new AlreadyBlockedException("The user has been already blocked by you");
		} else {
			blocksRepository.save(block);
		}

		return block;
	}

	/**
	 * Method that returns all the blocks made by a specific user.
	 *
	 * @param user
	 *            the owner of the blocks
	 * 
	 * @return the list of blocks
	 * 
	 * @throws InstanceNotFoundException
	 *             the instance not found exception
	 */
	@Transactional(noRollbackFor = Exception.class)
	public List<Blocks> findUserBlocks(UserProfile user) throws InstanceNotFoundException {
		if (user == null) {
			throw new NullPointerException("The user param cannot be null.");
		}
		if (!userProfileRepository.exists(user.getEmail())) {
			throw new InstanceNotFoundException("The user with the id" + user.getUser_id() + " doesn't exist.");
		}
		return blocksRepository.findBlocksByUser(user);
	}

	/**
	 * Unblocks an user for the designed user and type of block.
	 *
	 * @param user
	 *            the blocking user
	 * @param blockedUser
	 *            the blocked user
	 * @param type
	 *            the block type
	 * @throws InstanceNotFoundException
	 *             the instance not found exception
	 * @throws NotBlockedYetException
	 *             the not liked yet exception
	 */
	@Transactional(noRollbackFor = Exception.class)
	public void deleteUserBlock(UserProfile user, UserProfile blockedUser, BlockType type)
			throws InstanceNotFoundException, NotBlockedYetException {
		if (user == null) {
			throw new NullPointerException("The user param cannot be null.");
		}
		if (blockedUser == null) {
			throw new NullPointerException("The blocked param cannot be null.");
		}
		if (!userProfileRepository.exists(user.getEmail())) {
			throw new InstanceNotFoundException("The user with the id" + user.getUser_id() + " doesn't exist.");
		}
		if (!userProfileRepository.exists(blockedUser.getEmail())) {
			throw new InstanceNotFoundException("The user with the id" + blockedUser.getUser_id() + " doesn't exist.");
		}
		if (existBlocks(user, blockedUser, type)) {
			Blocks block = blocksRepository.findBlocksByEverything(user, blockedUser, type);
			blocksRepository.delete(block);
		} else {
			throw new NotBlockedYetException("The user is not blocked by you");
		}
	}

	/**
	 * Block a user from viewing your stories.
	 *
	 * @param email
	 *            the user email
	 * @param blocked_email
	 *            the blockedUser email
	 * @return the success/error message.
	 * @throws InstanceNotFoundException
	 *             the instance not found exception
	 * @throws AlreadyBlockedException
	 *             the Already blocked exception
	 */
	@Transactional(rollbackFor = Exception.class)
	public String blockStories(String email, String blocked_email)
			throws InstanceNotFoundException, AlreadyBlockedException {

		if (!userProfileRepository.exists(email)) {
			throw new InstanceNotFoundException("The user with that email doesn't exist.");
		}

		if (!userProfileRepository.exists(blocked_email)) {
			throw new InstanceNotFoundException("The user with that blocked_email doesn't exist.");
		}

		UserProfile user = userProfileRepository.findOneByEmail(email);
		UserProfile blockedUser = userProfileRepository.findOneByEmail(blocked_email);

		String error_message = "";

		if (existBlocks(user, blockedUser, BlockType.STORIES)) {
			error_message = ALREADY_BLOCKED_USER_STORIES_ERROR;
		} else {
			newBlocks(user, blockedUser, BlockType.STORIES);
			error_message = SUCESS_BLOCKED_USER_STORIES;
		}

		return error_message;
	}

	/**
	 * Unblock a user from viewing your stories.
	 *
	 * @param email
	 *            the user email
	 * @param blocked_email
	 *            the blockedUser email
	 * @return the success/error message.
	 * @throws InstanceNotFoundException
	 *             the instance not found exception
	 * @throws NotBlockedYetException
	 *             the not blocked yet exception
	 */
	@Transactional(noRollbackFor = Exception.class)
	public String unblockStories(String email, String blocked_email)
			throws InstanceNotFoundException, NotBlockedYetException {
		if (!userProfileRepository.exists(email)) {
			throw new InstanceNotFoundException("The user with that email doesn't exists.");
		}
		if (!userProfileRepository.exists(blocked_email)) {
			throw new InstanceNotFoundException("The user with that blocked_email doesn't exists.");
		}

		UserProfile user = userProfileRepository.findOneByEmail(email);
		UserProfile blockedUser = userProfileRepository.findOneByEmail(blocked_email);
		String error_message = "";

		if (!existBlocks(user, blockedUser, BlockType.STORIES)) {
			error_message = ALREADY_UNBLOCKED_USER_STORIES_ERROR;
		} else {
			deleteUserBlock(user, blockedUser, BlockType.STORIES);
			error_message = SUCESS_UNBLOCKED_USER_STORIES;
		}

		return error_message;
	}

	/**
	 * Block a user from viewing your uploaded posts.
	 *
	 * @param email
	 *            the user email
	 * @param blocked_email
	 *            the blockedUser email
	 * @return the success/error message.
	 * @throws InstanceNotFoundException
	 *             the instance not found exception
	 * @throws AlreadyBlockedException
	 *             the already blocked exception
	 */
	@Transactional(rollbackFor = Exception.class)
	public String blockPosts(String email, String blocked_email)
			throws InstanceNotFoundException, AlreadyBlockedException {

		if (!userProfileRepository.exists(email)) {
			throw new InstanceNotFoundException("The user with that email doesn't exist.");
		}

		if (!userProfileRepository.exists(blocked_email)) {
			throw new InstanceNotFoundException("The user with that blocked_email doesn't exist.");
		}

		UserProfile user = userProfileRepository.findOneByEmail(email);
		UserProfile blockedUser = userProfileRepository.findOneByEmail(blocked_email);

		String error_message = "";

		if (existBlocks(user, blockedUser, BlockType.PROFILE)) {
			error_message = ALREADY_BLOCKED_USER_ERROR;
		} else {
			newBlocks(user, blockedUser, BlockType.PROFILE);
			error_message = SUCESS_BLOCKED_USER;
		}

		return error_message;
	}

	/**
	 * Unblock a user from viewing your uploaded posts.
	 *
	 * @param email
	 *            the user email
	 * @param blocked_email
	 *            the blockedUser email
	 * @return the success/error message.
	 * @throws InstanceNotFoundException
	 *             the instance not found exception
	 * @throws NotBlockedYetException
	 *             the not blocked yet exception
	 */
	@Transactional(noRollbackFor = Exception.class)
	public String unblockPosts(String email, String blocked_email)
			throws InstanceNotFoundException, NotBlockedYetException {
		if (!userProfileRepository.exists(email)) {
			throw new InstanceNotFoundException("The user with that email doesn't exists.");
		}
		if (!userProfileRepository.exists(blocked_email)) {
			throw new InstanceNotFoundException("The user with that blocked_email doesn't exists.");
		}

		UserProfile user = userProfileRepository.findOneByEmail(email);
		UserProfile blockedUser = userProfileRepository.findOneByEmail(blocked_email);
		String error_message = "";

		if (!existBlocks(user, blockedUser, BlockType.PROFILE)) {
			error_message = ALREADY_UNBLOCKED_USER_ERROR;
		} else {
			deleteUserBlock(user, blockedUser, BlockType.PROFILE);
			error_message = SUCESS_UNBLOCKED_USER;
		}

		return error_message;
	}

}
