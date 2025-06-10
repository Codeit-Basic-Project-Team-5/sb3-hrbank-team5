package com.ohgiraffers.hrbank.dto.response;

import com.ohgiraffers.hrbank.dto.data.EmployeeDto;
import java.util.List;

public record CursorPageResponseEmployeeDto(
    List<EmployeeDto> content,
    String nextCursor,
    Long nextIdAfter,
    int size,
    Long totalElements,
    boolean hasNext
) {

}
