package com.ohgiraffers.hrbank.service;

import com.ohgiraffers.hrbank.dto.data.DepartmentDto;
import com.ohgiraffers.hrbank.dto.request.DepartmentCreateRequest;
import com.ohgiraffers.hrbank.dto.request.DepartmentUpdateRequest;
import com.ohgiraffers.hrbank.dto.response.DepartmentPageResponse;
import com.ohgiraffers.hrbank.entity.Department;
import java.util.List;

public interface DepartmentService {
    Department create(DepartmentCreateRequest request);

    List<DepartmentDto> findAll();
    DepartmentDto findById(Long id);

    Department update(Long id, DepartmentUpdateRequest request);

    void delete(Long id);

    DepartmentPageResponse<DepartmentDto> findAllSorted(String nameOrDescription, Long idAfter, String Cursor, String sortField, String sortDirection, int size);
}
