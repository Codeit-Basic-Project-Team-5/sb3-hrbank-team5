package com.ohgiraffers.hrbank.service;

import com.ohgiraffers.hrbank.dto.data.EmployeeDto;
import com.ohgiraffers.hrbank.dto.request.EmployeeCreateRequest;
import com.ohgiraffers.hrbank.dto.request.EmployeeUpdateRequest;
import java.util.List;

public interface EmployeeService {
    EmployeeDto create(EmployeeCreateRequest employeeCreateRequest);
//    EmployeeDto find(Long employeeId);
//    List<EmployeeDto> findAll();
    EmployeeDto update(Long employeeId, EmployeeUpdateRequest employeeUpdateRequest);
    void delete(Long employeeId);
}
