package com.ohgiraffers.hrbank.dto.data;

public record EmployeeDistributionDto(
    String groupKey,
    long count,
    double percentage
) {}