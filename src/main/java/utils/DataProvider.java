package utils;
import io.qameta.allure.internal.shadowed.jackson.databind.exc.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Provides methods for fetching test data from an Excel sheet and executing test steps based on provided data.
 */
public class DataProvider {
    /**
     * The WebDriver instance used for test execution.
     */
    public WebDriver driver;
    /**
     * Represents the Excel sheet used for test data.
     */
    private static XSSFSheet ExcelWSheet;
    /**
     * Represents the Excel workbook containing test data.
     */
    private static XSSFWorkbook ExcelWBook;
    /**
     * Represents the workbook object for data access.
     */
    public static Workbook book;
    /**
     * Represents the sheet object for data access.
     */
    public static Sheet sheet;
    /**
     * The name of the locator used in the test.
     */
    String locatorName = null;
    /**
     * The value of the locator used in the test.
     */
    String locatorValue = null;
    /**
     * Constructs a new DataProvider object with the given WebDriver instance.
     * @param driver the WebDriver instance to use
     */
    public DataProvider(WebDriver driver)
    {
        this.driver = driver;
    }
    /**
     * Fetches test data from the specified Excel sheet based on the test case name.
     * @param path the path of the Excel file
     * @param testCaseName the name of the test case
     * @return a 2D array of test data
     * @throws InvalidFormatException if the Excel format is invalid
     * @throws IOException if an I/O error occurs
     */
    public  static Object[][] fetchData(String path,String testCaseName) throws InvalidFormatException, IOException {

        Row row = null;
        int cellCount = 0;
        Object data[][] = null;
        // Open the Excel file

        FileInputStream ExcelFile = new FileInputStream(path);

        // Access the required test data sheet

        ExcelWBook = new XSSFWorkbook(ExcelFile);
        ExcelWSheet = ExcelWBook.getSheet("TestCases");

        int rowcount = ExcelWSheet.getLastRowNum();
        ArrayList<Integer> tcRowsList = new ArrayList<Integer>();

        for (int i = 1; i <= rowcount; i++) {
            row = ExcelWSheet.getRow(i);
            if (rowIsEmpty(row))
                break;
            if (row.getCell(0).getStringCellValue().equals(testCaseName)) {
                // header row
                if (row.getCell(1) == null)
                    continue;
                // Run mode is false
                if (!(row.getCell(1).getBooleanCellValue()))
                    continue;
                cellCount = row.getLastCellNum();
                tcRowsList.add(i);
            }
        }
        if (tcRowsList.size() > 0) {
            data = new Object[tcRowsList.size()][cellCount - 2];
            for (int i = 0; i < tcRowsList.size(); i++) {

                Row r = ExcelWSheet.getRow(tcRowsList.get(i));

                for (int j = 2; j < cellCount; j++) {
                    Cell c = r.getCell(j);
                    try {

                        if (c.getCellType() == CellType.STRING) {
                            data[i][j - 2] = c.getStringCellValue();
                        } else if (c.getCellType()== CellType.NUMERIC || c.getCellType() == CellType.FORMULA) {
                            // Date value
                            if (DateUtil.isCellDateFormatted(c)) {
                                DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                                Date date = c.getDateCellValue();
                                data[i][j - 2] = df.format(date);
                            }
                            // Numeric value
                            else {
                                String cellValue = String.valueOf(c.getNumericCellValue());
                                if (cellValue.contains(".0")) {
                                    data[i][j - 2] = cellValue.split("\\.")[0];
                                }
                            }
                        }
                        // boolean value
                        else if (c.getCellType() == CellType.BOOLEAN) {
                            data[i][j - 2] = c.getBooleanCellValue();
                        }

                    } catch (Exception e) {
                        //ReportManager.log(e.getMessage());
                    }
                }
            }
        }
        return data ;



    }
    /**
     * Checks if a row in Excel is empty.
     * @param row the Excel row to check
     * @return true if the row is empty, false otherwise
     */
    private static boolean rowIsEmpty(Row row) {
        if (row == null) {
            return true;
        }

        for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
            Cell cell = row.getCell(cellNum);

            if (cell != null && cell.getCellType()!= CellType.BLANK )
            {
                return false;
            }

        }
        return true;
    }
    /**
     * Starts test execution based on provided test steps and data from an Excel sheet.
     * @param sheetName the name of the Excel sheet containing test steps
     */
    public void startExecution(String sheetName) {
        FileInputStream file = null;
        try {
            file = new FileInputStream(Constants.filePath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            book = WorkbookFactory.create(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        sheet = book.getSheet(sheetName);
        int k = 0;
        for (int i = 0; i < sheet.getLastRowNum(); i++) {
            try{
            String locatorColValue = sheet.getRow(i + 1).getCell(k + 1).toString().trim();
            if (!locatorColValue.equalsIgnoreCase("NA")) {
                locatorName = locatorColValue.split("=")[0].trim();//xpath
                locatorValue = locatorColValue;//value of xpath
            }
            String action = sheet.getRow(i + 1).getCell(k + 2).toString().trim();
            String value = sheet.getRow(i + 1).getCell(k + 3).toString().trim();

            switch (action){
                case "send data":
                    driver.findElement(By.xpath(locatorValue)).sendKeys(value);
                    break;

                case "click":
                    driver.findElement(By.xpath(locatorValue)).click();
                    break;

                case "scroll":
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
                    break;

                case "switch":
                    driver.findElement(By.xpath(locatorValue)).click();
                    break;

                case "get results":
                    List<WebElement> pageResults = driver.findElements(By.xpath(locatorValue));
                    System.out.println("the list of results size is " + pageResults.size() + ".");
                    break;

                case "sleep":
                    int sleepTime = Integer.parseInt(value);
                    Thread.sleep(sleepTime);
                    System.out.println("start sleep for: "+value+".");
                    break;
            }

        } catch (Exception e) {
            }
    }
    }

}
