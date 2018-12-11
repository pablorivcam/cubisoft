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

import es.udc.fi.dc.fd.repository.UserProfileRepository;

public class ITInterfaceWeb {

	public static final String TEST_USER_LOGIN = "testLogin";
	public static final String TEST_USER_PASSWORD = "testPassword";
	public static final String TEST_USER_NAME = "testName";
	public static final String TEST_USER_SURNAME = "testSurname";
	public static final String TEST_USER_EMAIL = "test@test.com";

	@Autowired
	UserProfileRepository userProfileRepository;

	private WebDriver driver;
	private String baseUrl;

	@Before
	public void openFirefox() {
		System.setProperty("webdriver.gecko.driver", "geckodriver.exe");

		driver = new FirefoxDriver();

		driver.manage().window().maximize();
		baseUrl = "http://localhost:17070/CubisoftWeb/";

	}

	@After
	public void closeFirefox() {
		driver.quit();
	}

	@Test
	@Commit
	public void RegisterTest() throws Exception {

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

	@Test
	@Commit
	public void SignInFindUserTest() throws Exception {

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

		Thread.sleep(500);

	}
}
