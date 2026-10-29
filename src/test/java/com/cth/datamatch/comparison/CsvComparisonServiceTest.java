package com.cth.datamatch.comparison;

import com.cth.datamatch.comparison.model.ComparisonResult;
import com.cth.datamatch.comparison.service.CsvComparisonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class CsvComparisonServiceTest {

    private CsvComparisonService comparisonService;

    @BeforeEach
    public void setUp() {
        comparisonService = new CsvComparisonService();
    }

    @Test
    @DisplayName("TC004: Match exact rows in dataset comparison")
    public void testExactMatchingRows() {
        List<Map<String, String>> file1 = List.of(
                Map.of("id", "101", "name", "Alice", "amount", "500"),
                Map.of("id", "102", "name", "Bob", "amount", "750")
        );
        List<Map<String, String>> file2 = List.of(
                Map.of("id", "101", "name", "Alice", "amount", "500"),
                Map.of("id", "102", "name", "Bob", "amount", "750")
        );

        ComparisonResult result = comparisonService.compareDatasets(file1, file2, List.of("id"), List.of("name", "amount"));

        assertEquals(2, result.getMatchedCount());
        assertEquals(0, result.getMismatchedCount());
        assertEquals(0, result.getUnmatchedFile1Count());
        assertEquals(0, result.getUnmatchedFile2Count());
    }

    @Test
    @DisplayName("TC005: Identify mismatched attribute values for identical primary keys")
    public void testMismatchedRows() {
        List<Map<String, String>> file1 = List.of(
                Map.of("id", "101", "name", "Alice", "amount", "500")
        );
        List<Map<String, String>> file2 = List.of(
                Map.of("id", "101", "name", "Alice", "amount", "600") // Difference in amount
        );

        ComparisonResult result = comparisonService.compareDatasets(file1, file2, List.of("id"), List.of("amount"));

        assertEquals(0, result.getMatchedCount());
        assertEquals(1, result.getMismatchedCount());
    }

    @Test
    @DisplayName("TC006: Identify unmatched rows in File A and File B")
    public void testUnmatchedRows() {
        List<Map<String, String>> file1 = List.of(
                Map.of("id", "101", "name", "Alice")
        );
        List<Map<String, String>> file2 = List.of(
                Map.of("id", "202", "name", "Charlie")
        );

        ComparisonResult result = comparisonService.compareDatasets(file1, file2, List.of("id"), List.of("name"));

        assertEquals(0, result.getMatchedCount());
        assertEquals(1, result.getUnmatchedFile1Count());
        assertEquals(1, result.getUnmatchedFile2Count());
    }
}
