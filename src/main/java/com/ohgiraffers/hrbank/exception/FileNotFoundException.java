package com.ohgiraffers.hrbank.exception;

public class FileNotFoundException extends RuntimeException {

    public FileNotFoundException(Long fileId) {
        super("파일을 찾을 수 없습니다. id=" + fileId);
    }
}
