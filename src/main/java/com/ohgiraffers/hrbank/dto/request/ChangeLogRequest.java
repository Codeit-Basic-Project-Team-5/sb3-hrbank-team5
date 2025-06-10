package com.ohgiraffers.hrbank.dto.request;

import com.ohgiraffers.hrbank.dto.data.ChangeLogDiffDto;
import java.util.List;
import lombok.Builder;

public record ChangeLogRequest(
    String type,
    String employeeId,
    String memo,
    List<ChangeLogDiffDto> diffs
) {

}