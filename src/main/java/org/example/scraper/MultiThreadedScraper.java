package org.example.scraper;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.example.utility.CSVSplitter;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiThreadedScraper {
    static final AtomicInteger progressCounter = new AtomicInteger(0);
    private static final String GECKO_DRIVER_PATH = "C:\\Program Files\\Mozilla Firefox\\geckoDriver\\geckodriver.exe";
    private static final String FIREFOX_BINARY_PATH = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";

    public static void main(String[] args) {
        int numberOfThreads = 6;
        String inputCsv = "merriam_words.csv";

        try {
            List<String> splitFiles = CSVSplitter.splitCSV(inputCsv, numberOfThreads);

            int totalWords = getTotalWordCount(splitFiles);

            ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

            for (int i = 0; i < numberOfThreads; i++) {
                String splitFile = splitFiles.get(i);
                Runnable worker = new DefinitionScraperTask(splitFile, "output_" + (i + 1) + ".csv", totalWords);
                executor.execute(worker);
            }

            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.HOURS);

            CSVMerger.mergeCSVFiles("output_", numberOfThreads, "final_output.csv");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int getTotalWordCount(List<String> splitFiles) throws IOException {
        int totalWords = 0;
        for (String file : splitFiles) {
            try (CSVReader reader = new CSVReader(new FileReader(file))) {
                totalWords += reader.readAll().size() - 1;
            } catch (CsvException e) {
                throw new RuntimeException(e);
            }
        }
        return totalWords;
    }
}
