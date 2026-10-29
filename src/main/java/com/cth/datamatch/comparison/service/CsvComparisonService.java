package com.cth.datamatch.comparison.service;

import com.cth.datamatch.comparison.model.ComparisonResult;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CsvComparisonService {

    public ComparisonResult compareDatasets(List<Map<String, String>> file1Data,
                                           List<Map<String, String>> file2Data,
                                           List<String> keyColumns,
                                           List<String> compareColumns) {

        ComparisonResult result = new ComparisonResult();
        result.setTotalFile1Rows(file1Data.size());
        result.setTotalFile2Rows(file2Data.size());

        // Index File 2 by Composite Key
        Map<String, List<Map<String, String>>> file2KeyMap = new LinkedHashMap<>();
        for (Map<String, String> row : file2Data) {
            String key = buildCompositeKey(row, keyColumns);
            file2KeyMap.computeIfAbsent(key, k -> new ArrayList<>()).add(row);
        }

        Set<Map<String, String>> matchedFile2Rows = new HashSet<>();

        for (Map<String, String> row1 : file1Data) {
            String key1 = buildCompositeKey(row1, keyColumns);

            if (file2KeyMap.containsKey(key1) && !file2KeyMap.get(key1).isEmpty()) {
                List<Map<String, String>> matches = file2KeyMap.get(key1);
                Map<String, String> row2 = matches.get(0); // Match primary
                matchedFile2Rows.add(row2);

                boolean allMatch = true;
                Map<String, String> combinedRow = new LinkedHashMap<>(row1);

                for (String col : compareColumns) {
                    String val1 = row1.getOrDefault(col, "").trim();
                    String val2 = row2.getOrDefault(col, "").trim();

                    if (!val1.equalsIgnoreCase(val2)) {
                        allMatch = false;
                        combinedRow.put(col + " (File 2)", val2);
                    }
                }

                if (allMatch) {
                    combinedRow.put("_STATUS_", "MATCHED");
                    result.getMatchedRows().add(combinedRow);
                } else {
                    combinedRow.put("_STATUS_", "MISMATCHED");
                    result.getMismatchedRows().add(combinedRow);
                }
            } else {
                Map<String, String> unmatchedRow = new LinkedHashMap<>(row1);
                unmatchedRow.put("_STATUS_", "UNMATCHED_FILE1");
                result.getUnmatchedFile1Rows().add(unmatchedRow);
            }
        }

        // Identify File 2 Unmatched Rows
        for (Map<String, String> row2 : file2Data) {
            if (!matchedFile2Rows.contains(row2)) {
                Map<String, String> unmatchedRow = new LinkedHashMap<>(row2);
                unmatchedRow.put("_STATUS_", "UNMATCHED_FILE2");
                result.getUnmatchedFile2Rows().add(unmatchedRow);
            }
        }

        result.setMatchedCount(result.getMatchedRows().size());
        result.setMismatchedCount(result.getMismatchedRows().size());
        result.setUnmatchedFile1Count(result.getUnmatchedFile1Rows().size());
        result.setUnmatchedFile2Count(result.getUnmatchedFile2Rows().size());

        return result;
    }

    private String buildCompositeKey(Map<String, String> row, List<String> keyColumns) {
        StringBuilder sb = new StringBuilder();
        for (String keyCol : keyColumns) {
            String val = row.getOrDefault(keyCol, "").trim().toLowerCase();
            sb.append(val).append("||");
        }
        return sb.toString();
    }
}
