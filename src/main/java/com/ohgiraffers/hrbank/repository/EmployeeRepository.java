package com.ohgiraffers.hrbank.repository;

import com.ohgiraffers.hrbank.entity.Employee;
import com.ohgiraffers.hrbank.entity.EmployeeStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // 기본 제공 메서드: save(), findById(), findAll(), deleteById()

    Optional<Employee> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByEmployeeNumber(String employeeNumber);

    Optional<Employee> findByEmployeeNumber(String employeeNumber);


    // 대시 보드 관련 기본 제공 메서드: count() *status 상관 없이 전체 조회*
    long countByStatus(EmployeeStatus status);

    long countByStatusAndHireDateBetween(EmployeeStatus status, LocalDate fromDate, LocalDate toDate);

    @Query("SELECT e.department.name, COUNT(e) FROM Employee e WHERE e.status = :status GROUP BY e.department.name")
    List<Object[]> countByDepartment(@Param("status") EmployeeStatus status);

    @Query("SELECT e.position, COUNT(e) FROM Employee e WHERE e.status = :status GROUP BY e.position")
    List<Object[]> countByPosition(@Param("status") EmployeeStatus status);

    @Query("""
        SELECT COUNT(e)
        FROM Employee e
        WHERE (e.status = 'ACTIVE' OR e.status = 'ON_LEAVE')
          AND e.hireDate BETWEEN :startDate AND :endDate
    """)
    long countWorkingBetweenDates(@Param("startDate") LocalDate fromDate, @Param("endDate") LocalDate toDate);
}