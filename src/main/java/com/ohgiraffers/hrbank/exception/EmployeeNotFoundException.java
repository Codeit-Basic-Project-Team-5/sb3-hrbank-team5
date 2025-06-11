package com.ohgiraffers.hrbank.exception;

public class EmployeeNotFoundException extends RuntimeException {
    public EmployeeNotFoundException(Long employeeId) {
        super("ID가 " + employeeId + "인 직원을 찾을 수 없습니다.");
    }
}
