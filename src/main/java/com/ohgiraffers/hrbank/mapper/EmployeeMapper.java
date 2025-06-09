package com.ohgiraffers.hrbank.mapper;

import com.ohgiraffers.hrbank.dto.data.EmployeeDto;
import com.ohgiraffers.hrbank.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(target = "departmentId", source = "department.id")
    @Mapping(target = "departmentName", source = "department.name")
    @Mapping(target = "status", source = "status.statusName")
    @Mapping(target = "profileImageId", constant = "1L")    // 임시로 1L (나중에 실제 프로필 이미지 구현 시 수정)
    EmployeeDto toDto(Employee employee);
}
