package com.cth.datamatch.comparison.service;

import com.opencsv.CSVReader;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

@Service
public class FileParserService {

    public List<Map<String, String>> parseFile(MultipartFile file) throws Exception {
        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new IllegalArgumentException("Invalid file name");
        }

        if (filename.endsWith(".csv")) {
            return parseCsv(file);
        } else if (filename.endsWith(".xlsx") || filename.endsWith(".xls")) {
            return parseExcel(file);
        } else {
            throw new IllegalArgumentException("Unsupported file type. Please upload CSV or Excel files.");
        }
    }

    private List<Map<String, String>> parseCsv(MultipartFile file) throws Exception {
        List<Map<String, String>> rows = new ArrayList<>();
        try (Reader reader = new InputStreamReader(file.getInputStream());
             CSVReader csvReader = new CSVReader(reader)) {

            String[] headers = csvReader.readNext();
            if (headers == null) return rows;

            // Trim headers
            for (int i = 0; i < headers.length; i++) {
                headers[i] = headers[i].trim();
            }

            String[] nextLine;
            while ((nextLine = csvReader.readNext()) != null) {
                Map<String, String> row = new LinkedHashMap<>();
                for (int i = 0; i < headers.length; i++) {
                    String val = (i < nextLine.length) ? nextLine[i].trim() : "";
                    row.put(headers[i], val);
                }
                rows.add(row);
            }
        }
        return rows;
    }

    private List<Map<String, String>> parseExcel(MultipartFile file) throws Exception {
        List<Map<String, String>> rows = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            if (!rowIterator.hasNext()) return rows;

            Row headerRow = rowIterator.next();
            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(getCellValueAsString(cell).trim());
            }

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Map<String, String> rowData = new LinkedHashMap<>();
                boolean isEmptyRow = true;

                for (int i = 0; i < headers.size(); i++) {
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    String val = getCellValueAsString(cell).trim();
                    if (!val.isEmpty()) isEmptyRow = false;
                    rowData.put(headers.get(i), val);
                }

                if (!isEmptyRow) {
                    rows.add(rowData);
                }
            }
        }
        return rows;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell);
    }

    public List<String> extractHeaders(MultipartFile file) throws Exception {
        List<Map<String, String>> rows = parseFile(file);
        if (rows.isEmpty()) return Collections.emptyList();
        return new ArrayList<>(rows.get(0).keySet());
    }
}
