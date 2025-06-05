package com.ohgiraffers.hrbank.dto.data;

public record ChangeLogDiffDto(
    String fieldName,
    String oldValue,
    String newValue
) {

}
