package com.ohgiraffers.hrbank.repository;

import com.ohgiraffers.hrbank.entity.ChangeLog;
import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChangeLogRepository extends JpaRepository<ChangeLog, Long> {


    @Query("""
        SELECT c
          FROM ChangeLog c
         WHERE (:empId IS NULL    OR str(c.employeeId) LIKE %:empId%)
           AND (:memo   IS NULL    OR c.memo           LIKE %:memo%)
           AND (:ip     IS NULL    OR c.ipAddress      LIKE %:ip%)
           AND (:type   IS NULL    OR c.type           = :type)
           AND (:fromDt IS NULL    OR c.updatedAt      >= :fromDt)
           AND (:toDt   IS NULL    OR c.updatedAt      <= :toDt)
        """)
    Page<ChangeLog> search(
        @Param("empId")  String employeeIdPartial,
        @Param("memo")   String memoPartial,
        @Param("ip")     String ipAddressPartial,
        @Param("type")   String type,
        @Param("fromDt") Instant from,
        @Param("toDt")   Instant to,
        Pageable pageable
    );

    Long countByUpdatedAtBetween(Instant from, Instant to);
}