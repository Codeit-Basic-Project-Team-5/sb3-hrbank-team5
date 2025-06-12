package com.ohgiraffers.hrbank.repository;

import com.ohgiraffers.hrbank.entity.Employee;
import com.ohgiraffers.hrbank.entity.EmployeeStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, EmployeeRepositoryCustom, EmployeeDashboardRepository {

    boolean existsByEmail(String email);

    Optional<Employee> findByEmployeeNumber(String employeeNumber);
}