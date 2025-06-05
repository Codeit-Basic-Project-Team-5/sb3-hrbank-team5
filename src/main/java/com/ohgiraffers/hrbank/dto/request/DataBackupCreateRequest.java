package com.ohgiraffers.hrbank.dto.request;


import java.time.Instant;

public record DataBackupCreateRequest(
    Long id,
    String worker,
    Instant startedAt
) {

}
