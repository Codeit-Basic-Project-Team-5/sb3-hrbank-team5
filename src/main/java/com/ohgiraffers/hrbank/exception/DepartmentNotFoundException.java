package com.ohgiraffers.hrbank.exception;

public class DepartmentNotFoundException extends RuntimeException {
    public DepartmentNotFoundException(Long departmentId) {
        super("ID가 " + departmentId + "인 부서를 찾을 수 없습니다.");
    }
}
