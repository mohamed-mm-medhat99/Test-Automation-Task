package testcases;

import base.TestBase;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import io.qameta.allure.Step;
import org.testng.Assert;
import org.testng.annotations.*;
import pages.ResultsPage;
import pages.SearchPage;

import java.io.IOException;

/**
 * Represents test cases for validating search results on SearchPage and ResultsPage.
 */
public class SearchPageTests extends TestBase {
    /**
     * The SearchPage object for interacting with search functionality.
     */
    SearchPage searchPageObj ;
    /**
     * The ResultsPage object for interacting with search results.
     */
    ResultsPage resultPageObj ;
    /**
     * Initializes the SearchPage and ResultsPage objects and opens the browser before each test method.
     */
    @BeforeMethod
    public void beforeMethod()
    {
        openBrowser();
        searchPageObj = new SearchPage(driver);
        resultPageObj = new ResultsPage(driver);
    } //test changes
    /**
     * Provides test data for the Test_ValidateResultsOnSearchPages test method.
     * @return a 2D array of test data
     * @throws InvalidFormatException if the Excel format is invalid
     * @throws IOException if an I/O error occurs
     */
    @DataProvider(name = "PushSearchData")
    public Object [][] PushDataToAuth() throws InvalidFormatException, IOException {
        Object [][] testData = utils.DataProvider.fetchData(initializePropertyFile().getProperty("ExcelDataSheet") , "ValidateResultsOnSearchPages");
        return testData;
    }

    /**
     * Executes the test to validate search results on search pages.
     * @param searchKeyWord the keyword to search for
     * @param firstPage the number of the first page to navigate to
     * @param secondPage the number of the second page to navigate to
     */
    @Step("start executing Test_ValidateResultsOnSearchPages ")
    @Test(dataProvider = "PushSearchData")
    public void Test_ValidateResultsOnSearchPages(String searchKeyWord, String firstPage, String secondPage) {
        searchPageObj.sendDataToSearchField(searchKeyWord);
        searchPageObj.clickSearchButton();
        sleep(3000);
        resultPageObj.scrollToPageEnd();
        sleep(3000);
        resultPageObj.pageNavigator(firstPage);
        int numberOfSecondPageResult = resultPageObj.getResultsCount();
        resultPageObj.scrollToPageEnd();
        sleep(3000);
        resultPageObj.pageNavigator(secondPage);
        sleep(3000);
        int NumberOfThirdPageResults = resultPageObj.getResultsCount();
        sleep(3000);
        Assert.assertEquals(NumberOfThirdPageResults, numberOfSecondPageResult);
    }

}
