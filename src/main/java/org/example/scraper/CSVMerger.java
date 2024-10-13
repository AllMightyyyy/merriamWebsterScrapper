package org.example.scraper;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVMerger {

    public static void mergeCSVFiles(String outputPrefix, int numberOfFiles, String finalOutput) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(finalOutput))) {
            for (int i = 1; i <= numberOfFiles; i++) {
                try (CSVReader reader = new CSVReader(new FileReader(outputPrefix + i + ".csv"))) {
                    List<String[]> allRows = reader.readAll();
                    if (i == 1) {
                        writer.writeAll(allRows);
                    } else {
                        writer.writeAll(allRows.subList(1, allRows.size()));
                    }
                } catch (CsvException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        mergeCSVFiles("output_", 6, "final_output.csv");
    }
}
