package com.ohgiraffers.hrbank.controller;

import com.ohgiraffers.hrbank.dto.data.EmployeeDto;
import com.ohgiraffers.hrbank.dto.request.EmployeeCreateRequest;
import com.ohgiraffers.hrbank.service.EmployeeService;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    /**
     * 직원 등록
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EmployeeDto> create(
        @RequestPart("employee") EmployeeCreateRequest employeeCreateRequest,
        @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {


        EmployeeDto createdEmployee = employeeService.create(employeeCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);
    }

    /**
     * 직원 삭제
     */
    @DeleteMapping(path = "{employeeId}")
    public ResponseEntity<Void> delete(@PathVariable("employeeId") Long employeeId) {
        employeeService.delete(employeeId);
        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build();
    }
}
