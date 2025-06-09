package com.ohgiraffers.hrbank.dto.request;

public record FileCreateRequest(
    String fileName,
    String contentType,
    byte[] bytes
) {

}
