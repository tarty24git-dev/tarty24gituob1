package com.cth.datamatch.comparison.controller;

import com.cth.datamatch.comparison.model.ComparisonResult;
import com.cth.datamatch.comparison.service.CsvComparisonService;
import com.cth.datamatch.comparison.service.FileParserService;
import com.cth.datamatch.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.PrintWriter;
import java.util.*;

@Controller
public class ComparisonController {

    private final FileParserService fileParserService;
    private final CsvComparisonService comparisonService;
    private final UserService userService;

    @Value("${app.name:DataMatch Studio}")
    private String appName;

    public ComparisonController(FileParserService fileParserService,
                                CsvComparisonService comparisonService,
                                UserService userService) {
        this.fileParserService = fileParserService;
        this.comparisonService = comparisonService;
        this.userService = userService;
    }

    @GetMapping("/comparison")
    public String comparisonPage(Model model, Authentication authentication) {
        model.addAttribute("appName", appName);
        model.addAttribute("currentUsername", authentication != null ? authentication.getName() : "");
        return "comparison";
    }

    @PostMapping("/api/comparison/inspect-headers")
    @ResponseBody
    public ResponseEntity<?> inspectHeaders(@RequestParam("file1") MultipartFile file1,
                                            @RequestParam("file2") MultipartFile file2) {
        try {
            List<String> headers1 = fileParserService.extractHeaders(file1);
            List<String> headers2 = fileParserService.extractHeaders(file2);

            Set<String> commonHeaders = new LinkedHashSet<>(headers1);
            commonHeaders.retainAll(headers2);

            Map<String, Object> response = new HashMap<>();
            response.put("file1Headers", headers1);
            response.put("file2Headers", headers2);
            response.put("commonHeaders", commonHeaders);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Error inspecting files: " + e.getMessage()));
        }
    }

    @PostMapping("/api/comparison/execute")
    @ResponseBody
    public ResponseEntity<?> executeComparison(@RequestParam("file1") MultipartFile file1,
                                              @RequestParam("file2") MultipartFile file2,
                                              @RequestParam("keyColumns") List<String> keyColumns,
                                              @RequestParam("compareColumns") List<String> compareColumns,
                                              Authentication authentication) {
        try {
            List<Map<String, String>> file1Data = fileParserService.parseFile(file1);
            List<Map<String, String>> file2Data = fileParserService.parseFile(file2);

            ComparisonResult result = comparisonService.compareDatasets(file1Data, file2Data, keyColumns, compareColumns);
            result.setJobName(file1.getOriginalFilename() + " vs " + file2.getOriginalFilename());

            String username = authentication != null ? authentication.getName() : "anonymous";
            userService.logActivity(username, "COMPARISON_EXECUTION",
                    "Compared " + file1.getOriginalFilename() + " (" + file1Data.size() + " rows) and "
                            + file2.getOriginalFilename() + " (" + file2Data.size() + " rows). Matched: " + result.getMatchedCount(), "127.0.0.1");

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Comparison execution failed: " + e.getMessage()));
        }
    }

    @PostMapping("/api/comparison/export-csv")
    public void exportCsv(@RequestBody Map<String, Object> payload, HttpServletResponse response) {
        try {
            List<Map<String, String>> rows = (List<Map<String, String>>) payload.get("rows");
            String reportType = (String) payload.getOrDefault("reportType", "comparison_report");

            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + reportType + ".csv\"");

            if (rows != null && !rows.isEmpty()) {
                PrintWriter writer = response.getWriter();
                Set<String> headers = rows.get(0).keySet();

                // Write Header
                writer.println(String.join(",", headers));

                // Write Rows
                for (Map<String, String> row : rows) {
                    List<String> values = new ArrayList<>();
                    for (String header : headers) {
                        String val = row.getOrDefault(header, "");
                        if (val.contains(",") || val.contains("\"")) {
                            val = "\"" + val.replace("\"", "\"\"") + "\"";
                        }
                        values.add(val);
                    }
                    writer.println(String.join(",", values));
                }
                writer.flush();
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
