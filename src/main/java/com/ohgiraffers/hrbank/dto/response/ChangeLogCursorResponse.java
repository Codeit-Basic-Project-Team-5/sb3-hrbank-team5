package com.ohgiraffers.hrbank.dto.response;

import java.time.Instant;
import java.util.List;

public record ChangeLogCursorResponse(
    List<ChangeLogListResponse> content,
    Instant nextCursor,
    Long nextIdAfter,
    int size,
    long totalElements,
    boolean hasNext
) { }