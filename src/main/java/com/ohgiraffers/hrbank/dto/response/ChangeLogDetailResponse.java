package com.ohgiraffers.hrbank.dto.response;

import java.time.Instant;
import java.util.List;

public record ChangeLogDetailResponse(
    Long id,
    String type,
    String employeeNumber,
    String memo,
    String ipAddress,
    Instant at,
    List<ChangeLogDiffResponse> diffs
) {

}