package com.ohgiraffers.hrbank.mapper;

import com.ohgiraffers.hrbank.dto.response.EmployeeResponse;
import com.ohgiraffers.hrbank.entity.Employee;

public class EmployeeMapper {

    public static EmployeeResponse toResponse(Employee employee) {
        return EmployeeResponse.builder()
            .id(employee.getId())
            .name(employee.getName())
            .email(employee.getEmail())
            .employeeNumber(employee.getEmployeeNumber())
//            .department(employee.getDepartment())
            .position(employee.getPosition())
            .hireDate(employee.getHireDate())
            .status(employee.getStatus())
//            .프로필 이미지
            .build();
    }

}
