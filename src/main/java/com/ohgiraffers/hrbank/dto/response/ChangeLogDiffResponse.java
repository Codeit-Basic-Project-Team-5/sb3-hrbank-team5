package com.ohgiraffers.hrbank.dto.response;

import java.time.Instant;

public record ChangeLogDiffResponse(
    String fieldName,
    String oldValue,
    String newValue,
    Instant createdAt
) { }