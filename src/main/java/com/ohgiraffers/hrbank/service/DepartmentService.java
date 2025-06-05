package com.ohgiraffers.hrbank.service;

import com.ohgiraffers.hrbank.dto.data.DepartmentDto;
import com.ohgiraffers.hrbank.dto.request.DepartmentCreateRequest;
import com.ohgiraffers.hrbank.dto.request.DepartmentUpdateRequest;
import com.ohgiraffers.hrbank.entity.Department;
import java.util.List;

public interface DepartmentService {
    Department create(DepartmentCreateRequest request);

    List<DepartmentDto> findAll();
    DepartmentDto findById(Long id);

    Department update(DepartmentUpdateRequest request);

    void delete(Long id);
}
