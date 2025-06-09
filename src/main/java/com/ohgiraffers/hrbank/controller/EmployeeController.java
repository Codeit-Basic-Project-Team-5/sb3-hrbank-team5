package com.ohgiraffers.hrbank.controller;

import com.ohgiraffers.hrbank.dto.data.EmployeeDto;
import com.ohgiraffers.hrbank.dto.request.EmployeeCreateRequest;
import com.ohgiraffers.hrbank.dto.request.EmployeeUpdateRequest;
import com.ohgiraffers.hrbank.dto.request.FileCreateRequest;
import com.ohgiraffers.hrbank.service.EmployeeService;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
        Optional<FileCreateRequest> profileRequest = Optional.ofNullable(profile)
            .flatMap(this::resolveProfileRequest);

        EmployeeDto createdEmployee = employeeService.create(employeeCreateRequest, profileRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);
    }

    /**
     * 직원 정보 수정
     */
    @PatchMapping(
        path = "{employeeId}",
        consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public ResponseEntity<EmployeeDto> update(
        @PathVariable("employeeId") Long employeeId,
        @RequestPart("employee") EmployeeUpdateRequest employeeUpdateRequest,
        @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        Optional<FileCreateRequest> profileRequest = Optional.ofNullable(profile)
            .flatMap(this::resolveProfileRequest);

        EmployeeDto updatedEmployee = employeeService.update(employeeId, employeeUpdateRequest, profileRequest);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(updatedEmployee);
    }

    /**
     * 직원 정보 삭제
     */
    @DeleteMapping(path = "{employeeId}")
    public ResponseEntity<Void> delete(@PathVariable("employeeId") Long employeeId) {
        employeeService.delete(employeeId);
        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .build();
    }

    /**
     * 직원 정보 상세 조회
     */
    @GetMapping(path = "{employeeId}")
    public ResponseEntity<EmployeeDto> find(@PathVariable("employeeId") Long employeeId) {
        EmployeeDto employee = employeeService.find(employeeId);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(employee);
    }

    private Optional<FileCreateRequest> resolveProfileRequest(MultipartFile profileFile) {
        if (profileFile.isEmpty()) {
            return Optional.empty();
        } else {
            try {
                FileCreateRequest binaryContentCreateRequest = new FileCreateRequest(
                    profileFile.getOriginalFilename(),
                    profileFile.getContentType(),
                    profileFile.getBytes()
                );
                return Optional.of(binaryContentCreateRequest);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
