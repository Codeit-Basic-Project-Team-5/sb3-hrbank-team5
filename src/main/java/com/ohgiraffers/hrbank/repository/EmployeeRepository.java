package com.ohgiraffers.hrbank.repository;

import com.ohgiraffers.hrbank.entity.Employee;
import com.ohgiraffers.hrbank.entity.EmployeeStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // 기본 제공 메서드: save(), findById(), findAll(), deleteById()

    Optional<Employee> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByEmployeeNumber(String employeeNumber);

    Optional<Employee> findByEmployeeNumber(String employeeNumber);

    /**
     * 커서 기반 페이지네이션으로 직원 목록 조회 (이름 기준 정렬)
     *
     * @param nameOrEmail 이름 또는 이메일 검색어 (부분 일치)
     * @param departmentName 부서명
     * @param position 직함 검색어 (부분 일치)
     * @param employeeNumber 사원번호 검색어 (부분 일치)
     * @param hireDateFrom 입사일 시작 범위
     * @param hireDateTo 입사일 끝 범위
     * @param status 직원 상태
     * @param idAfter 이전 페이지 마지막 ID (커서)
     * @param lastSortValue 이전 페이지 마지막 정렬 값
     * @param isDescending 내림차순 여부
     * @param pageable 페이지 정보
     * @return 검색된 직원 목록
     */
    @Query("""
        SELECT e FROM Employee e 
        JOIN FETCH e.department d 
        LEFT JOIN FETCH e.profileImage p
        WHERE 
            (:nameOrEmail IS NULL OR 
             LOWER(e.name) LIKE LOWER(CONCAT('%', :nameOrEmail, '%')) OR 
             LOWER(e.email) LIKE LOWER(CONCAT('%', :nameOrEmail, '%')))
        AND (:departmentName IS NULL OR LOWER(e.department.name) LIKE LOWER(CONCAT('%', :departmentName, '%')))
        AND (:position IS NULL OR LOWER(e.position) LIKE LOWER(CONCAT('%', :position, '%')))
        AND (:employeeNumber IS NULL OR LOWER(e.employeeNumber) LIKE LOWER(CONCAT('%', :employeeNumber, '%')))
        AND (:hireDateFrom IS NULL OR e.hireDate >= :hireDateFrom)
        AND (:hireDateTo IS NULL OR e.hireDate <= :hireDateTo)
        AND (:status IS NULL OR e.status = :status)
        AND (
            :idAfter IS NULL OR 
            (
                CASE WHEN :isDescending = true THEN
                    (e.name < :lastSortValue OR (e.name = :lastSortValue AND e.id < :idAfter))
                ELSE
                    (e.name > :lastSortValue OR (e.name = :lastSortValue AND e.id > :idAfter))
                END
            )
        )
        ORDER BY 
            CASE WHEN :isDescending = true THEN e.name END DESC,
            CASE WHEN :isDescending = false THEN e.name END ASC,
            CASE WHEN :isDescending = true THEN e.id END DESC,
            CASE WHEN :isDescending = false THEN e.id END ASC
        """)
    List<Employee> findEmployeesWithCursorByName(
        @Param("nameOrEmail") String nameOrEmail,
        @Param("departmentName") String departmentName,
        @Param("position") String position,
        @Param("employeeNumber") String employeeNumber,
        @Param("hireDateFrom") LocalDate hireDateFrom,
        @Param("hireDateTo") LocalDate hireDateTo,
        @Param("status") EmployeeStatus status,
        @Param("idAfter") Long idAfter,
        @Param("lastSortValue") String lastSortValue,
        @Param("isDescending") boolean isDescending,
        Pageable pageable
    );

    /**
     * 입사일 기준 정렬용 커서 쿼리
     */
    @Query("""
        SELECT e FROM Employee e 
        JOIN FETCH e.department d 
        LEFT JOIN FETCH e.profileImage p
        WHERE 
            (:nameOrEmail IS NULL OR 
             LOWER(e.name) LIKE LOWER(CONCAT('%', :nameOrEmail, '%')) OR 
             LOWER(e.email) LIKE LOWER(CONCAT('%', :nameOrEmail, '%')))
        AND (:departmentName IS NULL OR LOWER(e.department.name) LIKE LOWER(CONCAT('%', :departmentName, '%')))
        AND (:position IS NULL OR LOWER(e.position) LIKE LOWER(CONCAT('%', :position, '%')))
        AND (:employeeNumber IS NULL OR LOWER(e.employeeNumber) LIKE LOWER(CONCAT('%', :employeeNumber, '%')))
        AND (:hireDateFrom IS NULL OR e.hireDate >= :hireDateFrom)
        AND (:hireDateTo IS NULL OR e.hireDate <= :hireDateTo)
        AND (:status IS NULL OR e.status = :status)
        AND (
            :idAfter IS NULL OR 
            (
                CASE WHEN :isDescending = true THEN
                    (e.hireDate < :lastSortValue OR (e.hireDate = :lastSortValue AND e.id < :idAfter))
                ELSE
                    (e.hireDate > :lastSortValue OR (e.hireDate = :lastSortValue AND e.id > :idAfter))
                END
            )
        )
        ORDER BY 
            CASE WHEN :isDescending = true THEN e.hireDate END DESC,
            CASE WHEN :isDescending = false THEN e.hireDate END ASC,
            CASE WHEN :isDescending = true THEN e.id END DESC,
            CASE WHEN :isDescending = false THEN e.id END ASC
        """)
    List<Employee> findEmployeesWithCursorByHireDate(
        @Param("nameOrEmail") String nameOrEmail,
        @Param("departmentName") String departmentName,
        @Param("position") String position,
        @Param("employeeNumber") String employeeNumber,
        @Param("hireDateFrom") LocalDate hireDateFrom,
        @Param("hireDateTo") LocalDate hireDateTo,
        @Param("status") EmployeeStatus status,
        @Param("idAfter") Long idAfter,
        @Param("lastSortValue") LocalDate lastSortValue,
        @Param("isDescending") boolean isDescending,
        Pageable pageable
    );

    /**
     * 사원번호 기준 정렬용 커서 쿼리
     */
    @Query("""
        SELECT e FROM Employee e 
        JOIN FETCH e.department d 
        LEFT JOIN FETCH e.profileImage p
        WHERE 
            (:nameOrEmail IS NULL OR 
             LOWER(e.name) LIKE LOWER(CONCAT('%', :nameOrEmail, '%')) OR 
             LOWER(e.email) LIKE LOWER(CONCAT('%', :nameOrEmail, '%')))
        AND (:departmentName IS NULL OR LOWER(e.department.name) LIKE LOWER(CONCAT('%', :departmentName, '%')))
        AND (:position IS NULL OR LOWER(e.position) LIKE LOWER(CONCAT('%', :position, '%')))
        AND (:employeeNumber IS NULL OR LOWER(e.employeeNumber) LIKE LOWER(CONCAT('%', :employeeNumber, '%')))
        AND (:hireDateFrom IS NULL OR e.hireDate >= :hireDateFrom)
        AND (:hireDateTo IS NULL OR e.hireDate <= :hireDateTo)
        AND (:status IS NULL OR e.status = :status)
        AND (
            :idAfter IS NULL OR 
            (
                CASE WHEN :isDescending = true THEN
                    (e.employeeNumber < :lastSortValue OR (e.employeeNumber = :lastSortValue AND e.id < :idAfter))
                ELSE
                    (e.employeeNumber > :lastSortValue OR (e.employeeNumber = :lastSortValue AND e.id > :idAfter))
                END
            )
        )
        ORDER BY 
            CASE WHEN :isDescending = true THEN e.employeeNumber END DESC,
            CASE WHEN :isDescending = false THEN e.employeeNumber END ASC,
            CASE WHEN :isDescending = true THEN e.id END DESC,
            CASE WHEN :isDescending = false THEN e.id END ASC
        """)
    List<Employee> findEmployeesWithCursorByEmployeeNumber(
        @Param("nameOrEmail") String nameOrEmail,
        @Param("departmentName") String departmentName,
        @Param("position") String position,
        @Param("employeeNumber") String employeeNumber,
        @Param("hireDateFrom") LocalDate hireDateFrom,
        @Param("hireDateTo") LocalDate hireDateTo,
        @Param("status") EmployeeStatus status,
        @Param("idAfter") Long idAfter,
        @Param("lastSortValue") String lastSortValue,
        @Param("isDescending") boolean isDescending,
        Pageable pageable
    );

    /**
     * 검색 조건에 맞는 총 직원 수 조회 (페이지네이션 정보용)
     */
    @Query("""
        SELECT COUNT(e) FROM Employee e 
        WHERE 
            (:nameOrEmail IS NULL OR 
             LOWER(e.name) LIKE LOWER(CONCAT('%', :nameOrEmail, '%')) OR 
             LOWER(e.email) LIKE LOWER(CONCAT('%', :nameOrEmail, '%')))
        AND (:departmentName IS NULL OR LOWER(e.department.name) LIKE LOWER(CONCAT('%', :departmentName, '%')))
        AND (:position IS NULL OR LOWER(e.position) LIKE LOWER(CONCAT('%', :position, '%')))
        AND (:employeeNumber IS NULL OR LOWER(e.employeeNumber) LIKE LOWER(CONCAT('%', :employeeNumber, '%')))
        AND (:hireDateFrom IS NULL OR e.hireDate >= :hireDateFrom)
        AND (:hireDateTo IS NULL OR e.hireDate <= :hireDateTo)
        AND (:status IS NULL OR e.status = :status)
        """)
    Long countEmployeesWithConditions(
        @Param("nameOrEmail") String nameOrEmail,
        @Param("departmentName") String departmentName,
        @Param("position") String position,
        @Param("employeeNumber") String employeeNumber,
        @Param("hireDateFrom") LocalDate hireDateFrom,
        @Param("hireDateTo") LocalDate hireDateTo,
        @Param("status") EmployeeStatus status
    );

    // 대시 보드 관련 기본 제공 메서드: count() *status 상관 없이 전체 조회*
    long countByStatus(EmployeeStatus status);

    long countByStatusAndHireDateBetween(EmployeeStatus status, LocalDate fromDate, LocalDate toDate);

    @Query("SELECT e.department.name, COUNT(e) FROM Employee e WHERE e.status = :status GROUP BY e.department.name")
    List<Object[]> countByDepartment(@Param("status") EmployeeStatus status);

    @Query("SELECT e.position, COUNT(e) FROM Employee e WHERE e.status = :status GROUP BY e.position")
    List<Object[]> countByPosition(@Param("status") EmployeeStatus status);

    Long countByDepartmentId(Long departmentId);
}
