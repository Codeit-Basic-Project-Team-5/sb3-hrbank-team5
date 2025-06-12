package com.ohgiraffers.hrbank.dto.data;

import com.ohgiraffers.hrbank.entity.EmployeeStatus;
import java.time.LocalDate;
import org.springframework.data.domain.Pageable;

public record EmployeeSearchCondition(
    String nameOrEmail,
    String departmentName,
    String position,
    String employeeNumber,
    LocalDate hireDateFrom,
    LocalDate hireDateTo,
    EmployeeStatus status,
    Long idAfter,
    String cursor,
    String sortField,
    String sortDirection,
    Pageable pageable
) {
}
