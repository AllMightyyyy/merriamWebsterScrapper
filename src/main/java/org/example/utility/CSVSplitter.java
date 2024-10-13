package org.example.utility;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVSplitter {

    public static List<String> splitCSV(String inputFilePath, int numberOfThreads) throws IOException {
        List<String> splitFiles = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new FileReader(inputFilePath))) {
            List<String[]> allRows = csvReader.readAll();
            int totalRows = allRows.size();
            int rowsPerFile = totalRows / numberOfThreads;

            for (int i = 0; i < numberOfThreads; i++) {
                int start = i * rowsPerFile;
                int end = (i == numberOfThreads - 1) ? totalRows : start + rowsPerFile;
                List<String[]> chunk = allRows.subList(start, end);

                String splitFileName = "split_file_" + (i + 1) + ".csv";
                try (CSVWriter writer = new CSVWriter(new FileWriter(splitFileName))) {
                    writer.writeAll(chunk);
                }
                splitFiles.add(splitFileName);
            }
        } catch (CsvException e) {
            throw new RuntimeException(e);
        }
        return splitFiles;
    }
}
