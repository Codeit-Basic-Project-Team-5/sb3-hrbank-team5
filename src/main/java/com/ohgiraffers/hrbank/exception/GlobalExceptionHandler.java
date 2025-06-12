package com.ohgiraffers.hrbank.exception;

import com.ohgiraffers.hrbank.dto.response.ErrorResponse;
import java.time.OffsetDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  /**
   * 직원 찾을 수 없음 -> 404
   */
  @ExceptionHandler(EmployeeNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleEmployeeNotFound(EmployeeNotFoundException ex) {

    ErrorResponse response = new ErrorResponse(OffsetDateTime.now(), 404, ex.getMessage(), "요청한 직원이 존재하지 않습니다.");

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
  }

  /**
   * 이메일 중복 -> 409
   */
  @ExceptionHandler(DuplicateEmailException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateEmail(DuplicateEmailException ex) {

    ErrorResponse response = new ErrorResponse(OffsetDateTime.now(), 409, ex.getMessage(), "다른 이메일을 사용해주세요.");

    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
  }

  /**
   * 잘못된 요청 -> 400
   */
  @ExceptionHandler(InvalidRequestException.class)
  public ResponseEntity<ErrorResponse> handleInvalidRequest(InvalidRequestException ex) {

    ErrorResponse response = new ErrorResponse(OffsetDateTime.now(), 400, ex.getMessage(), "입력 데이터를 확인해주세요.");

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  /**
   * 잘못된 커서 -> 400
   */
  @ExceptionHandler(InvalidCursorException.class)
  public ResponseEntity<ErrorResponse> handleInvalidCursor(InvalidCursorException ex) {

    ErrorResponse response = new ErrorResponse(OffsetDateTime.now(), 400, ex.getMessage(), "페이지 정보가 유효하지 않습니다.");

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  /**
   * 파일 처리 오류 -> 400
   */
  @ExceptionHandler(FileProcessingException.class)
  public ResponseEntity<ErrorResponse> handleFileProcessing(FileProcessingException ex) {

    ErrorResponse response = new ErrorResponse(OffsetDateTime.now(), 400, ex.getMessage(), "파일 업로드 중 문제가 발생했습니다.");

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  /**
   * 파일 찾을 수 없음 -> 404
   */
  @ExceptionHandler(FileNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(FileNotFoundException ex) {
    ErrorResponse body = new ErrorResponse(OffsetDateTime.now(),404, ex.getMessage(), "파일을 찾을 수 없습니다.");
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
  }
}
