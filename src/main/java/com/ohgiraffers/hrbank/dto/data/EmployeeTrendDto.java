package com.ohgiraffers.hrbank.dto.data;

import java.time.LocalDate;

public record EmployeeTrendDto(
    LocalDate date,
    long count,
    long change,
    double changeRate
) {}