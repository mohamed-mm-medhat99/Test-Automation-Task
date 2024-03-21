package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Represents a page object for search functionality.
 */
public class SearchPage {
    /**
     * The WebDriver instance used to interact with the web elements.
     */
    public WebDriver driver;

    /**
     * Constructs a new SearchPage object with the given WebDriver instance.
     * @param driver the WebDriver instance to use
     */
    public SearchPage(WebDriver driver)
    {
        this.driver = driver;
    }
    // locators

    /**
     * Locates the search text field on the page.
     */
    By searchTextFieldBy = By.xpath("//input[@id='sb_form_q']");

    /**
     * Locates the search button on the page.
     */
    By searchButtonBy = By.xpath("//*[@id='search_icon']");
    //Methods

    /**
     * Sends a keyword to the search field.
     * @param keyWord the keyword to search for
     */
    @Step("Send key word to search field")
    public void sendDataToSearchField(String keyWord){
        driver.findElement(searchTextFieldBy).sendKeys(keyWord);
    }

    /**
     * Clicks the search button on the page.
     */
    @Step("Click on search button")
    public void clickSearchButton()
    {
        driver.findElement(searchButtonBy).click();
    }
}
