package es.udc.fi.dc.fd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import es.udc.fi.dc.fd.model.persistence.Blocks;
import es.udc.fi.dc.fd.model.persistence.Blocks.BlockType;
import es.udc.fi.dc.fd.model.persistence.UserProfile;

/**
 * The Interface BlocksRepository.
 */
@Repository("blocksRepository")
public interface BlocksRepository extends JpaRepository<Blocks, Long> {

	/**
	 * Find blocks by user.
	 *
	 * @param user
	 *            the user profile
	 * @return the list of blocks
	 */
	@Query("select b from Blocks b where b.user=:user")
	List<Blocks> findBlocksByUser(@Param("user") UserProfile user);

	/**
	 * Find blocks by blocked user.
	 *
	 * @param target
	 *            the user profile
	 * @return the list of blocks
	 */
	@Query("select b from Blocks b where b.target=:target")
	List<Blocks> findBlocksByBlockedUser(@Param("target") UserProfile target);

	/**
	 * Find one by blocks id.
	 *
	 * @param blocks_id
	 *            the blocks id
	 * @return the "blocks" (only 1)
	 */
	@Query("SELECT b FROM Blocks b where b.blocks_id=:blocks_id")
	Blocks findBlocksById(@Param("blocks_id") Long blocks_id);

	/**
	 * Find one by everything.
	 *
	 * @param user
	 *            the user
	 * @param target
	 *            the user receiving the block
	 * @param blockType
	 *            the block type (either PROFILE or STORIES)
	 * 
	 * @return the "blocks" (only 1)
	 */
	@Query("SELECT b FROM Blocks b WHERE b.user=:user AND b.target=:target AND b.blockType=:blockType")
	Blocks findBlocksByEverything(@Param("user") UserProfile user, @Param("target") UserProfile target,
			@Param("blockType") BlockType blockType);

	/**
	 * Check if a block exists
	 *
	 * @param user
	 *            the user
	 * @param target
	 *            the user receiving the block
	 * @param blockType
	 *            the block type (either PROFILE or STORIES)
	 * 
	 * @return a boolean (true if the block exists)
	 */
	@Query("SELECT count(b) > 0 FROM Blocks b WHERE b.user=:user AND b.target=:target AND b.blockType=:blockType")
	boolean checkBlock(@Param("user") UserProfile user, @Param("target") UserProfile target,
			@Param("blockType") BlockType blockType);

}
