package com.ohgiraffers.hrbank.dto.response;

import java.time.Instant;

public record ChangeLogListResponse(
    Long   id,
    String type,
    String employeeId,
    String memo,
    String ipAddress,
    Instant updatedAt
) { }