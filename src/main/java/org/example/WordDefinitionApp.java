package org.example;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class WordDefinitionApp extends JFrame {
    private JComboBox<String> wordComboBox;
    private JTextArea definitionTextArea;
    private Map<String, String> wordMap;

    public WordDefinitionApp() {
        wordMap = loadWordsFromCsv("final_output.csv");

        setTitle("Word Definition Finder");
        setLayout(new BorderLayout());

        JTextField wordField = new JTextField(20);
        wordComboBox = new JComboBox<>();
        wordComboBox.setEditable(true);
        wordComboBox.setPrototypeDisplayValue("Enter a word...");

        wordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String input = wordField.getText().toLowerCase();
                updateComboBox(input);
            }
        });

        wordComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedWord = (String) wordComboBox.getSelectedItem();
                showDefinition(selectedWord);
            }
        });

        definitionTextArea = new JTextArea(10, 30);
        definitionTextArea.setWrapStyleWord(true);
        definitionTextArea.setLineWrap(true);
        definitionTextArea.setEditable(false);

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Enter Word:"));
        inputPanel.add(wordField);
        inputPanel.add(wordComboBox);

        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(definitionTextArea), BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 300);
        setVisible(true);
    }

    private Map<String, String> loadWordsFromCsv(String filePath) {
        Map<String, String> wordMap = new HashMap<>();
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            List<String[]> lines = reader.readAll();
            for (String[] line : lines) {
                if (line.length >= 2) {
                    wordMap.put(line[0].toLowerCase(), line[1].trim());
                }
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
        return wordMap;
    }

    private void updateComboBox(String input) {
        wordComboBox.removeAllItems();
        if (!input.isEmpty()) {
            for (String word : wordMap.keySet()) {
                if (word.startsWith(input)) {
                    wordComboBox.addItem(word);
                }
            }
        }
        wordComboBox.setPopupVisible(wordComboBox.getItemCount() > 0);
    }

    private void showDefinition(String word) {
        if (word != null && wordMap.containsKey(word.toLowerCase())) {
            definitionTextArea.setText(wordMap.get(word.toLowerCase()));
        } else {
            definitionTextArea.setText("Definition not found.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(WordDefinitionApp::new);
    }
}
