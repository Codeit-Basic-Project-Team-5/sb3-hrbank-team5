package com.ohgiraffers.hrbank.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ChangeLogDiffResponse(
    @JsonProperty("propertyName")
    String fieldName,

    @JsonProperty("before")
    String oldValue,

    @JsonProperty("after")
    String newValue

) {

}