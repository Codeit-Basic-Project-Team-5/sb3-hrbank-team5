package com.ohgiraffers.hrbank.repository;

import com.ohgiraffers.hrbank.entity.ChangeLog;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface ChangeLogRepository extends JpaRepository<ChangeLog, Long> {


    @Query("""
            SELECT c
              FROM ChangeLog c
             WHERE c.updatedAt < :cursor
        """)
    List<ChangeLog> searchWithCursor(
        @Param("cursor") Instant cursor,
        Pageable pageable
    );

    //전체 이력 건수 반환
    long count();

    Long countByUpdatedAtBetween(Instant from, Instant to);
}

