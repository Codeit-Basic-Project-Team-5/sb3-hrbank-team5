package com.ohgiraffers.hrbank.repository;

import com.ohgiraffers.hrbank.entity.EmployeeStatus;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmployeeDashboardRepository {

    long countByStatus(EmployeeStatus status);

    long countByStatusAndHireDateBetween(EmployeeStatus status, LocalDate fromDate, LocalDate toDate);

    @Query("SELECT e.department.name, COUNT(e) FROM Employee e WHERE e.status = :status GROUP BY e.department.name")
    List<Object[]> countByDepartment(@Param("status") EmployeeStatus status);

    @Query("SELECT e.position, COUNT(e) FROM Employee e WHERE e.status = :status GROUP BY e.position")
    List<Object[]> countByPosition(@Param("status") EmployeeStatus status);

    Long countByDepartmentId(Long departmentId);

    @Query("""
        SELECT COUNT(e)
        FROM Employee e
        WHERE (e.status = 'ACTIVE' OR e.status = 'ON_LEAVE')
          AND e.hireDate <= :date
    """)
    long countWorkingUpToDate(@Param("date") LocalDate date);
}