package com.ohgiraffers.hrbank.repository;

import com.ohgiraffers.hrbank.entity.ChangeLog;
import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChangeLogRepository extends JpaRepository<ChangeLog, Long> {

    // 대시보드 - 조건별 총 직원 수 조회
    long countByUpdatedAtBetween(Instant fromDate, Instant toDate);
}
