package com.ohgiraffers.hrbank.repository;

import com.ohgiraffers.hrbank.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    Department findDepartmentById(Long id);
    boolean existsByName(String name);
    boolean existsById(Long id);
}
