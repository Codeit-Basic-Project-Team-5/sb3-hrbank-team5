package com.ohgiraffers.hrbank.mapper;

import com.ohgiraffers.hrbank.dto.data.EmployeeDistributionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EmployeeDistributionMapper {

    @Mapping(target = "groupKey", source = "key")
    @Mapping(target = "count", source = "count")
    @Mapping(target = "percentage", source = "percentage")
    EmployeeDistributionDto toDto(String key, long count, double percentage);
}