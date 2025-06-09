package com.ohgiraffers.hrbank.mapper;

import com.ohgiraffers.hrbank.dto.data.EmployeeDto;
import com.ohgiraffers.hrbank.entity.Employee;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {
    EmployeeDto toDto(Employee employee);
}
