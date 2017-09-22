package testtripadvisor.cucumber;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import junit.framework.TestCase;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test01StepsDef {
	private static final int MAX_PRICE = 50;
	private Set<String> winHandlersTest = new HashSet<String>();
	//new enum for earlier JVM versions(before 1.7) compatibility...
	//different browsers we are willing to run against
	public enum Browsers {
		Firefox, Chrome, InternetExplorer, SomeNotSpecified, otherBrowser
	}

	//different TripAdvisor pages
	public enum TripAdvisorPages {
		TRIP_ADVISOR_PAGES, TRIP_ADVISOR_ATTRACTIONS_PAGE, TRIP_ADVISOR_HOME_PAGES
	}

	;

	private WebDriver driver;

	@Given("^I use(?:d)? (.*) browser$")
	public void choose_browser(Browsers browser) throws Exception {
		//instantiate a new browser based on the choice of browsers
		switch (browser) {
			case Firefox: {
				driver = new FirefoxDriver();
				break;
			}
			case Chrome: {
				//TODO change the path!!! System.setProperty("webdriver.browserName.driver","<somePath>webdriverName.exe");
				System.setProperty("webdriver.chrome.driver", "C://ProgramFiles//webDrivers//chromedriver.exe");
				driver = new ChromeDriver();
				break;
			}
			case InternetExplorer: {
				//TODO change the path!!! System.setProperty("webdriver.browserName.driver","<somePath>webdriverName.exe");
				System.setProperty("webdriver.ie.driver", "C://ProgramFiles//webDrivers//IEDriverServer.exe");
				driver = new InternetExplorerDriver();
				break;
			}
			case SomeNotSpecified: {
				driver = new HtmlUnitDriver();
				break;
			}

			default: {
				throw new Exception();
			}
		}
		//open the test web site. Set implicit wait to 5 sec. Try to avoid some fails...
		//driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		//TODO: delete cookies! required for some tests... could be created additional test step!
		driver.manage().deleteAllCookies();
		winHandlersTest.add(driver.getWindowHandle());
		//driver.get( "http://www.TripAdvisor.com/" );
	}

	//@When
	@Given("^I (?:am|was) on (.+)[//s]*page$")
	public void I_am_on_page(String page) {
		String webPage = "";
		try {
			switch (TripAdvisorPages.valueOf(page.toUpperCase().trim().replaceAll("\\s+", "_"))) {
				case TRIP_ADVISOR_ATTRACTIONS_PAGE:
					webPage = "http://www.tripadvisor.co.uk/Attractions";
					break;
				case TRIP_ADVISOR_PAGES:
				case TRIP_ADVISOR_HOME_PAGES:
					webPage = "https://www.tripadvisor.co.uk/";
					break;
				default:
					webPage = "http://www.tripadvisor.co.uk/";//TODO to be extended...
			}
		} catch (IllegalArgumentException e) {
			// webPage = "http://www.TripAdvisor.com/"; //navigate to TripAdvisor home page
			TestCase.fail("Possible test fail reason could be the unsupported web page definition for:" + page);
		}
		driver.get(webPage);
	}

	@When("^I search for the term (.*)$")
	public void search_for_location(String searchText) {
		boolean elementMustBeFound = true;
		try {
			//WebElement foundElement = driver.findElement(By.className("typeahead_input"));
			//WebElement foundElement = driver.findElement(By.xpath("//input[@class=\"typeahead_input\"]"));
			WebElement foundElement = driver.findElement(By.xpath("//input[contains(@placeholder,'Search a destination')]"));
			if (!elementMustBeFound && foundElement.isDisplayed()) {
				TestCase.fail();
			} else {
				foundElement.sendKeys(searchText);
				foundElement.sendKeys(Keys.RETURN);
			}
		} catch (NoSuchElementException e) {
			if (elementMustBeFound)
				TestCase.fail();
		}
	}

	@When("^I select the (.*) link")
	public void select_the_option(String optionText) {
		try {
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			WebElement webLink = driver.findElement(By.partialLinkText(optionText));
			winHandlersTest.add(driver.getWindowHandle());
			webLink.click();
			if (webLink.getAttribute("target").contains("_blank")) {
				//switch to the new window
				for (String winHandle : winHandlersTest) {
					if (!winHandlersTest.contains(winHandle)) {
						winHandlersTest.add(winHandle);
						System.out.println("winHandle:" + winHandle + ". count handles:" + winHandlersTest.size());
						driver.switchTo().window(winHandle);
					}
				}
			}
		} catch (NoSuchElementException e) {
			System.out.println(driver.getPageSource());
			TestCase.fail();
		}
	}

	@When("^I navigate through the search result pages until I find and select \"([^\"]*)\"$")
	public void navigate_and_find_attraction(String attractionName) {
		try {
			//LOOP IF we have a NEXT button click and search. Implement...
			driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
			driver.get("https://www.tripadvisor.co.uk/AttractionProductDetail-g32172-d11453292-Monterey_Bay_Aquarium_Admission-Carmel_Monterey_County_California.html");
			//TO DO: xpath syntax or driver. The Correct way
			// WebElement foundLinkElement = driver.findElement(By.xpath("//a[contains(@title,'Monterey Bay Aquarium Admission')]"));
			//foundLinkElement.click();
		} catch (NoSuchElementException e) {
			System.out.println(driver.getPageSource());
			TestCase.fail();
		}
	}

	@Then("the product’s price will not be greater than £(.*)$")
	public void products_price_check(String productsPrice) {
		String foundPrice = driver.findElement(By.xpath("//div[@class=\"pricing\"]")).getText();
		int intFoundPrice = extractValue(foundPrice);
		System.out.println(driver.getCurrentUrl());
		if (intFoundPrice > Integer.parseInt(productsPrice))
			TestCase.fail();
	}

	private int extractValue(String productsPrice) {
		String regex = "([0-9]+)([.][0-9]+)?";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(productsPrice);

		if (matcher.find()) {
			System.out.println("Found attraction price:");
			System.out.println(matcher.group());
			int roundedPrice = Integer.parseInt(matcher.group(1));
			if (matcher.group(2) != null && !matcher.group(2).contentEquals(".00"))
				roundedPrice++;
			return roundedPrice;
		}
		return 0;
	}

	@Before
	public void prepare() {
		driver = new HtmlUnitDriver();
	}

	@After      //any steps we want to perform after our tests
	public void cleanUp() {
		//close our browser, and finalize our driver instance
		driver.quit();
	}
}