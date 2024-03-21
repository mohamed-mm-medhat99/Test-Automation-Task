package base;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.apache.commons.io.FileUtils;
import org.asynchttpclient.uri.Uri;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import utils.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * TestBase class is the class which contain the methods which will be used
 * by all test cases classes to :
 * <ul>
 *     <li>Configure local and remote execution
 *     <li>Open The browser session before the test case execution
 *     <li>Close the browser session after the execution of the test case
 * </ul>
 * <p>
 * TestBase also responsible for:
 * <ul>
 *     <li>configure the drivers properties based on the selected driver
 *     <li>Create drivers instances
 *     <li>Create property file reader
 *     <li>Take screenshot in case of failure
 * </ul>
 */
public class TestBase {
    public static WebDriver driver;
    public static Properties property;
    public static String configPath = Constants.configPath;

    static {
        property = initializePropertyFile();

    }

    /**
     *load data from properties file to read data from
     * @return property
     */
    public static Properties initializePropertyFile()
    {
        property = new Properties();
        try {
            InputStream stream = new FileInputStream(configPath);
            property.load(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return property;
    }

    /**
     * Set up the local execution configurations such as:
     * <ul>
     *     <li>driver type.
     *          <ul>
     *              <li>Chrome Driver
     *              <li>FireFix Driver
     *              <li>Microsoft Edge
     *          </ul>
     *      <li>Driver Options.
     * </ul>
     */
    public static void localExecutionSetup(){
        if(initializePropertyFile().getProperty("BrowserType").equalsIgnoreCase("FireFox")) {
            WebDriverManager.firefoxdriver().setup();
            driver = new FirefoxDriver();

        }else if(initializePropertyFile().getProperty("BrowserType").equalsIgnoreCase("Chrome")) {
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--remote-allow-origins=*");
            driver = new ChromeDriver(options);

        }else if(initializePropertyFile().getProperty("BrowserType").equalsIgnoreCase("MicrosoftEdge")) {
            WebDriverManager.edgedriver().setup();
            EdgeOptions options = new EdgeOptions();
            options.addArguments("--remote-allow-origins=*");
            driver = new EdgeDriver(options);
        }
    }

    /**
     * Set up the remote execution on one of the nodes created the hub such as:
     * <ul>
     *     <li>configure the hub URL
     *     <li>configure the driver type
     *     <li>configure the platform used to execute the scenarios
     * </ul>
     */
    public static void remoteExecutionSetup() {
        String url = initializePropertyFile().getProperty("hubURL");
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setBrowserName(initializePropertyFile().getProperty("BrowserType"));
        capabilities.setPlatform(Platform.WIN10);
        try {
            driver = new RemoteWebDriver(new URI(url).toURL(), capabilities);
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *responsible to open the driver and manage the following:
     * <ul>
     *     <li>Set the execution type
     *     <li>Maximize the window.
     *     <li>Navigate to the target URL.
     * </ul>
     */
    @Step("Open the browser and navigate to target URL")
    public static void openBrowser()
    {
        String runType = initializePropertyFile().getProperty("RunType");

        switch (runType){
            case "remote":
                System.out.println("Start execution in remote mode");
                remoteExecutionSetup();
                break;

            case "local":
                System.out.println("Start execution in local mode");
                localExecutionSetup();
                break;

        }
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        System.out.println(initializePropertyFile().getProperty("TargetURL"));
        driver.get(initializePropertyFile().getProperty("TargetURL"));
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        }


    /**
     * Take a screenshot in case of failure
     * @param driver
     * @return screenshot
     */
    public static File takeScreenShot(WebDriver driver)
        {
            TakesScreenshot captureIMG = (TakesScreenshot) driver;
            File src = captureIMG.getScreenshotAs(OutputType.FILE);
            return src;
        }

    /**
     * Responsible to:
     * <ul>
     *     <li>Take screenshot in case of failure.
     *     <li>Close the browser session.
     * </ul>
     * @param testResult
     * @throws IOException
     */
    @Step("Take screenshot in case of failure and close the browser session")
        @AfterMethod(alwaysRun = true)
            public static void updateTestStatus(ITestResult testResult) throws IOException {
            if(testResult.getStatus()==ITestResult.FAILURE){
                File screenShot = takeScreenShot(driver);
                Allure.addAttachment("Page ScreenShot" , FileUtils.openInputStream(screenShot));
            }
            driver.quit();
        }

    /**
     * Create an implicit sleep time
     * @param sleepAmount
     */
    public void sleep(int sleepAmount)
        {
            System.out.println("Start Sleep Mode for :"+sleepAmount+".");
            try {
                Thread.sleep(sleepAmount);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
