package es.udc.fi.dc.fd.test;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Commit;

import es.udc.fi.dc.fd.controller.picture.PictureUploaderController;
import es.udc.fi.dc.fd.repository.UserProfileRepository;

public class ITInterfaceWeb {

	public static final String TEST_USER_LOGIN = "testLogin";
	public static final String TEST_USER_PASSWORD = "testPassword";
	public static final String TEST_USER_NAME = "testName";
	public static final String TEST_USER_SURNAME = "testSurname";
	public static final String TEST_USER_EMAIL = "test@test.com";

	public static final String TEST_ADMIN_EMAIL = "admin@admin.com";
	public static final String TEST_ADMIN_PASSWORD = "admin";

	public static final String TEST_PRIVATE_EMAIL = "private@user.com";
	public static final String TEST_PRIVATE_PASSWORD = "admin";

	@Autowired
	UserProfileRepository userProfileRepository;

	@Autowired
	PictureUploaderController pictureUploaderController;

	private WebDriver driver;
	private String baseUrl;

	@Before
	public void openFirefox() {
		System.setProperty("webdriver.gecko.driver", "geckodriver");

		driver = new FirefoxDriver();

		driver.manage().window().maximize();
		baseUrl = "http://localhost:17070/cubisoft/";

	}

	@After
	public void closeFirefox() {
		driver.quit();
	}

	// Register Test
	@Test
	@Commit
	public void TestA() throws Exception {

		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + "signup");

		Thread.sleep(500);

		WebElement loginElement = driver.findElement(By.id("login"));
		loginElement.sendKeys(TEST_USER_LOGIN);

		WebElement passElement = driver.findElement(By.id("password"));
		passElement.sendKeys(TEST_USER_PASSWORD);

		WebElement nameElement = driver.findElement(By.id("name"));
		nameElement.sendKeys(TEST_USER_NAME);

		WebElement surnameElement = driver.findElement(By.id("surname"));
		surnameElement.sendKeys(TEST_USER_NAME);

		WebElement emailElement = driver.findElement(By.id("email"));
		emailElement.sendKeys(TEST_USER_EMAIL);

		driver.findElement(By.id("signUpButton")).click();

		Thread.sleep(500);

		assertTrue(driver.findElement(By.id("myFeed")).isDisplayed());

	}

	// RegisterTest with error
	@Test
	@Commit
	public void TestB() throws Exception {

		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + "signup");

		Thread.sleep(500);

		WebElement passElement = driver.findElement(By.id("password"));
		passElement.sendKeys(TEST_USER_PASSWORD);

		WebElement nameElement = driver.findElement(By.id("name"));
		nameElement.sendKeys(TEST_USER_NAME);

		WebElement surnameElement = driver.findElement(By.id("surname"));
		surnameElement.sendKeys(TEST_USER_NAME);

		WebElement emailElement = driver.findElement(By.id("email"));
		emailElement.sendKeys(TEST_USER_EMAIL);

		driver.findElement(By.id("signUpButton")).click();

		Thread.sleep(500);

		assertTrue(driver.findElement(By.id("signUpButton")).isDisplayed());

	}

	// upload photo page
	@Test
	@Commit
	public void TestC() throws Exception {

		driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
		driver.get(baseUrl + "signin");

		Thread.sleep(500);

		WebElement emailElement = driver.findElement(By.id("email"));
		emailElement.sendKeys(TEST_USER_EMAIL);

		WebElement passElement = driver.findElement(By.id("inputPassword"));
		passElement.sendKeys(TEST_USER_PASSWORD);

		driver.findElement(By.id("loginButton")).click();

		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + "picture/upload");

		Thread.sleep(500);

		assertTrue(driver.findElement(By.id("upload")).isDisplayed());

	}

	// SignIn/findUsers/follow/unfollow/request/cancel request
	@Test
	@Commit
	public void TestD() throws Exception {

		driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
		driver.get(baseUrl + "signin");

		Thread.sleep(500);

		WebElement emailElement = driver.findElement(By.id("email"));
		emailElement.sendKeys(TEST_USER_EMAIL);

		WebElement passElement = driver.findElement(By.id("inputPassword"));
		passElement.sendKeys(TEST_USER_PASSWORD);

		driver.findElement(By.id("loginButton")).click();

		Thread.sleep(500);

		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + "settings/view");

		driver.findElement(By.id("private")).click();

		Thread.sleep(500);

		assertTrue(driver.findElement(By.id("public")).isDisplayed());

		driver.findElement(By.id("public")).click();

		Thread.sleep(500);

		assertTrue(driver.findElement(By.id("private")).isDisplayed());

		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + "account/list");

		Thread.sleep(500);

		WebElement findElement = driver.findElement(By.id("keywords"));
		findElement.sendKeys("megalodon");

		driver.findElement(By.id("findButton")).click();

		driver.findElement(By.id("followButton")).click();

		Thread.sleep(500);

		assertTrue(driver.findElement(By.id("unfollowButton")).isDisplayed());

		Thread.sleep(500);

		findElement = driver.findElement(By.id("keywords"));
		findElement.sendKeys("private");

		driver.findElement(By.id("findButton")).click();

		driver.findElement(By.id("requestButton")).click();

		Thread.sleep(500);

		assertTrue(driver.findElement(By.id("unrequestButton")).isDisplayed());

		Thread.sleep(500);

		findElement = driver.findElement(By.id("keywords"));
		findElement.sendKeys("megalodon");

		driver.findElement(By.id("findButton")).click();

		driver.findElement(By.id("unfollowButton")).click();

		Thread.sleep(500);

		assertTrue(driver.findElement(By.id("followButton")).isDisplayed());

		Thread.sleep(500);

		findElement = driver.findElement(By.id("keywords"));
		findElement.sendKeys("private");

		driver.findElement(By.id("findButton")).click();

		driver.findElement(By.id("unrequestButton")).click();

		Thread.sleep(500);

		assertTrue(driver.findElement(By.id("requestButton")).isDisplayed());

	}

	// Request accepted when private profile change to public profile
	@Test
	@Commit
	public void TestE() throws Exception {

		// Sign In

		driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
		driver.get(baseUrl + "signin");

		Thread.sleep(500);

		WebElement emailElement = driver.findElement(By.id("email"));
		emailElement.sendKeys(TEST_ADMIN_EMAIL);

		WebElement passElement = driver.findElement(By.id("inputPassword"));
		passElement.sendKeys(TEST_ADMIN_PASSWORD);

		driver.findElement(By.id("loginButton")).click();

		Thread.sleep(500);

		// Find private profile and send request

		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + "account/list");

		WebElement findElement = driver.findElement(By.id("keywords"));
		findElement.sendKeys("private");

		driver.findElement(By.id("findButton")).click();

		driver.findElement(By.id("requestButton")).click();

		Thread.sleep(500);

		assertTrue(driver.findElement(By.id("unrequestButton")).isDisplayed());

		Thread.sleep(500);

		// logout

		driver.findElement(By.id("logOut")).click();

		Thread.sleep(500);

		// Sign in private profile

		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + "signin");

		emailElement = driver.findElement(By.id("email"));
		emailElement.clear();
		emailElement.sendKeys(TEST_PRIVATE_EMAIL);

		passElement = driver.findElement(By.id("inputPassword"));
		passElement.clear();
		passElement.sendKeys(TEST_PRIVATE_PASSWORD);

		driver.findElement(By.id("loginButton")).click();

		Thread.sleep(500);

		// check list of requests

		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + "request/list");

		Thread.sleep(500);

		assertTrue(driver.findElement(By.id("requestOk")).isDisplayed());

		Thread.sleep(500);

		// Change profile to public and to private

		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + "settings/view");

		driver.findElement(By.id("public")).click();

		Thread.sleep(500);

		assertTrue(driver.findElement(By.id("private")).isDisplayed());

		Thread.sleep(500);

		driver.findElement(By.id("private")).click();

		Thread.sleep(500);

		assertTrue(driver.findElement(By.id("public")).isDisplayed());

		Thread.sleep(500);

		// logout

		driver.findElement(By.id("logOut")).click();

		Thread.sleep(500);

		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + "signin");

		emailElement = driver.findElement(By.id("email"));
		emailElement.sendKeys(TEST_ADMIN_EMAIL);

		passElement = driver.findElement(By.id("inputPassword"));
		passElement.sendKeys(TEST_ADMIN_PASSWORD);

		driver.findElement(By.id("loginButton")).click();

		Thread.sleep(500);

		// check follows

		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + "followed/list");

		Thread.sleep(500);

		assertTrue(driver.findElement(By.id("unfollowButton")).isDisplayed());

		Thread.sleep(500);

		driver.findElement(By.id("unfollowButton")).click();

	}

	// Accept/Cancel request
	@Test
	@Commit
	public void TestF() throws Exception {

		// Sign In

		driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
		driver.get(baseUrl + "signin");

		Thread.sleep(500);

		WebElement emailElement = driver.findElement(By.id("email"));
		emailElement.sendKeys(TEST_ADMIN_EMAIL);

		WebElement passElement = driver.findElement(By.id("inputPassword"));
		passElement.sendKeys(TEST_ADMIN_PASSWORD);

		driver.findElement(By.id("loginButton")).click();

		Thread.sleep(500);

		// Find private profile and send request

		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + "account/list");

		WebElement findElement = driver.findElement(By.id("keywords"));
		findElement.sendKeys("private");

		driver.findElement(By.id("findButton")).click();

		driver.findElement(By.id("requestButton")).click();

		Thread.sleep(500);

		assertTrue(driver.findElement(By.id("unrequestButton")).isDisplayed());

		Thread.sleep(500);

		// logout

		driver.findElement(By.id("logOut")).click();

		Thread.sleep(500);

		// Sign in private profile

		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + "signin");

		emailElement = driver.findElement(By.id("email"));
		emailElement.clear();
		emailElement.sendKeys(TEST_PRIVATE_EMAIL);

		passElement = driver.findElement(By.id("inputPassword"));
		passElement.clear();
		passElement.sendKeys(TEST_PRIVATE_PASSWORD);

		driver.findElement(By.id("loginButton")).click();

		Thread.sleep(500);

		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + "followers/list");

		Thread.sleep(500);

		// check list of requests

		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + "request/list");

		Thread.sleep(500);

		assertTrue(driver.findElement(By.id("requestOk")).isDisplayed());

		Thread.sleep(500);

		driver.findElement(By.id("requestOk")).click();

		Thread.sleep(500);

		// logout

		driver.findElement(By.id("logOut")).click();

		Thread.sleep(500);

		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + "signin");

		emailElement = driver.findElement(By.id("email"));
		emailElement.sendKeys(TEST_ADMIN_EMAIL);

		passElement = driver.findElement(By.id("inputPassword"));
		passElement.sendKeys(TEST_ADMIN_PASSWORD);

		driver.findElement(By.id("loginButton")).click();

		Thread.sleep(500);

		// check follows

		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + "followed/list");

		Thread.sleep(500);

		assertTrue(driver.findElement(By.id("unfollowButton")).isDisplayed());

		Thread.sleep(500);

		driver.findElement(By.id("unfollowButton")).click();

		Thread.sleep(500);

		// logout

		driver.findElement(By.id("logOut")).click();

		Thread.sleep(500);

		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + "signin");

		emailElement = driver.findElement(By.id("email"));
		emailElement.sendKeys(TEST_USER_EMAIL);

		passElement = driver.findElement(By.id("inputPassword"));
		passElement.sendKeys(TEST_USER_PASSWORD);

		driver.findElement(By.id("loginButton")).click();

		Thread.sleep(500);

		// Find private profile and send request

		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + "account/list");

		findElement = driver.findElement(By.id("keywords"));
		findElement.sendKeys("private");

		driver.findElement(By.id("findButton")).click();

		driver.findElement(By.id("requestButton")).click();

		Thread.sleep(500);

		assertTrue(driver.findElement(By.id("unrequestButton")).isDisplayed());

		Thread.sleep(500);

		// logout

		driver.findElement(By.id("logOut")).click();

		Thread.sleep(500);

		// Sign in private profile

		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + "signin");

		emailElement = driver.findElement(By.id("email"));
		emailElement.clear();
		emailElement.sendKeys(TEST_PRIVATE_EMAIL);

		passElement = driver.findElement(By.id("inputPassword"));
		passElement.clear();
		passElement.sendKeys(TEST_PRIVATE_PASSWORD);

		driver.findElement(By.id("loginButton")).click();

		Thread.sleep(500);

		// check list of requests

		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + "request/list");

		Thread.sleep(500);

		assertTrue(driver.findElement(By.id("requestCancel")).isDisplayed());

		Thread.sleep(500);

		driver.findElement(By.id("requestCancel")).click();

		Thread.sleep(500);

		// logout

		driver.findElement(By.id("logOut")).click();

		Thread.sleep(500);

		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + "signin");

		emailElement = driver.findElement(By.id("email"));
		emailElement.sendKeys(TEST_USER_EMAIL);

		passElement = driver.findElement(By.id("inputPassword"));
		passElement.sendKeys(TEST_USER_PASSWORD);

		driver.findElement(By.id("loginButton")).click();

		Thread.sleep(500);

		// check follows

		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + "followed/list");

	}

	// GlobalFeedTest with reshare

	@Test
	@Commit
	public void TestG() throws Exception {

		driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
		driver.get(baseUrl + "signin");

		Thread.sleep(500);

		WebElement emailElement = driver.findElement(By.id("email"));
		emailElement.sendKeys(TEST_USER_EMAIL);

		WebElement passElement = driver.findElement(By.id("inputPassword"));
		passElement.sendKeys(TEST_USER_PASSWORD);

		driver.findElement(By.id("loginButton")).click();

		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + "account/list");

		Thread.sleep(500);

		WebElement findElement = driver.findElement(By.id("keywords"));
		findElement.sendKeys("admin");

		driver.findElement(By.id("findButton")).click();

		driver.findElement(By.id("followButton")).click();

		Thread.sleep(500);

		assertTrue(driver.findElement(By.id("unfollowButton")).isDisplayed());

		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + "followed/list");

		Thread.sleep(500);

		assertTrue(driver.findElement(By.id("unfollowButton")).isDisplayed());

		Thread.sleep(500);

		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + "post/globalFeed");

		Thread.sleep(2000);

		driver.findElement(By.id("like")).click();

		assertTrue(driver.findElement(By.id("unlike")).isDisplayed());

		Thread.sleep(2000);

		driver.findElement(By.id("unlike")).click();

		assertTrue(driver.findElement(By.id("like")).isDisplayed());

		Thread.sleep(1000);

		WebElement commentElement = driver.findElement(By.id("commentText"));
		commentElement.clear();
		commentElement.sendKeys("comentario");

		Thread.sleep(1000);

		driver.findElement(By.id("AddComment")).click();

		Thread.sleep(1000);

		driver.findElement(By.id("replyComment")).click();

		commentElement = driver.findElement(By.id("replyText"));
		commentElement.clear();
		commentElement.sendKeys("respuesta");

		Thread.sleep(1000);

		driver.findElement(By.id("replyCommentOk")).click();

		Thread.sleep(1000);

		driver.findElement(By.id("reshare")).click();

		driver.findElement(By.id("reshareOk")).click();

		Thread.sleep(2000);

		driver.findElement(By.id("deleteComment")).click();

		driver.findElement(By.id("deleteCommentOk")).click();

		Thread.sleep(2000);

		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + "post/myFeed");

		Thread.sleep(1000);

		assertTrue(driver.findElement(By.id("like")).isDisplayed());

	}

	// story
	@Test
	@Commit
	public void TestH() throws Exception {

		driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
		driver.get(baseUrl + "signin");

		Thread.sleep(500);

		WebElement emailElement = driver.findElement(By.id("email"));
		emailElement.sendKeys(TEST_ADMIN_EMAIL);

		WebElement passElement = driver.findElement(By.id("inputPassword"));
		passElement.sendKeys(TEST_ADMIN_PASSWORD);

		driver.findElement(By.id("loginButton")).click();

		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + "post/myFeed");

		Thread.sleep(2000);

		driver.findElement(By.id("story")).click();

		Thread.sleep(2000);

	}

	// myFeedPublicTest

	@Test
	@Commit
	public void TestI() throws Exception {

		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + "account/list");

		Thread.sleep(500);

		WebElement findElement = driver.findElement(By.id("keywords"));
		findElement.sendKeys("admin");

		driver.findElement(By.id("findButton")).click();

		Thread.sleep(500);

		driver.findElement(By.id("userClick")).click();

		Thread.sleep(500);

		assertTrue(driver.findElement(By.id("newComment")).isDisplayed());

	}

	// MyFeed Test with delete
	@Test
	@Commit
	public void TestJ() throws Exception {

		driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
		driver.get(baseUrl + "signin");

		Thread.sleep(500);

		WebElement emailElement = driver.findElement(By.id("email"));
		emailElement.sendKeys(TEST_ADMIN_EMAIL);

		WebElement passElement = driver.findElement(By.id("inputPassword"));
		passElement.sendKeys(TEST_ADMIN_PASSWORD);

		driver.findElement(By.id("loginButton")).click();

		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + "post/myFeed");

		Thread.sleep(2000);

		driver.findElement(By.id("modify")).click();

		WebElement modifyElement = driver.findElement(By.id("description"));
		modifyElement.clear();
		modifyElement.sendKeys("descriptionModify");

		Thread.sleep(2000);

		driver.findElement(By.id("modifyOk")).click();

		assertTrue(driver.findElement(By.id("like")).isDisplayed());

		Thread.sleep(2000);

		driver.findElement(By.id("like")).click();

		assertTrue(driver.findElement(By.id("unlike")).isDisplayed());

		Thread.sleep(2000);

		driver.findElement(By.id("unlike")).click();

		assertTrue(driver.findElement(By.id("like")).isDisplayed());

		Thread.sleep(500);

		WebElement commentElement = driver.findElement(By.id("commentText"));
		commentElement.clear();
		commentElement.sendKeys("comentario");

		driver.findElement(By.id("newComment")).click();

		Thread.sleep(1000);

		driver.findElement(By.id("modifyComment")).click();

		modifyElement = driver.findElement(By.id("newContentComment"));
		modifyElement.clear();
		modifyElement.sendKeys("modify");

		Thread.sleep(2000);

		driver.findElement(By.id("modifyCommentOk")).click();

		Thread.sleep(2000);

		driver.findElement(By.id("showView")).click();

		driver.findElement(By.id("showViewOk")).click();

		assertTrue(driver.findElement(By.id("like")).isDisplayed());

		Thread.sleep(2000);

		driver.findElement(By.id("deleteComment")).click();

		driver.findElement(By.id("deleteCommentOk")).click();

		Thread.sleep(2000);

		driver.findElement(By.id("delete")).click();

		driver.findElement(By.id("deleteOk")).click();

		assertTrue(driver.findElement(By.id("upload")).isDisplayed());

	}

	// Blocks unblocks
	@Test
	@Commit
	public void TestK() throws Exception {

		driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
		driver.get(baseUrl + "signin");

		Thread.sleep(500);

		WebElement emailElement = driver.findElement(By.id("email"));
		emailElement.sendKeys(TEST_ADMIN_EMAIL);

		WebElement passElement = driver.findElement(By.id("inputPassword"));
		passElement.sendKeys(TEST_ADMIN_PASSWORD);

		driver.findElement(By.id("loginButton")).click();

		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + "followers/list");

		Thread.sleep(2000);

		driver.findElement(By.id("blockPosts")).click();

		Thread.sleep(500);

		driver.findElement(By.id("blockStories")).click();

		Thread.sleep(500);

		driver.findElement(By.id("unblockPosts")).click();

		Thread.sleep(500);

		driver.findElement(By.id("unblockStories")).click();

		Thread.sleep(500);

		driver.findElement(By.id("logOut")).click();

		Thread.sleep(500);

	}

	// kick follower
	@Test
	@Commit
	public void TestL() throws Exception {

		driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
		driver.get(baseUrl + "signin");

		Thread.sleep(500);

		WebElement emailElement = driver.findElement(By.id("email"));
		emailElement.sendKeys(TEST_ADMIN_EMAIL);

		WebElement passElement = driver.findElement(By.id("inputPassword"));
		passElement.sendKeys(TEST_ADMIN_PASSWORD);

		driver.findElement(By.id("loginButton")).click();

		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + "followers/list");

		Thread.sleep(500);

		// Find private profile and send request

		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + "account/list");

		WebElement findElement = driver.findElement(By.id("keywords"));
		findElement.sendKeys("private");

		driver.findElement(By.id("findButton")).click();

		driver.findElement(By.id("requestButton")).click();

		Thread.sleep(500);

		assertTrue(driver.findElement(By.id("unrequestButton")).isDisplayed());

		Thread.sleep(500);

		// logout

		driver.findElement(By.id("logOut")).click();

		Thread.sleep(500);

		// Sign in private profile

		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + "signin");

		emailElement = driver.findElement(By.id("email"));
		emailElement.clear();
		emailElement.sendKeys(TEST_PRIVATE_EMAIL);

		passElement = driver.findElement(By.id("inputPassword"));
		passElement.clear();
		passElement.sendKeys(TEST_PRIVATE_PASSWORD);

		driver.findElement(By.id("loginButton")).click();

		Thread.sleep(500);

		// check list of requests

		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + "request/list");

		Thread.sleep(500);

		assertTrue(driver.findElement(By.id("requestOk")).isDisplayed());

		Thread.sleep(500);

		driver.findElement(By.id("requestOk")).click();

		Thread.sleep(500);

		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + "followers/list");

		Thread.sleep(2000);

		driver.findElement(By.id("kick")).click();

		Thread.sleep(500);

		driver.findElement(By.id("logOut")).click();

		Thread.sleep(500);

		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + "signin");

		emailElement = driver.findElement(By.id("email"));
		emailElement.sendKeys(TEST_ADMIN_EMAIL);

		passElement = driver.findElement(By.id("inputPassword"));
		passElement.sendKeys(TEST_ADMIN_PASSWORD);

		driver.findElement(By.id("loginButton")).click();

		Thread.sleep(500);

		// check follows

		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.get(baseUrl + "followed/list");

	}

}
