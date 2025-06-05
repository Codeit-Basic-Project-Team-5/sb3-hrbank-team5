package com.ohgiraffers.hrbank.dto.data;

import java.time.LocalDateTime;

public record ChangeLogDto(
    Long id,
    String type,
    int employeeId,
    String memo,
    String ipAddress,
    LocalDateTime updatedAt

) {

}
