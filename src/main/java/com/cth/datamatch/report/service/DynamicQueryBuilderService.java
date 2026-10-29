package com.cth.datamatch.report.service;

import com.cth.datamatch.report.entity.ReportConfig;
import com.cth.datamatch.report.repository.ReportConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DynamicQueryBuilderService {

    private static final Logger log = LoggerFactory.getLogger(DynamicQueryBuilderService.class);

    private final JdbcTemplate jdbcTemplate;
    private final ReportConfigRepository reportConfigRepository;

    public DynamicQueryBuilderService(JdbcTemplate jdbcTemplate, ReportConfigRepository reportConfigRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.reportConfigRepository = reportConfigRepository;
    }

    public List<Map<String, Object>> executeDynamicReport(String tableName,
                                                        List<String> fields,
                                                        String filterCondition,
                                                        String sortBy,
                                                        int limit) {
        // Sanitize Table Name to prevent SQL Injection
        if (tableName == null || !tableName.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("Invalid table name specification.");
        }

        StringBuilder sql = new StringBuilder("SELECT ");

        if (fields == null || fields.isEmpty()) {
            sql.append("*");
        } else {
            List<String> sanitizedFields = new ArrayList<>();
            for (String f : fields) {
                if (f.matches("^[a-zA-Z0-9_]+$")) {
                    sanitizedFields.add(f);
                }
            }
            if (sanitizedFields.isEmpty()) sql.append("*");
            else sql.append(String.join(", ", sanitizedFields));
        }

        sql.append(" FROM ").append(tableName);

        if (filterCondition != null && !filterCondition.trim().isEmpty()) {
            // Basic condition check
            sql.append(" WHERE ").append(filterCondition);
        }

        if (sortBy != null && !sortBy.trim().isEmpty() && sortBy.matches("^[a-zA-Z0-9_\\s,ASCdesc]+$")) {
            sql.append(" ORDER BY ").append(sortBy);
        }

        if (limit > 0) {
            sql.append(" FETCH FIRST ").append(limit).append(" ROWS ONLY");
        }

        log.info("Executing Dynamic Report SQL: {}", sql);
        return jdbcTemplate.queryForList(sql.toString());
    }

    public ReportConfig saveReportConfig(ReportConfig config) {
        return reportConfigRepository.save(config);
    }

    public List<ReportConfig> getAllReportConfigs() {
        return reportConfigRepository.findAll();
    }

    @Scheduled(cron = "0 0 * * * *") // Every hour automatic report refresh simulation
    public void runScheduledReports() {
        List<ReportConfig> activeReports = reportConfigRepository.findByActiveTrue();
        log.info("Running automatic background report refresh for {} active configurations...", activeReports.size());
        for (ReportConfig report : activeReports) {
            try {
                List<String> fields = Arrays.asList(report.getSelectedFields().split(","));
                List<Map<String, Object>> data = executeDynamicReport(report.getTargetTable(), fields,
                        report.getFilterConditions(), report.getSortBy(), 100);
                log.info("Report '{}' executed successfully. Generated {} records.", report.getReportName(), data.size());
            } catch (Exception e) {
                log.error("Failed executing scheduled report: " + report.getReportName(), e);
            }
        }
    }
}
