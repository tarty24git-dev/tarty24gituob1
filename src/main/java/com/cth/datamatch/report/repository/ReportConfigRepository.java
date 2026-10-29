package com.cth.datamatch.report.repository;

import com.cth.datamatch.report.entity.ReportConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportConfigRepository extends JpaRepository<ReportConfig, Long> {
    List<ReportConfig> findByActiveTrue();
}
