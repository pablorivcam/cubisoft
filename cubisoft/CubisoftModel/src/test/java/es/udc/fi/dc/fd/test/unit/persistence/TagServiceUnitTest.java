package es.udc.fi.dc.fd.test.unit.persistence;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import es.udc.fi.dc.fd.model.persistence.Tag;
import es.udc.fi.dc.fd.repository.TagRepository;
import es.udc.fi.dc.fd.service.TagService;

@RunWith(MockitoJUnitRunner.class)
public class TagServiceUnitTest {

	public static final String TEST_TAG_NAME = "tag";

	@Mock
	private TagRepository tagRepository;

	@InjectMocks
	private TagService tagService;

	private Tag tagA, tagB;

	@Before
	public void initialize() {
		tagA = new Tag(TEST_TAG_NAME + "1", null);
		tagB = new Tag(TEST_TAG_NAME + "2", null);
	}

	@Test
	public void findTagByKeywordsTest() {

		ArrayList<Tag> expected = new ArrayList<>();
		expected.add(tagA);
		expected.add(tagB);

		Mockito.when(tagRepository.findTagsByKeywords(TEST_TAG_NAME)).thenReturn(expected);

		assertThat(tagService.findTagsByKeywords(TEST_TAG_NAME), is(equalTo(expected)));

	}

}
