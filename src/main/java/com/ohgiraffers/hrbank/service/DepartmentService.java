package com.ohgiraffers.hrbank.service;

import com.ohgiraffers.hrbank.dto.data.DepartmentDto;
import com.ohgiraffers.hrbank.dto.request.DepartmentCreateRequest;
import com.ohgiraffers.hrbank.dto.request.DepartmentUpdateRequest;
import com.ohgiraffers.hrbank.dto.response.DepartmentPageResponse;
import java.util.List;

public interface DepartmentService {
    DepartmentDto create(DepartmentCreateRequest request);

    List<DepartmentDto> findAll();
    DepartmentDto findById(Long id);

    DepartmentDto update(Long id, DepartmentUpdateRequest request);

    void delete(Long id);

    DepartmentPageResponse<DepartmentDto> findAllSorted(String nameOrDescription, Long idAfter, String Cursor, String sortField, String sortDirection, int size);
}
