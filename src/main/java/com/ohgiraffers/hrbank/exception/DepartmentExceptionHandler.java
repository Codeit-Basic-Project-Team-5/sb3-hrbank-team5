package com.ohgiraffers.hrbank.exception;

import com.ohgiraffers.hrbank.controller.api.DepartmentApi;
import com.ohgiraffers.hrbank.dto.response.ErrorResponse;
import com.ohgiraffers.hrbank.exception.department.DepartmentHasEmployeesException;
import com.ohgiraffers.hrbank.exception.department.DepartmentNotFoundException;
import java.time.OffsetDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = DepartmentApi.class)
public class DepartmentExceptionHandler {
  /** 이름 중복 -> 400
   */
  @ExceptionHandler(DuplicatedNameException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateName(DuplicatedNameException ex) {

    ErrorResponse response = new ErrorResponse(OffsetDateTime.now(), 400, ex.getMessage(), "부서 이름은 중복될 수 없습니다.");

    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
  }
  /** 부서 찾을 수 없음 -> 404 */
  @ExceptionHandler(DepartmentNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleDepartmentNotFound(DepartmentNotFoundException ex) {

    ErrorResponse response = new ErrorResponse(OffsetDateTime.now(), 404, ex.getMessage(), "요청한 부서가 존재하지 않습니다.");

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
  }
  /** 부서에 직원 존재시 삭제불가 예외처리 -> 400 */
  @ExceptionHandler(DepartmentHasEmployeesException.class)
  public ResponseEntity<ErrorResponse> handleDeptHasEmp(DepartmentHasEmployeesException ex) {

    ErrorResponse response = new ErrorResponse(OffsetDateTime.now(), 400, ex.getMessage(), "직원이 소속된 부서는 삭제할 수 없습니다.");

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }
}
