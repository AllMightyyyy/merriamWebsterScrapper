package org.example.scraper;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

public class MerriamWebsterDefinitionScraper {

    static WebDriver driver;
    private static final String GECKO_DRIVER_PATH = "C:\\Program Files\\Mozilla Firefox\\geckoDriver\\geckodriver.exe";
    private static final String FIREFOX_BINARY_PATH = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
    static final String BASE_URL = "https://www.merriam-webster.com/dictionary/";

    public static void main(String[] args) {
        System.setProperty("webdriver.gecko.driver", GECKO_DRIVER_PATH);

        FirefoxOptions options = new FirefoxOptions();
        options.setHeadless(true);
        options.setBinary(FIREFOX_BINARY_PATH);
        options.addArguments("--start-maximized");

        driver = new FirefoxDriver(options);

        String inputCsv = "merriam_words.csv";
        String outputCsv = "merriam_words_with_definitions.csv";

        try (CSVReader csvReader = new CSVReader(new FileReader(inputCsv));
             CSVWriter csvWriter = new CSVWriter(new FileWriter(outputCsv))) {

            String[] headers = csvReader.readNext();
            csvWriter.writeNext(new String[]{headers[0], "Definition"});

            String[] row;
            while ((row = csvReader.readNext()) != null) {
                String word = row[1];
                String definition = getDefinition(word);
                System.out.println("Word: " + word + ", Definition: " + definition);

                csvWriter.writeNext(new String[]{word, definition});
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        } finally {
            driver.quit();
        }
    }

    public static String getDefinition(String word) {
        String url = BASE_URL + word;
        driver.get(url);

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement definitionElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.vg")));

            List<WebElement> definitionParts = definitionElement.findElements(By.cssSelector("span.dtText"));
            StringBuilder definitionBuilder = new StringBuilder();

            for (WebElement part : definitionParts) {
                definitionBuilder.append(part.getText()).append(" ");
            }

            return definitionBuilder.toString().trim();

        } catch (Exception e) {
            System.out.println("Definition not found for word: " + word);
            return "Definition not found";
        }
    }
}