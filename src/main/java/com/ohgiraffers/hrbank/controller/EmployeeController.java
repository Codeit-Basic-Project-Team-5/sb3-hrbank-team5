package com.ohgiraffers.hrbank.controller;

import com.ohgiraffers.hrbank.dto.data.EmployeeDto;
import com.ohgiraffers.hrbank.dto.request.EmployeeCreateRequest;
import com.ohgiraffers.hrbank.dto.request.EmployeeSearchRequest;
import com.ohgiraffers.hrbank.dto.request.EmployeeUpdateRequest;
import com.ohgiraffers.hrbank.dto.request.FileCreateRequest;
import com.ohgiraffers.hrbank.dto.response.CursorPageResponseEmployeeDto;
import com.ohgiraffers.hrbank.entity.EmployeeStatus;
import com.ohgiraffers.hrbank.service.EmployeeService;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    /**
     * 직원 목록 조회 (커서 기반 페이지네이션)
     */
    @GetMapping
    public ResponseEntity<CursorPageResponseEmployeeDto> findEmployees(
        // 검색 조건 파라미터들
        @RequestParam(required = false) String nameOrEmail,
        @RequestParam(required = false) String departmentName,
        @RequestParam(required = false) String position,
        @RequestParam(required = false) String employeeNumber,

        // 입사일 범위 검색 (yyyy-MM-dd 형식)
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hireDateFrom,

        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hireDateTo,

        // 직원 상태 (ACTIVE, ON_LEAVE, RESIGNED)
        @RequestParam(required = false) EmployeeStatus status,

        // 정렬 조건
        @RequestParam(defaultValue = "name") String sortField,
        @RequestParam(defaultValue = "asc") String sortDirection,

        // 페이지네이션
        @RequestParam(required = false) Long idAfter,
        @RequestParam(required = false) String cursor,
        @RequestParam(defaultValue = "30") Integer size
    ) {
        // 요청 파라미터를 EmployeeSearchRequest로 변환
        EmployeeSearchRequest searchRequest = new EmployeeSearchRequest(
            nameOrEmail,
            departmentName,
            position,
            employeeNumber,
            hireDateFrom,
            hireDateTo,
            status,
            sortField,
            sortDirection,
            idAfter,
            cursor,
            size
        );

        // 서비스 호출
        CursorPageResponseEmployeeDto response = employeeService.findEmployees(searchRequest);

        return ResponseEntity.ok(response);
    }


    /**
     * 커서 정보를 담는 내부 클래스
     */

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
