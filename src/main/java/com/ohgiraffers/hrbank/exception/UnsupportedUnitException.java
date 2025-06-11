package com.ohgiraffers.hrbank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnsupportedUnitException extends RuntimeException {

    public UnsupportedUnitException(String unit) {
        super("지원하지 않는 집계 단위입니다. unit=" + unit);
    }
}
