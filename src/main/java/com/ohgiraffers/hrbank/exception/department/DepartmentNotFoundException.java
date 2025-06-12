package com.ohgiraffers.hrbank.exception.department;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) // 404 반환!
public class DepartmentNotFoundException extends RuntimeException {
    public DepartmentNotFoundException() {
        super("찾을 수 없습니다.");
    }
}
