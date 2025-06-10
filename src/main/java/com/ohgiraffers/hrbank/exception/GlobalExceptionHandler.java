package com.ohgiraffers.hrbank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(DepartmentHasEmployeesException.class)
  public ResponseEntity<String> handleDeptHasEmp(DepartmentHasEmployeesException exc) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exc.getMessage());
  }

}
