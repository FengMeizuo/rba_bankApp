package rba.task.bank.service;

import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import rba.task.bank.model.Person;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Predicate;

@Service
public class CsvService {

    private static final Logger log = LoggerFactory.getLogger(CsvService.class);

    public void markCsvAsInactive(String oib) throws IOException {
        markCsvFile(oib, "_inactive", name -> name.endsWith("_inactive"));
        log.info("Marked CSV as inactive for OIB: {}", oib);
    }

    public void markCsvAsActive(String oib) throws IOException {
        markCsvFile(oib, "", name -> !name.endsWith("_inactive") && !name.endsWith("_suspended"));
        log.info("Marked CSV as active for OIB: {}", oib);
    }

    public void markCsvAsSuspended(String oib) throws IOException {
        markCsvFile(oib, "_suspended", name -> name.endsWith("_inactive") || name.endsWith("_suspended"));
        log.info("Marked CSV as suspended for OIB: {}", oib);
    }

    private void markCsvFile(String oib, String suffix, Predicate<String> condition) throws IOException {
        Path workingDirectory = Paths.get("").toAbsolutePath();
        File[] listOfFiles = workingDirectory.toFile().listFiles();

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile() && file.getName().startsWith(oib)) {
                    if (condition.test(file.getName())) return;
                    Path originalPath = file.toPath();
                    String filename = originalPath.toString();
                    Path newPath;

                    if (suffix.equals("_inactive") && file.getName().endsWith("_suspended")) {
                        newPath = Paths.get(filename.substring(0, filename.lastIndexOf('_')) + suffix);
                    } else {
                        newPath = suffix.startsWith("_") ? Paths.get(filename + suffix)
                                : Paths.get(filename.substring(0, filename.lastIndexOf('_')));
                    }

                    try {
                        Files.move(originalPath, newPath);
                    } catch (Exception e) {
                        log.error("Failed to rename the file from {} to {}. OIB: {}", originalPath, newPath, oib, e);
                    }
                }
            }
        }
    }

    public void writePersonsToCsv(List<Person> persons) throws IOException {

        log.info("Writing persons to CSV files");
        for (Person person : persons) {
            String oib = person.getOib();
            if (!csvFileExistsForOib(oib)) {
                long timestamp = System.currentTimeMillis();
                String fileName = oib + "_" + timestamp;

                try (CSVWriter writer = new CSVWriter(new FileWriter(fileName))) {

                    String[] values = {
                            person.getFirstName(), person.getLastName(), person.getOib(), person.getStatus().toString()
                    };
                    writer.writeNext(values);
                }
            }
        }
    }

    public boolean csvFileExistsForOib(String oib) {
        log.debug("Checking if CSV file exists for OIB: {}", oib);
        Path workingDirectory = Paths.get("").toAbsolutePath();
        File[] listOfFiles = workingDirectory.toFile().listFiles();

        if (fileStartingWithOibExists(listOfFiles, oib)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean fileStartingWithOibExists(File[] listOfFiles, String oib) {

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile() && file.getName().startsWith(oib)) {
                    return true;
                }
            }
        }
        return false;
    }
}
