package com.ohgiraffers.hrbank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST) // 400 반환!
public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String email) {
        super("이메일 '" + email + "'는 이미 사용 중입니다.");
    }
}
