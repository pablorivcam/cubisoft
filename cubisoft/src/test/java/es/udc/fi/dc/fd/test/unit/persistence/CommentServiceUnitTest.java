package es.udc.fi.dc.fd.test.unit.persistence;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

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
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;

import es.udc.fi.dc.fd.model.Block;
import es.udc.fi.dc.fd.model.persistence.Comment;
import es.udc.fi.dc.fd.model.persistence.Picture;
import es.udc.fi.dc.fd.model.persistence.Post;
import es.udc.fi.dc.fd.model.persistence.UserProfile;
import es.udc.fi.dc.fd.model.persistence.UserProfile.UserType;
import es.udc.fi.dc.fd.repository.CommentRepository;
import es.udc.fi.dc.fd.repository.PostRepository;
import es.udc.fi.dc.fd.repository.UserProfileRepository;
import es.udc.fi.dc.fd.service.CommentService;

@RunWith(MockitoJUnitRunner.class)
public class CommentServiceUnitTest {

	public static final String TEST_LOGIN = "test";
	public static final String TEST_FIRSTNAME = "eve";
	public static final String TEST_LASTNAME = "Test";
	public static final String TEST_PASSWORD = "abc123..";
	public static final String TEST_EMAIL = "test@test.com";

	public static final String TEST_TEXT = "asdfg";

	@Mock
	private CommentRepository commentRepository;

	@Mock
	private UserProfileRepository userProfileRepository;

	@Mock
	private PostRepository postRepository;

	@InjectMocks
	private CommentService commentService;

	private Comment commentA, commentB, commentC;
	private UserProfile userA;
	private Post postA;
	private Picture picture;

	@Before
	public void initialize() {

		MockitoAnnotations.initMocks(this);

		userA = new UserProfile(TEST_LOGIN, TEST_FIRSTNAME, TEST_LASTNAME, TEST_PASSWORD, TEST_EMAIL, null, null,
				UserType.PUBLIC);
		postA = new Post(Calendar.getInstance(), picture, userA, (long) 0, (long) 0, (long) 0, false);

		commentA = new Comment(TEST_TEXT, Calendar.getInstance(), postA, userA, null);
		commentB = new Comment(TEST_TEXT + "2", Calendar.getInstance(), postA, userA, null);
		commentC = new Comment(TEST_TEXT + "3", Calendar.getInstance(), postA, userA, commentB);

	}

	@Test
	public void saveCommentTest() {
		Mockito.when(commentRepository.save(commentA)).thenReturn(commentA);
		assertEquals(commentService.save(commentA), commentA);
	}

	@Test
	public void findPostCommentsTest() {

		ArrayList<Comment> commentsFound = new ArrayList<>();
		commentsFound.add(commentA);
		commentsFound.add(commentB);

		ArrayList<Comment> commentsFound2 = new ArrayList<>();
		commentsFound2.add(commentA);
		commentsFound2.add(commentB);
		commentsFound2.add(commentC);

		Block<Comment> commentsBlock = new Block<>(commentsFound, true);
		Block<Comment> commentsBlock2 = new Block<>(commentsFound2, false);

		Mockito.when(commentRepository.findCommentsByPost(postA, PageRequest.of(0, 3))).thenReturn(commentsFound2);
		Mockito.when(commentRepository.findCommentsByPost(postA, PageRequest.of(0, 4))).thenReturn(commentsFound2);

		Block<Comment> expected1 = commentService.findPostComments(postA, 0, 2);
		Block<Comment> expected2 = commentService.findPostComments(postA, 0, 3);

		assertEquals(expected1.getExistMoreItems(), commentsBlock.getExistMoreItems());
		assertEquals(expected1.getItems().size(), commentsBlock.getItems().size());
		assertEquals(expected2.getExistMoreItems(), commentsBlock2.getExistMoreItems());
		assertEquals(expected2.getItems().size(), commentsBlock2.getItems().size());

		for (int i = 0; i < expected1.getItems().size(); i++) {
			assertEquals(expected1.getItems().get(i), commentsBlock.getItems().get(i));
		}
		for (int i = 0; i < expected2.getItems().size(); i++) {
			assertEquals(expected2.getItems().get(i), commentsBlock2.getItems().get(i));
		}

	}

	@Test(expected = NullPointerException.class)
	public void findUnexistentPostCommentsTest() {
		Mockito.when(commentService.findPostComments(null, 0, 0)).thenReturn(null);
	}

	@Test
	public void findParentCommentsByPostTest() {
		ArrayList<Comment> commentsFound = new ArrayList<>();
		commentsFound.add(commentA);
		commentsFound.add(commentB);

		ArrayList<Comment> commentsFound2 = new ArrayList<>();
		commentsFound2.add(commentA);

		Block<Comment> commentsBlock = new Block<>(commentsFound, true);
		Block<Comment> commentsBlock2 = new Block<>(commentsFound2, false);

		Mockito.when(commentRepository.findParentCommentsByPost(postA, PageRequest.of(0, 2))).thenReturn(commentsFound);
		Mockito.when(commentRepository.findParentCommentsByPost(postA, PageRequest.of(0, 3))).thenReturn(commentsFound);

		Block<Comment> expected1 = commentService.findPostParentComments(postA, 0, 1);
		Block<Comment> expected2 = commentService.findPostParentComments(postA, 0, 2);

		assertEquals(expected1.getExistMoreItems(), commentsBlock.getExistMoreItems());
		assertEquals(expected1.getItems().size(), commentsBlock.getItems().size());
		assertEquals(expected2.getExistMoreItems(), commentsBlock2.getExistMoreItems());
		assertEquals(expected2.getItems().size(), commentsBlock2.getItems().size());

		for (int i = 0; i < expected1.getItems().size(); i++) {
			assertEquals(expected1.getItems().get(i), commentsBlock.getItems().get(i));
		}
		for (int i = 0; i < expected2.getItems().size(); i++) {
			assertEquals(expected2.getItems().get(i), commentsBlock2.getItems().get(i));
		}

	}

	@Test(expected = NullPointerException.class)
	public void findParentCommentsByUnexistentPostTest() {
		Mockito.when(commentService.findPostParentComments(null, 0, 0)).thenReturn(null);
	}

	@Test
	public void addCommentTest() throws InstanceNotFoundException {

		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(true);
		Mockito.when(userProfileRepository.findOneByEmail(TEST_EMAIL)).thenReturn(userA);

		Mockito.when(postRepository.existsById(postA.getPost_id())).thenReturn(true);
		Mockito.when(postRepository.findPostByPostId(postA.getPost_id())).thenReturn(postA);

		assertThat(commentService.addComment(TEST_TEXT, postA.getPost_id(), TEST_EMAIL), is(equalTo(null)));

	}

	@Test(expected = InstanceNotFoundException.class)
	public void addCommentWithUnexistentUserTest() throws InstanceNotFoundException {
		Mockito.when(userProfileRepository.exists(TEST_EMAIL + "123")).thenReturn(false);

		assertThat(commentService.addComment(TEST_TEXT, postA.getPost_id(), TEST_EMAIL + "123"), is(equalTo(null)));

	}

	@Test(expected = InstanceNotFoundException.class)
	public void addCommentWithUnexistentPostTest() throws InstanceNotFoundException {

		Post p = new Post();

		Mockito.when(postRepository.existsById(p.getPost_id())).thenReturn(false);
		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(true);

		assertThat(commentService.addComment(TEST_TEXT, p.getPost_id(), TEST_EMAIL), is(equalTo(null)));

	}

	@Test
	public void modifyCommentTest() throws InstanceNotFoundException {
		Comment c = new Comment();

		Mockito.when(commentRepository.existsById(c.getComment_id())).thenReturn(true);
		Mockito.when(commentRepository.save(c)).thenReturn(c);

		Mockito.when(commentRepository.findById(c.getComment_id())).thenReturn(Optional.of(c));

		assertThat(commentService.modifyComment(c.getComment_id(), TEST_TEXT), is(equalTo(c)));

	}

	@Test(expected = InstanceNotFoundException.class)
	public void modifyUnexistentCommentTest() throws InstanceNotFoundException {
		Comment c = new Comment();

		Mockito.when(commentRepository.existsById(c.getComment_id())).thenReturn(false);

		assertThat(commentService.modifyComment(c.getComment_id(), TEST_TEXT), is(equalTo(null)));

	}

	@Test
	public void replyCommentTest() throws InstanceNotFoundException {

		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(true);
		Mockito.when(userProfileRepository.findOneByEmail(TEST_EMAIL)).thenReturn(userA);

		Mockito.when(commentRepository.existsById(commentA.getComment_id())).thenReturn(true);
		Mockito.when(commentRepository.findById(commentA.getComment_id())).thenReturn(Optional.of(commentA));

		assertThat(commentService.replyComment(commentA.getComment_id(), TEST_TEXT, userA.getEmail(),
				Calendar.getInstance()), is(equalTo(null)));

	}

	@Test(expected = InstanceNotFoundException.class)
	public void replyUnexistentCommentTest() throws InstanceNotFoundException {

		Comment c = new Comment();

		Mockito.when(commentRepository.existsById(c.getComment_id())).thenReturn(false);

		assertThat(commentService.replyComment(c.getComment_id(), TEST_TEXT, userA.getEmail(), Calendar.getInstance()),
				is(equalTo(null)));

	}

	@Test(expected = InstanceNotFoundException.class)
	public void replyUnexistentUserTest() throws InstanceNotFoundException {

		Comment c = new Comment();

		Mockito.when(userProfileRepository.exists(TEST_EMAIL)).thenReturn(false);
		Mockito.when(commentRepository.existsById(commentA.getComment_id())).thenReturn(true);

		assertThat(commentService.replyComment(c.getComment_id(), TEST_TEXT, userA.getEmail(), Calendar.getInstance()),
				is(equalTo(null)));

	}
}
