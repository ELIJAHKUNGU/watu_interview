package com.example.scheduledactions.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Component
class CsvReader {
    private static final Path CSV_FILE_PATH = Path.of("src/main/resources/actions.csv");
    private static final Logger log = LoggerFactory.getLogger(CsvReader.class);

    public List<String[]> readCsv() {
        try {
            return Files.lines(CSV_FILE_PATH)
                    .map(line -> line.split("#")[0].trim())  // Remove comments
                    .filter(line -> !line.isBlank())        // Ignore empty lines
                    .map(line -> line.split(","))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
            log.error("Error reading CSV file: " + e.getMessage());
            return List.of();
        }
    }
}