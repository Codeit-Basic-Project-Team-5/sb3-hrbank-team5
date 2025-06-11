package com.ohgiraffers.hrbank.repository;

import com.ohgiraffers.hrbank.dto.data.EmployeeSearchCondition;
import com.ohgiraffers.hrbank.entity.Employee;
import com.ohgiraffers.hrbank.entity.EmployeeStatus;
import java.time.LocalDate;
import java.util.List;

public interface EmployeeRepositoryCustom {
    List<Employee> searchEmployees(EmployeeSearchCondition condition);

    Long countEmployeesWithConditions(
        String nameOrEmail,
        String departmentName,
        String position,
        String employeeNumber,
        LocalDate hireDateFrom,
        LocalDate hireDateTo,
        EmployeeStatus status
    );
}
