package com.ohgiraffers.hrbank.dto.request;

import com.ohgiraffers.hrbank.dto.data.ChangeLogDiffDto;
import java.util.List;
import lombok.Builder;


@Builder
public record ChangeLogRequestDto(
    String type,
    String employee_id,
    String memo,
    List<ChangeLogDiffDto> diffs
) {

}
