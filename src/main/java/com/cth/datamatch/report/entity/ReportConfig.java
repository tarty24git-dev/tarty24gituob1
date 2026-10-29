package com.cth.datamatch.report.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "REPORT_CONFIGS")
public class ReportConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "REPORT_NAME", nullable = false, length = 100)
    private String reportName;

    @Column(length = 255)
    private String description;

    @Column(name = "TARGET_TABLE", nullable = false, length = 100)
    private String targetTable;

    @Column(name = "SELECTED_FIELDS", nullable = false, length = 1000)
    private String selectedFields; // Comma separated field names

    @Column(name = "FILTER_CONDITIONS", length = 1000)
    private String filterConditions;

    @Column(name = "SORT_BY", length = 100)
    private String sortBy;

    @Column(name = "CREATED_BY", nullable = false, length = 50)
    private String createdBy;

    @Column(name = "CRON_EXPRESSION", length = 50)
    private String cronExpression;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getReportName() { return reportName; }
    public void setReportName(String reportName) { this.reportName = reportName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTargetTable() { return targetTable; }
    public void setTargetTable(String targetTable) { this.targetTable = targetTable; }

    public String getSelectedFields() { return selectedFields; }
    public void setSelectedFields(String selectedFields) { this.selectedFields = selectedFields; }

    public String getFilterConditions() { return filterConditions; }
    public void setFilterConditions(String filterConditions) { this.filterConditions = filterConditions; }

    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getCronExpression() { return cronExpression; }
    public void setCronExpression(String cronExpression) { this.cronExpression = cronExpression; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
