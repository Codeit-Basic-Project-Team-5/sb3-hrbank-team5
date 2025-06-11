package com.ohgiraffers.hrbank.exception;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String email) {
        super("이메일 '" + email + "'는 이미 사용 중입니다.");
    }
}
