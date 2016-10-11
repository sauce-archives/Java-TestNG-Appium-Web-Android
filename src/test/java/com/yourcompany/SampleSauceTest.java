package com.yourcompany;

import com.saucelabs.common.SauceOnDemandAuthentication;
import com.saucelabs.common.SauceOnDemandSessionIdProvider;
import com.saucelabs.testng.SauceOnDemandAuthenticationProvider;
import com.saucelabs.testng.SauceOnDemandTestListener;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static org.junit.Assert.*;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Simple TestNG test which demonstrates being instantiated via a DataProvider in order to supply multiple browser/device combinations.
 */

@Listeners({SauceOnDemandTestListener.class})
public class SampleSauceTest implements SauceOnDemandSessionIdProvider, SauceOnDemandAuthenticationProvider {
    /**
     * Constructs a {@link com.saucelabs.common.SauceOnDemandAuthentication} instance using the supplied user name/access key.  To use the authentication
     * supplied by environment variables or from an external file, use the no-arg {@link com.saucelabs.common.SauceOnDemandAuthentication} constructor.
     */
    public SauceOnDemandAuthentication authentication = new SauceOnDemandAuthentication(System.getenv("SAUCE_USERNAME"), System.getenv("SAUCE_ACCESS_KEY"));

    /**
     * ThreadLocal variable which contains the  {@link WebDriver} instance which is used to perform browser interactions with.
     */
    private ThreadLocal<AndroidDriver<WebElement>> webDriver = new ThreadLocal<AndroidDriver<WebElement>>();

    /**
     * ThreadLocal variable which contains the Sauce Job Id.
     */
    private ThreadLocal<String> sessionId = new ThreadLocal<String>();

    /**
     * DataProvider that explicitly sets the browser combinations to be used.
     *
     * @param testMethod
     * @return
     */
    @DataProvider(name = "hardCodedBrowsers", parallel = true)
    public static Object[][] sauceBrowserDataProvider(Method testMethod) {
        return new Object[][]{
        		// Emulators
                new Object[]{"Android", "Samsung Galaxy S4 Emulator", "4.4", "Browser", "portrait"},
                new Object[]{"Android", "Google Nexus 7 HD Emulator", "4.4", "Browser", "portrait"},
                new Object[]{"Android", "Android Emulator", "5.1", "Browser", "portrait"},
                // Real Devices  - Make sure your Sauce Labs account has access to Real Devices before executing
                new Object[]{"Android", "Samsung Galaxy S4 Device", "4.4", "Chrome", "portrait"},
                new Object[]{"Android", "Samsung Galaxy S5 Device", "5.0", "Chrome", "portrait"},
                new Object[]{"Android", "Samsung Galaxy S6 Device", "6.0", "Chrome", "portrait"},
                new Object[]{"Android", "Samsung Galaxy S7 Device", "6.0", "Chrome", "portrait"},
        };
    }

    /**
     * /**
     * Constructs a new {@link RemoteWebDriver} instance which is configured to use the capabilities defined by the platformName,
     * deviceName, platformVersion, and app and which is configured to run against ondemand.saucelabs.com, using
     * the username and access key populated by the {@link #authentication} instance.
     *
     * @param platformName Represents the platform to be run.
     * @param deviceName Represents the device to be tested on
     * @param platform Version Represents version of the platform.
     * @param app Represents the location of the app under test.
     * @return
     * @throws MalformedURLException if an error occurs parsing the url
     */
    private AndroidDriver<WebElement> createDriver(String platformName, String deviceName, String platformVersion, String browserName, String deviceOrientation, String methodName) throws MalformedURLException {

        DesiredCapabilities capabilities = new DesiredCapabilities();

        capabilities.setCapability("platformName", platformName);
        capabilities.setCapability("deviceName", deviceName);
        capabilities.setCapability("platformVersion", platformVersion);
        capabilities.setCapability("browserName", browserName);
        capabilities.setCapability("deviceOrientation", deviceOrientation);
        capabilities.setCapability("deviceType", "phone");
        capabilities.setCapability("appiumVersion", "1.5.3");

        String jobName = methodName + '_' + deviceName + '_' + platformName + '_' + platformVersion;
        capabilities.setCapability("name", jobName);

        webDriver.set(new AndroidDriver<WebElement>(
                new URL("https://" + authentication.getUsername() + ":" + authentication.getAccessKey() + "@ondemand.saucelabs.com:443/wd/hub"),
                capabilities));
        String id = ((RemoteWebDriver) getWebDriver()).getSessionId().toString();
        sessionId.set(id);
        return webDriver.get();
    }

    /**
     * Runs a simple test that clicks the add contact button.
     *
     * @param platformName Represents the platform to be run.
     * @param deviceName Represents the device to be tested on
     * @param platform Version Represents version of the platform.
     * @param app Represents the location of the app under test.
     * @throws Exception if an error occurs during the running of the test
     */
    @Test(dataProvider = "hardCodedBrowsers")
    public void clickLinkTest(String platformName, String deviceName, String platformVersion, String browserName, String deviceOrientation, Method method) throws Exception {
    	AndroidDriver<WebElement> driver = createDriver(platformName, deviceName, platformVersion, browserName, deviceOrientation, method.getName());

        driver.get("https://saucelabs.com/test/guinea-pig");
        driver.findElementById("i am a link").click();
        String title = driver.getTitle();
        assertEquals(title, "I am another page title - Sauce Labs");
        driver.quit();
    }

    @Test(dataProvider = "hardCodedBrowsers")
    public void clickLinkTest2(String platformName, String deviceName, String platformVersion, String browserName, String deviceOrientation, Method method) throws Exception {
    	AndroidDriver<WebElement> driver = createDriver(platformName, deviceName, platformVersion, browserName, deviceOrientation, method.getName());

        driver.get("https://saucelabs.com/test/guinea-pig");
        driver.findElementById("i am a link").click();
        String title = driver.getTitle();
        assertEquals(title, "I am another page title - Sauce Labs");
        driver.quit();
    }


    /**
     * @return the {@link WebDriver} for the current thread
     */
    public AndroidDriver<WebElement> getWebDriver() {
        return webDriver.get();
    }

    /**
     *
     * @return the Sauce Job id for the current thread
     */
    public String getSessionId() {
        return sessionId.get();
    }

    /**
     *
     * @return the {@link SauceOnDemandAuthentication} instance containing the Sauce username/access key
     */
    @Override
    public SauceOnDemandAuthentication getAuthentication() {
        return authentication;
    }
}
