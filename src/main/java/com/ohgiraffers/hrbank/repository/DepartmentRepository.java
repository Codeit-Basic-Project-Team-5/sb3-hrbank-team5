package com.ohgiraffers.hrbank.repository;

import com.ohgiraffers.hrbank.entity.Department;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    Department findDepartmentById(Long id);
    boolean existsByName(String name);
    boolean existsById(Long id);


    /**커서 기반 페이지 구현 JPQL 메서드
     */
    // name 기준 오름차순
    @Query("""
    SELECT d FROM Department d
    WHERE
        (:nameOrDescription IS NULL OR d.name LIKE %:nameOrDescription% OR d.description LIKE %:nameOrDescription%)
        AND (
            (:Cursor IS NULL)
            OR (d.name > :Cursor)
            OR (d.name = :Cursor AND d.id > :IdAfter)
        )
    ORDER BY d.name ASC, d.id ASC
    """)
    List<Department> findByCursorNameAsc(
        @Param("nameOrDescription") String nameOrDescription,
        @Param("Cursor") String cursor,
        @Param("IdAfter") Long idAfter,
        Pageable pageable
    );

    // name 기준 내림차순
    @Query("""
    SELECT d FROM Department d
    WHERE
        (:nameOrDescription IS NULL OR d.name LIKE %:nameOrDescription% OR d.description LIKE %:nameOrDescription%)
        AND (
            (:Cursor IS NULL)
            OR (d.name < :Cursor)
            OR (d.name = :Cursor AND d.id < :IdAfter)
        )
    ORDER BY d.name DESC, d.id DESC
    """)
    List<Department> findByCursorNameDesc(
        @Param("nameOrDescription") String nameOrDescription,
        @Param("Cursor") String cursor,
        @Param("IdAfter") Long idAfter,
        Pageable pageable
    );

    // establishedDate 기준 오름차순
    @Query("""
    SELECT d FROM Department d
    WHERE
        (:nameOrDescription IS NULL OR d.name LIKE %:nameOrDescription% OR d.description LIKE %:nameOrDescription%)
        AND (
            (:Cursor IS NULL)
            OR (d.establishedDate > :Cursor)
            OR (d.establishedDate = :Cursor AND d.id > :IdAfter)
        )
    ORDER BY d.establishedDate ASC, d.id ASC
    """)
    List<Department> findByCursorDateAsc(
        @Param("nameOrDescription") String nameOrDescription,
        @Param("Cursor") LocalDate cursor,
        @Param("IdAfter") Long idAfter,
        Pageable pageable
    );

    // establishedDate 기준 내림차순
    @Query("""
    SELECT d FROM Department d
    WHERE
        (:nameOrDescription IS NULL OR d.name LIKE %:nameOrDescription% OR d.description LIKE %:nameOrDescription%)
        AND (
            (:Cursor IS NULL)
            OR (d.establishedDate < :Cursor)
            OR (d.establishedDate = :Cursor AND d.id < :IdAfter)
        )
    ORDER BY d.establishedDate DESC, d.id DESC
    """)
    List<Department> findByCursorDateDesc(
        @Param("nameOrDescription") String nameOrDescription,
        @Param("Cursor") LocalDate cursor,
        @Param("IdAfter") Long idAfter,
        Pageable pageable
    );

    // name이나 description에 검색어가 포함된 개수 반환
    @Query("SELECT COUNT(d) FROM Department d WHERE d.name LIKE %:keyword% OR d.description LIKE %:keyword%")
    long countByNameOrDescription(@Param("keyword") String keyword);

}
