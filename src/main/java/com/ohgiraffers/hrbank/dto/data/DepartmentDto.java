package com.ohgiraffers.hrbank.dto.data;

import com.ohgiraffers.hrbank.entity.Department;
import java.time.LocalDate;

public record DepartmentDto(
    Long id,
    String name,
    String description,
    LocalDate establishedDate,
    Long employeeCount
) {

    public static DepartmentDto fromEntity(Department department, Long employeeCount) {
        if (department == null) return null;
        return new DepartmentDto(
            department.getId(),
            department.getName(),
            department.getDescription(),
            department.getEstablishedDate(),
            employeeCount
            );
    }

}
