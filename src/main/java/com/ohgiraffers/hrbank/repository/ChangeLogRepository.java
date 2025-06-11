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

    // 1) from/to 둘 다 없는 경우
    @Query("""
        SELECT c
          FROM ChangeLog c
          JOIN c.employee e
         WHERE c.updatedAt < :cursor
           AND (:empPattern  IS NULL OR e.employeeNumber LIKE :empPattern)
           AND (:memoPattern IS NULL OR c.memo           LIKE :memoPattern)
           AND (:ipPattern   IS NULL OR c.ipAddress      LIKE :ipPattern)
           AND (:type        IS NULL OR c.type           = :type)
        """)
    List<ChangeLog> findAllByFilterNoDate(
        @Param("cursor") Instant cursor,
        @Param("empPattern") String empPattern,
        @Param("memoPattern") String memoPattern,
        @Param("ipPattern") String ipPattern,
        @Param("type") String type,
        Pageable pageable
    );

    // 2) from만 있는 경우
    @Query("""
        SELECT c
          FROM ChangeLog c
          JOIN c.employee e
         WHERE c.updatedAt < :cursor
           AND (:empPattern  IS NULL OR e.employeeNumber LIKE :empPattern)
           AND (:memoPattern IS NULL OR c.memo           LIKE :memoPattern)
           AND (:ipPattern   IS NULL OR c.ipAddress      LIKE :ipPattern)
           AND (:type        IS NULL OR c.type           = :type)
           AND c.updatedAt   >= :from
        """)
    List<ChangeLog> findAllByFilterFromOnly(
        @Param("cursor") Instant cursor,
        @Param("empPattern") String empPattern,
        @Param("memoPattern") String memoPattern,
        @Param("ipPattern") String ipPattern,
        @Param("type") String type,
        @Param("from") Instant from,
        Pageable pageable
    );

    // 3) to만 있는 경우
    @Query("""
        SELECT c
          FROM ChangeLog c
          JOIN c.employee e
         WHERE c.updatedAt < :cursor
           AND (:empPattern  IS NULL OR e.employeeNumber LIKE :empPattern)
           AND (:memoPattern IS NULL OR c.memo           LIKE :memoPattern)
           AND (:ipPattern   IS NULL OR c.ipAddress      LIKE :ipPattern)
           AND (:type        IS NULL OR c.type           = :type)
           AND c.updatedAt   <= :to
        """)
    List<ChangeLog> findAllByFilterToOnly(
        @Param("cursor") Instant cursor,
        @Param("empPattern") String empPattern,
        @Param("memoPattern") String memoPattern,
        @Param("ipPattern") String ipPattern,
        @Param("type") String type,
        @Param("to") Instant to,
        Pageable pageable
    );

    // 4) from/to 둘 다 있는 경우
    @Query("""
        SELECT c
          FROM ChangeLog c
          JOIN c.employee e
         WHERE c.updatedAt < :cursor
           AND (:empPattern  IS NULL OR e.employeeNumber LIKE :empPattern)
           AND (:memoPattern IS NULL OR c.memo           LIKE :memoPattern)
           AND (:ipPattern   IS NULL OR c.ipAddress      LIKE :ipPattern)
           AND (:type        IS NULL OR c.type           = :type)
           AND c.updatedAt   >= :from
           AND c.updatedAt   <= :to
        """)
    List<ChangeLog> findAllByFilterWithDate(
        @Param("cursor") Instant cursor,
        @Param("empPattern") String empPattern,
        @Param("memoPattern") String memoPattern,
        @Param("ipPattern") String ipPattern,
        @Param("type") String type,
        @Param("from") Instant from,
        @Param("to") Instant to,
        Pageable pageable
    );

    // 필터링된 전체 개수만 계산하는 메서드

    @Query("""
        SELECT COUNT(c)
          FROM ChangeLog c
          JOIN c.employee e
         WHERE c.updatedAt < :cursor
           AND (:empPattern  IS NULL OR e.employeeNumber LIKE :empPattern)
           AND (:memoPattern IS NULL OR c.memo           LIKE :memoPattern)
           AND (:ipPattern   IS NULL OR c.ipAddress      LIKE :ipPattern)
           AND (:type        IS NULL OR c.type           = :type)
        """)
    long countByFilterNoDate(
        @Param("cursor") Instant cursor,
        @Param("empPattern") String empPattern,
        @Param("memoPattern") String memoPattern,
        @Param("ipPattern") String ipPattern,
        @Param("type") String type
    );

    @Query("""
        SELECT COUNT(c)
          FROM ChangeLog c
          JOIN c.employee e
         WHERE c.updatedAt < :cursor
           AND (:empPattern  IS NULL OR e.employeeNumber LIKE :empPattern)
           AND (:memoPattern IS NULL OR c.memo           LIKE :memoPattern)
           AND (:ipPattern   IS NULL OR c.ipAddress      LIKE :ipPattern)
           AND (:type        IS NULL OR c.type           = :type)
           AND c.updatedAt   >= :from
        """)
    long countByFilterFromOnly(
        @Param("cursor") Instant cursor,
        @Param("empPattern") String empPattern,
        @Param("memoPattern") String memoPattern,
        @Param("ipPattern") String ipPattern,
        @Param("type") String type,
        @Param("from") Instant from
    );

    @Query("""
        SELECT COUNT(c)
          FROM ChangeLog c
          JOIN c.employee e
         WHERE c.updatedAt < :cursor
           AND (:empPattern  IS NULL OR e.employeeNumber LIKE :empPattern)
           AND (:memoPattern IS NULL OR c.memo           LIKE :memoPattern)
           AND (:ipPattern   IS NULL OR c.ipAddress      LIKE :ipPattern)
           AND (:type        IS NULL OR c.type           = :type)
           AND c.updatedAt   <= :to
        """)
    long countByFilterToOnly(
        @Param("cursor") Instant cursor,
        @Param("empPattern") String empPattern,
        @Param("memoPattern") String memoPattern,
        @Param("ipPattern") String ipPattern,
        @Param("type") String type,
        @Param("to") Instant to
    );

    @Query("""
        SELECT COUNT(c)
          FROM ChangeLog c
          JOIN c.employee e
         WHERE c.updatedAt < :cursor
           AND (:empPattern  IS NULL OR e.employeeNumber LIKE :empPattern)
           AND (:memoPattern IS NULL OR c.memo           LIKE :memoPattern)
           AND (:ipPattern   IS NULL OR c.ipAddress      LIKE :ipPattern)
           AND (:type        IS NULL OR c.type           = :type)
           AND c.updatedAt   >= :from
           AND c.updatedAt   <= :to
        """)
    long countByFilterWithDate(
        @Param("cursor") Instant cursor,
        @Param("empPattern") String empPattern,
        @Param("memoPattern") String memoPattern,
        @Param("ipPattern") String ipPattern,
        @Param("type") String type,
        @Param("from") Instant from,
        @Param("to") Instant to
    );


    Long countByUpdatedAtBetween(Instant from, Instant to);
}