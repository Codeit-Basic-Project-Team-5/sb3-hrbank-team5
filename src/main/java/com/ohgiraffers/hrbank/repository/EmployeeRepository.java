package com.ohgiraffers.hrbank.repository;

import com.ohgiraffers.hrbank.entity.Employee;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // 기본 제공 메서드: save(), findById(), findAll(), deleteById()

    Optional<Employee> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByEmployeeNumber(String employeeNumber);

    Optional<Employee> findByEmployeeNumber(String employeeNumber);

    //대시보드 - 총 직원 수 조회
    long getTotalEmployeeCount();
}
