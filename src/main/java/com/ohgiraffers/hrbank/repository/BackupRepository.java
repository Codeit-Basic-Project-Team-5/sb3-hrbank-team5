package com.ohgiraffers.hrbank.repository;

import com.ohgiraffers.hrbank.entity.Backup;
import com.ohgiraffers.hrbank.entity.StatusType;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BackupRepository extends JpaRepository<Backup, Long> {

    List<Backup> findAllByWorker(String worker);

    @Query("""
        SELECT d FROM Backup d
        WHERE (:worker IS NULL OR d.worker LIKE CONCAT('%', :worker, '%'))
          AND (:status IS NULL OR d.status = :status)
          AND (:startedAtFrom IS NULL OR d.startedAt >= :startedAtFrom)
          AND (:startedAtTo IS NULL OR d.startedAt <= :startedAtTo)
          AND (:cursor IS NULL OR (
              d.startedAt < :cursor
              OR (d.startedAt = :cursor AND d.id < :idAfter)
          ))
    """)
    List<Backup> findAllByCursor(
        @Param("worker") String worker,
        @Param("status") StatusType status,
        @Param("startedAtFrom") Instant startedAtFrom,
        @Param("startedAtTo") Instant startedAtTo,
        @Param("cursor") Instant cursor,
        @Param("idAfter") Long idAfter,
        Pageable pageable // PageRequest.of(0, size + 1)
    );

}
