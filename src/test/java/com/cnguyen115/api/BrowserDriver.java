package com.cnguyen115.api;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;

public class BrowserDriver {

    public static WebDriver driver;
    private static final Logger log = LoggerFactory.getLogger(BrowserDriver.class);

    private @Value("${browser}") String browser;
    private @Value("${location}") String location;
    private @Value("${chromepath}") String chromePath;

    @Before
    public void init() throws MalformedURLException {
        log.info("Launching " + browser + "...");

        DesiredCapabilities capabilities = null;


        if (browser.toLowerCase().equals("firefox")) {
            capabilities = capabilitiesFirefox(capabilities);
        } else if (browser.toLowerCase().equals("chrome")) {
            capabilities = capabilitiesChrome(capabilities);
        } else if (browser.toLowerCase().equals("iexplore")) {
            capabilities = capabilitiesExplorer(capabilities);
        } else if (browser.toLowerCase().equals("android")) {
            capabilities = capabilitiesAndroid(capabilities);
        } else if (browser.toLowerCase().equals("iphone")) {
            capabilities = capabilitiesiPhone(capabilities);
        } else if (browser.toLowerCase().equals("ipad")) {
            capabilities = capabilitiesiPad(capabilities);
        }

        if (!location.toLowerCase().contains("local")) {
            log.info("Running on Selenium Grid: " + location);
            driver = new RemoteWebDriver(new URL(location), capabilities);
        } else if (browser.toLowerCase().equals("firefox")) {
            driver = new FirefoxDriver(capabilities);
        } else if (browser.toLowerCase().equals("chrome")) {
            driver = new ChromeDriver(capabilities);
        } else if (browser.toLowerCase().equals("iexplore")) {
            driver = new InternetExplorerDriver(capabilities);
        } else if (browser.toLowerCase().equals("android")) {
            driver = new ChromeDriver(capabilities);
        } else if (browser.toLowerCase().equals("iphone")) {
            driver = new ChromeDriver(capabilities);
        } else if (browser.toLowerCase().equals("ipad")) {
            driver = new ChromeDriver(capabilities);
        }
    }

    public DesiredCapabilities capabilitiesAndroid(DesiredCapabilities capabilities) {
        capabilities = DesiredCapabilities.chrome();

        Map<String, String> mobileEmulation = new HashMap<String, String>();
        mobileEmulation.put("deviceName", "Google Nexus 5");

        Map<String, Object> chromeOptions = new HashMap<String, Object>();
        chromeOptions.put("mobileEmulation", mobileEmulation);
        capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);

        return capabilities;
    }

    public DesiredCapabilities capabilitiesiPhone(DesiredCapabilities capabilities) {
        capabilities = DesiredCapabilities.chrome();

        Map<String, String> mobileEmulation = new HashMap<String, String>();
        mobileEmulation.put("deviceName", "Apple iPhone 6");

        Map<String, Object> chromeOptions = new HashMap<String, Object>();
        chromeOptions.put("mobileEmulation", mobileEmulation);
        capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);

        return capabilities;
    }

    public DesiredCapabilities capabilitiesiPad(DesiredCapabilities capabilities) {
        capabilities = DesiredCapabilities.chrome();

        Map<String, String> mobileEmulation = new HashMap<String, String>();
        mobileEmulation.put("deviceName", "Apple iPad");

        Map<String, Object> chromeOptions = new HashMap<String, Object>();
        chromeOptions.put("mobileEmulation", mobileEmulation);
        capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);

        return capabilities;
    }

    public DesiredCapabilities capabilitiesFirefox(DesiredCapabilities capabilities) {
        capabilities = DesiredCapabilities.firefox();

        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("network.http.phishy-userpass-length", 255);
        profile.setEnableNativeEvents(true);
        profile.setAcceptUntrustedCertificates(true);

        capabilities.setCapability(FirefoxDriver.PROFILE, profile);
        capabilities.setBrowserName(DesiredCapabilities.firefox().getBrowserName());
        return capabilities;
    }

    public DesiredCapabilities capabilitiesChrome(DesiredCapabilities capabilities) {
        String downloadFilepath = System.getProperty("user.dir") + System.getProperty("file.separator") + "target" + System.getProperty("file.separator");

        HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
        chromePrefs.put("profile.default_content_settings.popups", 0);
        chromePrefs.put("download.default_directory", downloadFilepath);
        chromePrefs.put("enableNetwork", "true");

        ChromeOptions option = new ChromeOptions();
        option.addArguments("test-type");
        option.addArguments("--start-maximized");
        option.setExperimentalOption("prefs", chromePrefs);
        option.addArguments("--browser.download.folderList=2");
        option.addArguments(
                "--browser.helperApps.neverAsk.saveToDisk=image/jpg,text/csv,text/xml,application/xml,application/vnd.ms-excel,application/x-excel,application/x-msexcel,application/excel,application/pdf");
        option.addArguments("--browser.download.dir=" + downloadFilepath);
        option.addArguments("allow-running-insecure-content");

        System.setProperty("webdriver.chrome.driver", chromePath);

        capabilities = DesiredCapabilities.chrome();
        capabilities.setBrowserName(DesiredCapabilities.chrome().getBrowserName());
        capabilities.setCapability(ChromeOptions.CAPABILITY, option);
        capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);

        return capabilities;
    }

    public DesiredCapabilities capabilitiesExplorer(DesiredCapabilities capabilities) {
        capabilities = DesiredCapabilities.internetExplorer();
        capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
        capabilities.setCapability("ignoreZoomSetting", true);
        capabilities.setCapability("ignoreProtectedModeSettings", true);
        capabilities.setBrowserName(DesiredCapabilities.internetExplorer().getBrowserName());
        return capabilities;
    }


    @After
    public void tearDown(Scenario scenario) {
        if (scenario.isFailed()) {
            // Take a screenshot...
            final byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            scenario.embed(screenshot, "image/png"); // ... and embed it in the report.
        }
        if (driver != null) {
            driver.quit();
        }
    }

    /*
     * Waits for the actual element to appear on the web page
     */
    public static void waitForElementVisible(By locator) {
        log.info("Waiting for element visible: " + locator);
        WebDriverWait wait = new WebDriverWait(BrowserDriver.driver, 60);
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /*
     * Waits for the actual element to appear on the web page
     */
    public static void waitForElementVisible(By locator, long timeout) {
        log.info("Waiting for element visible for " + timeout + " seconds: " + locator);
        WebDriverWait wait = new WebDriverWait(BrowserDriver.driver, timeout);
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /*
     * Waits for the element to load from the html
     */
    public static void waitForElementPresent(By locator) {
        log.info("Waiting for element visible: " + locator);
        WebDriverWait wait = new WebDriverWait(BrowserDriver.driver, 60);
        wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /*
     * Waits for the element to load from the html
     */
    public static void waitForElementPresent(By locator, long timeout) {
        log.info("Waiting for element visible for " + timeout + " seconds: " + locator);
        WebDriverWait wait = new WebDriverWait(BrowserDriver.driver, timeout);
        wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /*
     * Uses JS to detect if the page is fully loaded
     */
    public static void waitForPageLoad() {
        String state = "";
        int counter = 0;

        do {
            try {
                state = (String) ((JavascriptExecutor) driver).executeScript("return document.readyState");
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            counter++;
            log.info(("Browser state is: " + state));
        } while (!state.equalsIgnoreCase("complete") && counter < 20);

    }

    /*
     * Returns true if an attribute exists for the element specified
     */
    public static boolean isAttributePresent(By locator, String attribute) {
        return driver.findElement(locator).getAttribute(attribute) != null;
    }

    /*
     * Method to select a dropdown option by index
     */
    public static void selectDropdownByIndex(By locator, int index) {
        try {
            Select select = new Select(driver.findElement(locator));
            select.selectByIndex(index);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
