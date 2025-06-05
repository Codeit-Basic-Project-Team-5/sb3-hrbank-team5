package com.ohgiraffers.hrbank.dto.request;

import java.time.LocalDate;

public record DepartmentUpdateRequest(
    Long id,
    String name,
    String description,
    LocalDate establishedDate
) {

}
