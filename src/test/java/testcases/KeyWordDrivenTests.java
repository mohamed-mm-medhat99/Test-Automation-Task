package testcases;

import base.TestBase;
import io.qameta.allure.Step;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import utils.DataProvider;

/**
 * Represents test cases driven by keywords for validating search results on search pages.
 */
public class KeyWordDrivenTests extends TestBase {
/**
 * The data provider for fetching test data and executing test steps.
 */
public DataProvider dataProvider;
    /**
     * Initializes the data provider and opens the browser before each test method.
     */
    @BeforeMethod
    public void beforeMethod(){
        openBrowser();
        dataProvider = new DataProvider(driver);
    }

    /**
     * Executes the keyword-driven test to validate search results on search pages.
     */
    @Step("start executing KewWord_Test_ValidateResultsOnSearchPages ")
    @Test
    public void Test_ValidateResultsOnSearchPages()
    {
        dataProvider.startExecution("TC_1");
    }

}

