package org.example.scraper;

import com.opencsv.CSVWriter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class MerriamWebsterWordScraper {

    static WebDriver driver;
    static String baseUrl = "https://www.merriam-webster.com/wordfinder/classic/begins/common/-1/";
    private static final String GECKO_DRIVER_PATH = "C:\\Program Files\\Mozilla Firefox\\geckoDriver\\geckodriver.exe";
    private static final String FIREFOX_BINARY_PATH = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
    static CSVWriter csvWriter;

    public static void main(String[] args) {
        System.setProperty("webdriver.gecko.driver", GECKO_DRIVER_PATH);

        FirefoxOptions options = new FirefoxOptions();
        options.setBinary(FIREFOX_BINARY_PATH);
        options.addArguments("--start-maximized");

        driver = new FirefoxDriver(options);

        try (FileWriter fileWriter = new FileWriter("merriam_words.csv");
             CSVWriter csvWriter = new CSVWriter(fileWriter)) {
            MerriamWebsterWordScraper.csvWriter = csvWriter;
            csvWriter.writeNext(new String[]{"Letter", "Word"});

            for (char letter = 'a'; letter <= 'z'; letter++) {
                scrapeWordsForLetter(letter);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }

    public static void scrapeWordsForLetter(char letter) {
        int page = 1;
        boolean hasNextPage = true;

        while (hasNextPage) {
            String url = baseUrl + letter + "/" + page;
            driver.get(url);

            try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                WebElement wordListContainer = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("ul.paginated-list-results")));

                List<WebElement> wordElements = wordListContainer.findElements(By.tagName("li"));
                List<String[]> words = new ArrayList<>();

                for (WebElement wordElement : wordElements) {
                    WebElement link = wordElement.findElement(By.tagName("a"));
                    String word = link.getText();
                    words.add(new String[]{String.valueOf(letter), word});
                }

                csvWriter.writeAll(words);

                hasNextPage = checkForNextPage();
                page++;
            } catch (Exception e) {
                System.out.println("Failed to scrape words for letter: " + letter + " page: " + page);
                e.printStackTrace();
                hasNextPage = false;
            }
        }
    }

    public static boolean checkForNextPage() {
        try {
            WebElement nextPageButton = driver.findElement(By.cssSelector("a[aria-label='Next']"));
            return nextPageButton != null;
        } catch (Exception e) {
            return false;
        }
    }
}
