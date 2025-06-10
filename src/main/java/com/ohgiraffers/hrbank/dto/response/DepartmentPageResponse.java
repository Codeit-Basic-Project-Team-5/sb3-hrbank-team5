package com.ohgiraffers.hrbank.dto.response;

import java.util.List;

public record DepartmentPageResponse<T>(
    List<T> content,
    String nextCursor,
    Long nextIdAfter,
    int size,
    long totalElements,
    boolean hasNext
){}