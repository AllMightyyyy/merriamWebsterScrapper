package org.example.scraper;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileReader;
import java.io.FileWriter;
import java.time.Duration;
import java.util.List;


public class DefinitionScraperTask implements Runnable {
    private final String inputCsv;
    private final String outputCsv;
    private final int totalWords;
    private static final String GECKO_DRIVER_PATH = "C:\\Program Files\\Mozilla Firefox\\geckoDriver\\geckodriver.exe";
    private static final String FIREFOX_BINARY_PATH = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";


    public DefinitionScraperTask(String inputCsv, String outputCsv, int totalWords) {
        this.inputCsv = inputCsv;
        this.outputCsv = outputCsv;
        this.totalWords = totalWords;
    }

    @Override
    public void run() {
        WebDriver driver = initializeWebDriver();

        try (CSVReader csvReader = new CSVReader(new FileReader(inputCsv));
             CSVWriter csvWriter = new CSVWriter(new FileWriter(outputCsv))) {

            String[] row;
            int localProgress = 0;
            while ((row = csvReader.readNext()) != null) {
                String word = row[1];
                String definition = getDefinition(driver, word);
                csvWriter.writeNext(new String[]{word, definition});

                localProgress = MultiThreadedScraper.progressCounter.incrementAndGet();
                printProgress(localProgress, totalWords);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }

    private WebDriver initializeWebDriver() {
        System.setProperty("webdriver.gecko.driver",GECKO_DRIVER_PATH );
        FirefoxOptions options = new FirefoxOptions();
        options.setBinary(FIREFOX_BINARY_PATH);
        options.setHeadless(true);
        return new FirefoxDriver(options);
    }

    private String getDefinition(WebDriver driver, String word) {
        String url = "https://www.merriam-webster.com/dictionary/" + word;
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
            return "Definition not found";
        }
    }

    private void printProgress(int processedWords, int totalWords) {
        double progressPercentage = (double) processedWords / totalWords * 100;
        System.out.printf("Progress: %.2f%% (%d/%d)\n", progressPercentage, processedWords, totalWords);
    }
}
