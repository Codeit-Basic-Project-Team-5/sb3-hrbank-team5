package com.ohgiraffers.hrbank.exception.department;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST) // 400 반환!
public class DepartmentHasEmployeesException extends RuntimeException {
  public DepartmentHasEmployeesException() {
    super("소속직원이 존재하는 부서는 삭제할 수 없습니다.");
  }
}
