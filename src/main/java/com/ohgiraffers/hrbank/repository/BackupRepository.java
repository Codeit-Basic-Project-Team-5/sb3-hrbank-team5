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
          WHERE (:worker IS NULL OR d.worker LIKE :worker)
          AND (:status IS NULL OR d.status = :status)
          AND (CAST(:startedAtFrom AS java.time.Instant) IS NULL OR d.startedAt >= :startedAtFrom)
          AND (CAST(:startedAtTo AS java.time.Instant) IS NULL OR d.startedAt <= :startedAtTo)
          AND (d.startedAt < CAST(:cursor AS java.time.Instant)
              OR (d.startedAt = CAST(:cursor AS java.time.Instant) AND d.id <= :idAfter)
          )
    """)
    List<Backup> findAllWithCursor(
        @Param("worker") String worker,
        @Param("status") StatusType status,
        @Param("startedAtFrom") Instant startedAtFrom,
        @Param("startedAtTo") Instant startedAtTo,
        @Param("cursor") Instant cursor,
        @Param("idAfter") Long idAfter,
        Pageable pageable // PageRequest.of(0, size + 1)
    );


    @Query("""
    SELECT d FROM Backup d
    WHERE (:worker IS NULL OR d.worker LIKE :worker)
      AND (:status IS NULL OR d.status = :status)
      AND (CAST(:startedAtFrom AS java.time.Instant) IS NULL OR d.startedAt >= :startedAtFrom)
      AND (CAST(:startedAtTo AS java.time.Instant) IS NULL OR d.startedAt <= :startedAtTo)
""")
    List<Backup> findAllWithoutCursor(
        @Param("worker") String worker,
        @Param("status") StatusType status,
        @Param("startedAtFrom") Instant startedAtFrom,
        @Param("startedAtTo") Instant startedAtTo,
        Pageable pageable
    );
}
