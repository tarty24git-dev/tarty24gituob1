package com.cth.datamatch.report.controller;

import com.cth.datamatch.report.entity.ReportConfig;
import com.cth.datamatch.report.service.DatabaseMetadataService;
import com.cth.datamatch.report.service.DynamicQueryBuilderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
public class ReportController {

    private final DatabaseMetadataService metadataService;
    private final DynamicQueryBuilderService queryBuilderService;

    @Value("${app.name:DataMatch Studio}")
    private String appName;

    public ReportController(DatabaseMetadataService metadataService, DynamicQueryBuilderService queryBuilderService) {
        this.metadataService = metadataService;
        this.queryBuilderService = queryBuilderService;
    }

    @GetMapping("/reports")
    public String reportsPage(Model model, Authentication authentication) {
        model.addAttribute("appName", appName);
        model.addAttribute("tables", metadataService.getAvailableTables());
        model.addAttribute("savedReports", queryBuilderService.getAllReportConfigs());
        model.addAttribute("currentUsername", authentication != null ? authentication.getName() : "");
        return "reports";
    }

    @GetMapping("/api/reports/tables")
    @ResponseBody
    public ResponseEntity<List<String>> getTables() {
        return ResponseEntity.ok(metadataService.getAvailableTables());
    }

    @GetMapping("/api/reports/tables/{tableName}/columns")
    @ResponseBody
    public ResponseEntity<Map<String, String>> getColumns(@PathVariable String tableName) {
        return ResponseEntity.ok(metadataService.getTableColumns(tableName));
    }

    @PostMapping("/api/reports/execute")
    @ResponseBody
    public ResponseEntity<?> executeReport(@RequestBody Map<String, Object> payload) {
        try {
            String tableName = (String) payload.get("tableName");
            List<String> fields = (List<String>) payload.get("fields");
            String filterCondition = (String) payload.get("filterCondition");
            String sortBy = (String) payload.get("sortBy");
            int limit = payload.containsKey("limit") ? Integer.parseInt(payload.get("limit").toString()) : 100;

            List<Map<String, Object>> result = queryBuilderService.executeDynamicReport(tableName, fields, filterCondition, sortBy, limit);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed executing report query: " + e.getMessage()));
        }
    }

    @PostMapping("/api/reports/save")
    @ResponseBody
    public ResponseEntity<?> saveReportConfig(@RequestBody ReportConfig config, Authentication authentication) {
        try {
            if (authentication != null) {
                config.setCreatedBy(authentication.getName());
            } else {
                config.setCreatedBy("admin");
            }
            ReportConfig saved = queryBuilderService.saveReportConfig(config);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
