package com.cth.datamatch.report.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.*;

@Service
public class DatabaseMetadataService {

    private final DataSource dataSource;

    public DatabaseMetadataService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<String> getAvailableTables() {
        List<String> tables = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            String catalog = conn.getCatalog();
            String schema = conn.getSchema();

            try (ResultSet rs = metaData.getTables(catalog, schema, "%", new String[]{"TABLE"})) {
                while (rs.next()) {
                    String tableName = rs.getString("TABLE_NAME");
                    // Exclude system tables if any
                    if (!tableName.startsWith("SYSTEM_") && !tableName.startsWith("HTE_")) {
                        tables.add(tableName);
                    }
                }
            }
        } catch (Exception e) {
            // Fallback list of known entities
            tables = List.of("USERS", "REPORT_CONFIGS", "COMPARISON_LOGS", "AUDIT_LOGS");
        }
        return tables;
    }

    public Map<String, String> getTableColumns(String tableName) {
        Map<String, String> columns = new LinkedHashMap<>();
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            String catalog = conn.getCatalog();
            String schema = conn.getSchema();

            try (ResultSet rs = metaData.getColumns(catalog, schema, tableName, "%")) {
                while (rs.next()) {
                    String colName = rs.getString("COLUMN_NAME");
                    String typeName = rs.getString("TYPE_NAME");
                    columns.put(colName, typeName);
                }
            }
        } catch (Exception e) {
            columns.put("ID", "BIGINT");
            columns.put("NAME", "VARCHAR");
        }
        return columns;
    }
}
