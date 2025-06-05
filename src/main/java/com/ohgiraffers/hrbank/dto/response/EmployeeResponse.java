package com.ohgiraffers.hrbank.dto.response;

import com.ohgiraffers.hrbank.entity.EmployeeStatus;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public class EmployeeResponse {
    private Long id;
    private String name;
    private String email;
    private String employeeNumber;
    private String department;
    private String position;
    private LocalDate hireDate;
    private EmployeeStatus status;
    // 프로필 이미지
}
