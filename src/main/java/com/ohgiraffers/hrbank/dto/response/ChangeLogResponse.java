package com.ohgiraffers.hrbank.dto.response;

import java.time.Instant;

public record ChangeLogResponse(
    Long id,
    String type,
    String employeeNumber,
    String memo,
    String ipAddress,
    Instant at
) {

}