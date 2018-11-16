package es.udc.fi.dc.fd.test.unit.persistence;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import es.udc.fi.dc.fd.config.SecurityConfig;
import es.udc.fi.dc.fd.model.persistence.UserProfile;
import es.udc.fi.dc.fd.model.persistence.UserProfile.UserType;
import es.udc.fi.dc.fd.repository.UserProfileRepository;
import es.udc.fi.dc.fd.service.UserProfileService;

@RunWith(MockitoJUnitRunner.class)
public class UserProfileServiceTest {

	public static final String TEST_LOGIN = "test";
	public static final String TEST_FIRSTNAME = "eve";
	public static final String TEST_LASTNAME = "Test";
	public static final String TEST_PASSWORD = "abc123..";
	public static final String TEST_EMAIL = "test@test.com";

	public static final String TEST_DESCRIPTION = "asdfgh";
	public static final String KEYWORD = "eve";

	@Mock
	private UserProfileRepository userProfileRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UserProfileService userProfileService;

	private UserProfile userA, userB, userC;

	private User createUser(UserProfile account) {
		return new User(account.getEmail(), account.getPassword(), Collections.singleton(createAuthority()));
	}

	private Authentication authenticate(UserProfile account) {
		return new UsernamePasswordAuthenticationToken(createUser(account), null,
				Collections.singleton(createAuthority()));
	}

	private GrantedAuthority createAuthority() {
		return new SimpleGrantedAuthority(SecurityConfig.DEFAULT_ROLE);
	}

	@Before
	public void initialize() {
		userA = new UserProfile(TEST_LOGIN, TEST_FIRSTNAME, TEST_LASTNAME, TEST_PASSWORD, TEST_EMAIL, null, null,
				UserType.PUBLIC);
		userA.setUser_id(1L);

		userB = new UserProfile(TEST_LOGIN, TEST_FIRSTNAME + "11", TEST_LASTNAME, TEST_PASSWORD, TEST_EMAIL, null, null,
				UserType.PUBLIC);
		userB.setUser_id(2L);

		userC = new UserProfile(TEST_LOGIN, "11" + TEST_FIRSTNAME + "11", TEST_LASTNAME, TEST_PASSWORD, TEST_EMAIL,
				null, null, UserType.PUBLIC);
		userC.setUser_id(3L);

	}

	@Test
	public void SaveUserTest() {

		Mockito.when(userProfileRepository.save(userA)).thenReturn(userA);

		userA.setPassword(passwordEncoder.encode(userA.getPassword()));
		assertThat(userProfileService.save(userA), is(equalTo(userA)));

	}

	@Test
	public void loadUserByUsernameTest() {

		Mockito.when(userProfileRepository.findOneByEmail(userA.getEmail())).thenReturn(userA);
		User expected = createUser(userA);

		assertThat(userProfileService.loadUserByUsername(userA.getEmail()), is(equalTo(expected)));

	}
	
	@Test(expected = UsernameNotFoundException.class)
	public void loadUserByNullUsernameTest() throws UsernameNotFoundException{

		Mockito.when(userProfileRepository.findOneByEmail(userA.getEmail())).thenReturn(null);

		assertThat(userProfileService.loadUserByUsername(userA.getEmail()), is(equalTo(null)));

	}

	@Test
	public void ValidateUserTest() {

		Mockito.when(userProfileRepository.findOneByEmail(userA.getEmail())).thenReturn(userA);

		assertThat(userProfileService.validateUser(userA.getEmail(), userA.getPassword()), is(equalTo(userA)));

	}

	@Test
	public void SignInTest() {

		userProfileService.signin(userA);
		assertThat(SecurityContextHolder.getContext().getAuthentication(), is(equalTo(authenticate(userA))));

	}

	/* Se añaden 2 usuarios y se tienen obtener esos 2 */
	@Test
	public void ProfileByKeywordsTest() {

		ArrayList<UserProfile> usersList = new ArrayList<UserProfile>();
		usersList.add(userA);
		usersList.add(userC);

		Mockito.when(userProfileRepository.findUserProfileByKeywords(KEYWORD)).thenReturn(usersList);

		try {
			assertThat(userProfileService.findUserProfileByKeywords(KEYWORD), is(equalTo(usersList)));

		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	/* Se meten 2 usuarios y solo tiene que devolver 1 */
	@Test
	public void ProfileByKeywordsTest1() {

		ArrayList<UserProfile> usersList1 = new ArrayList<UserProfile>();
		usersList1.add(userC);

		Mockito.when(userProfileRepository.findUserProfileByKeywords("11")).thenReturn(usersList1);

		try {
			assertThat(userProfileService.findUserProfileByKeywords("11"), is(equalTo(usersList1)));

		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	/* Se meten 2 y no devuelve ninguno */
	@Test
	public void ProfileByKeywordsTest2() {

		ArrayList<UserProfile> usersList1 = new ArrayList<UserProfile>();
		Mockito.when(userProfileRepository.findUserProfileByKeywords("ola")).thenReturn(usersList1);

		try {
			assertThat(userProfileService.findUserProfileByKeywords("ola"), is(equalTo(usersList1)));

		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void ProfileByKeywordsTest3() {

		ArrayList<UserProfile> usersList = new ArrayList<UserProfile>();
		usersList.add(userB);
		usersList.add(userC);
		Mockito.when(userProfileRepository.findUserProfileByKeywords("ola")).thenReturn(usersList);

		try {
			assertThat(userProfileService.findUserProfileByKeywords("ola"), is(equalTo(usersList)));

		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void changeUserProfileTest() {

		Mockito.when(userProfileRepository.findOneByEmail(userA.getEmail())).thenReturn(userA);
		try {
			userProfileService.changeUserProfileType(userA, UserType.PRIVATE);
			UserProfile user_test = userProfileRepository.findOneByEmail(userA.getEmail());
			assertEquals(user_test.getUserType(), UserType.PRIVATE);

		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void getSetUserProfileTest() {
		UserProfile userD = new UserProfile();
		userD.setUser_id(4L);
		userD.setLogin(TEST_LOGIN);
		userD.setFirstName(TEST_FIRSTNAME);
		userD.setLastName(TEST_LASTNAME);
		userD.setPassword(TEST_PASSWORD);
		userD.setEmail(TEST_EMAIL);
		userD.setFollows(null);
		userD.setPosts(null);
		userD.setUserType(UserType.PUBLIC);

		UserProfile userE = new UserProfile(TEST_LOGIN, TEST_FIRSTNAME, TEST_LASTNAME, TEST_PASSWORD, TEST_EMAIL, null,
				null, UserType.PUBLIC);
		userE.setUser_id(5L);

		assertEquals(userD.getLogin(), userE.getLogin());
		assertEquals(userD.getFirstName(), userE.getFirstName());
		assertEquals(userD.getLastName(), userE.getLastName());
		assertEquals(userD.getPassword(), userE.getPassword());
		assertEquals(userD.getEmail(), userE.getEmail());
		assertEquals(userD.getFollows(), userE.getFollows());
		assertEquals(userD.getPosts(), userE.getPosts());
		assertEquals(userD.getUserType(), userE.getUserType());
	}

}