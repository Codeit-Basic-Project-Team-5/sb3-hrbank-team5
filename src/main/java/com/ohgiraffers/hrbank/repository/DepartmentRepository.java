package com.ohgiraffers.hrbank.repository;

import com.ohgiraffers.hrbank.entity.Department;
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
    @Query("""
        SELECT data FROM Department data
        WHERE
            (:nameOrDescription IS NULL OR data.name LIKE %:nameOrDescription% OR data.description LIKE %:nameOrDescription%)
     AND (
           (:sortField = 'name' AND (
             (:sortDirection = 'asc' AND ((:Cursor IS NULL OR data.name > :Cursor OR (data.name = :Cursor AND data.id > :IdAfter))))
                 OR
             (:sortDirection = 'desc' AND ((:Cursor IS NULL OR data.name < :Cursor OR (data.name = :Cursor AND data.id < :IdAfter))))
           ))
           OR
           (:sortField = 'establishedDate' AND (
             (:sortDirection = 'asc' AND (
               (:Cursor IS NULL OR data.establishedDate > :Cursor OR (data.establishedDate = :Cursor AND data.id > :IdAfter))
             )) OR
             (:sortDirection = 'desc' AND (
               (:Cursor IS NULL OR data.establishedDate < :Cursor OR (data.establishedDate = :Cursor AND data.id < :IdAfter))
             ))
           ))
         )
    """)
    List<Department> findByCursor(
        @Param("nameOrDescription") String nameOrDescription,
        @Param("sortField") String sortField,
        @Param("sortDirection") String sortDirection,
        @Param("Cursor") String Cursor,
        @Param("IdAfter") Long IdAfter,
        Pageable pageable);

    // name이나 description에 검색어가 포함된 개수 반환
    @Query("SELECT COUNT(d) FROM Department d WHERE d.name LIKE %:keyword% OR d.description LIKE %:keyword%")
    long countByNameOrDescription(@Param("keyword") String keyword);

}
