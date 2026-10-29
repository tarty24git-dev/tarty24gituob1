package com.cth.datamatch.user.repository;

import com.cth.datamatch.user.entity.UserActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<UserActivityLog, Long> {
    List<UserActivityLog> findTop50ByOrderByCreatedAtDesc();
}
