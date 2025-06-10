package com.ohgiraffers.hrbank.dto.data;

import java.time.Instant;

public record ChangeLogDto(
    Long id,
    String type,
    int employeeId,
    String memo,
    String ipAddress,
    Instant updatedAt

) {

}
