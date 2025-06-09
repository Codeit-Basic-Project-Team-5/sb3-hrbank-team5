package com.ohgiraffers.hrbank.mapper;

import com.ohgiraffers.hrbank.dto.data.ChangeLogDiffDto;
import com.ohgiraffers.hrbank.dto.request.ChangeLogRequestDto;
import com.ohgiraffers.hrbank.entity.ChangeLog;
import com.ohgiraffers.hrbank.entity.ChangeLogDiff;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ChangeLogMapper {

    // ChangeLogRequestDto → ChangeLog 매핑
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employeeId", ignore = true)
    @Mapping(target = "ipAddress", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "diffs", ignore = true)
    ChangeLog toEntity(ChangeLogRequestDto dto);

    // ChangeLogDiffDto → ChangeLogDiff 매핑
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "changeLog", source = "changeLog")
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    ChangeLogDiff toDiffEntity(ChangeLog changeLog, ChangeLogDiffDto dto);

    // ChangeLogDiff 리스트 매핑 메서드
    default List<ChangeLogDiff> toDiffEntityList(ChangeLog changeLog, List<ChangeLogDiffDto> dtos) {
        if (dtos == null) {
            return null;
        }
        return dtos.stream()
            .map(dto -> toDiffEntity(changeLog, dto))
            .toList();
    }
}