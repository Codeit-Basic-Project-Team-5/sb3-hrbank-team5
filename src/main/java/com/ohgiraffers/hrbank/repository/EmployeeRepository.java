package com.ohgiraffers.hrbank.repository;

import com.ohgiraffers.hrbank.entity.Employee;
import com.ohgiraffers.hrbank.entity.EmployeeStatus;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // 기본 제공 메서드: save(), findById(), findAll(), deleteById()

    Optional<Employee> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByEmployeeNumber(String employeeNumber);

    Optional<Employee> findByEmployeeNumber(String employeeNumber);


    // 대시보드 - 조건별 총 직원 수 조회
    // 기본 제공 메서드: count() *status 상관 없이 전체 조회*
    long countByStatus(EmployeeStatus status);

    long countByHireDateBetween(LocalDate fromDate, LocalDate toDate);

    long countByStatusAndHireDateBetween(EmployeeStatus status, LocalDate fromDate, LocalDate toDate);


}
