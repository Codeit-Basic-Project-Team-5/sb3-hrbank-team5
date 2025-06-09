package com.ohgiraffers.hrbank.service;

import com.ohgiraffers.hrbank.dto.data.EmployeeDto;
import com.ohgiraffers.hrbank.dto.request.EmployeeCreateRequest;
import com.ohgiraffers.hrbank.dto.request.EmployeeUpdateRequest;
import com.ohgiraffers.hrbank.dto.request.FileCreateRequest;
import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    EmployeeDto create(EmployeeCreateRequest employeeCreateRequest,
        Optional<FileCreateRequest> optionalFileCreateRequest);
    EmployeeDto find(Long employeeId);
//    List<EmployeeDto> findAll();
    EmployeeDto update(Long employeeId, EmployeeUpdateRequest employeeUpdateRequest,
        Optional<FileCreateRequest> optionalFileCreateRequest);
    void delete(Long employeeId);
}
