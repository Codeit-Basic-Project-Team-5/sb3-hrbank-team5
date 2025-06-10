package com.ohgiraffers.hrbank.dto.request;

import java.time.Instant;

public record ChangeLogSearchRequest(
    String employeeIdPartial,
    String memoPartial,
    String ipAddressPartial,
    String type,
    Instant from,
    Instant to,
    Long lastId,
    Integer size, //페이지 크기
    String sortBy // IP주소, 생성시간 오름차순/내림차순
) { }