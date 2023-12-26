package lod.irongoon.parse.external;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class CSVParser implements DataParser {
    private static final CSVParser INSTANCE = new CSVParser();

    public static CSVParser getInstance() {
        return INSTANCE;
    }

    private CSVParser() {}

    public List<String[]> load(String filePath) {
        try (FileReader fileReader = new FileReader(filePath);
             CSVReader csv = new CSVReader(fileReader)) {
            List<String[]> list = csv.readAll();
            return list;
        } catch (IOException | CsvException exception) {
            throw new RuntimeException(exception);
        }
    }
}
