package com.ohgiraffers.hrbank.service;

import com.ohgiraffers.hrbank.dto.data.EmployeeDto;
import com.ohgiraffers.hrbank.dto.request.EmployeeCreateRequest;
import com.ohgiraffers.hrbank.dto.request.EmployeeSearchRequest;
import com.ohgiraffers.hrbank.dto.request.EmployeeUpdateRequest;
import com.ohgiraffers.hrbank.dto.request.FileCreateRequest;
import com.ohgiraffers.hrbank.dto.response.CursorPageResponseEmployeeDto;
import java.util.Optional;

public interface EmployeeService {
    EmployeeDto create(EmployeeCreateRequest employeeCreateRequest,
        Optional<FileCreateRequest> optionalFileCreateRequest);
    EmployeeDto find(Long employeeId);
    CursorPageResponseEmployeeDto findEmployees(EmployeeSearchRequest searchRequest);
    EmployeeDto update(Long employeeId, EmployeeUpdateRequest employeeUpdateRequest,
        Optional<FileCreateRequest> optionalFileCreateRequest);
    void delete(Long employeeId);
}
