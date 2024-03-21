package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;


/**
 * Represents a page object for displaying search results.
 */
public class ResultsPage {
    /**
     * The WebDriver instance used to interact with the web elements.
     */
    public WebDriver driver;
    /**
     * Constructs a new ResultsPage object with the given WebDriver instance.
     * @param driver the WebDriver instance to use
     */
    public ResultsPage(WebDriver driver)
    {
        this.driver = driver;
    }
    //locators
    /**
     * Locator for the returned search results on the page.
     */
    By returnedResultsBy = By.xpath("//div[@class='b_tpcn']/a");

    /**
     * Gets the number of results shown on the page.
     * @return the number of search results
     */
    @Step("Get the number of results shown on the page")
    public int getResultsCount()
    {
        List<WebElement> pageResults = driver.findElements(returnedResultsBy);
        System.out.println("the list of results size is " + pageResults.size() + ".");
        return pageResults.size();
    }

    /**
     * Navigates to the specified page number using pagination.
     * @param pageNumber the page number to navigate to
     */
    @Step("Navigate to the next Page")
    public void pageNavigator(String pageNumber)
    {
        By PaginationBarLocatorBy = By.xpath("//li[@class='b_pag']//nav//ul//a[@aria-label='Page "+pageNumber+"']");
        driver.findElement(PaginationBarLocatorBy).click();
    }

    /**
     * Scrolls to the end of the page using JavaScript.
     */
    @Step("Scroll to the end of the page")
    public void scrollToPageEnd()
    {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
    }
}
