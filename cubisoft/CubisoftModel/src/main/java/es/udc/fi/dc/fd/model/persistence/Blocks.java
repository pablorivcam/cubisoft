package es.udc.fi.dc.fd.model.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Blocks {

	/** The Blocks id. */
	private Long blocks_id = -1L;

	/** The user. */
	private UserProfile user;

	/** The blocked user. */
	private UserProfile target;

	/** The possible types of blocks. */
	public enum BlockType {
		STORIES, PROFILE
	}

	private BlockType blockType;

	public Blocks() {

	}

	public Blocks(UserProfile user, UserProfile target, BlockType type) {
		this.user = user;
		this.target = target;
		this.blockType = type;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, unique = true)
	public Long getBlocks_id() {
		return blocks_id;
	}

	public void setBlocks_id(Long blocks_id) {
		this.blocks_id = blocks_id;
	}

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user")
	public UserProfile getUser() {
		return user;
	}

	public void setUser(UserProfile user) {
		this.user = user;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "target")
	public UserProfile getTarget() {
		return target;
	}

	public void setTarget(UserProfile target) {
		this.target = target;
	}

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public BlockType getBlockType() {
		return blockType;
	}

	public void setBlockType(BlockType blockType) {
		this.blockType = blockType;
	}

}
