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
        SELECT data
        FROM Department data
        WHERE (:nameOrDescription IS NULL OR data.name LIKE %:nameOrDescription% OR data.description LIKE %:nameOrDescription%)
          AND (:idAfter IS NULL OR data.id > :idAfter)
        ORDER BY data.id ASC
    """)
    List<Department> findByCursor(
        @Param("nameOrDescription")String nameOrDescription,
        @Param("idAfter") Long idAfter,
        Pageable pageable);

    // name이나 description에 검색어가 포함된 개수 반환
    @Query("SELECT COUNT(d) FROM Department d WHERE d.name LIKE %:keyword% OR d.description LIKE %:keyword%")
    long countByNameOrDescription(@Param("keyword") String keyword);

}
