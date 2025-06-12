package com.ohgiraffers.hrbank.service.basic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ohgiraffers.hrbank.dto.data.ChangeLogDiffDto;
import com.ohgiraffers.hrbank.dto.data.EmployeeDto;
import com.ohgiraffers.hrbank.dto.data.EmployeeSearchCondition;
import com.ohgiraffers.hrbank.dto.request.ChangeLogRequest;
import com.ohgiraffers.hrbank.dto.request.EmployeeCreateRequest;
import com.ohgiraffers.hrbank.dto.request.EmployeeSearchRequest;
import com.ohgiraffers.hrbank.dto.request.EmployeeUpdateRequest;
import com.ohgiraffers.hrbank.dto.request.FileCreateRequest;
import com.ohgiraffers.hrbank.dto.response.CursorPageResponseEmployeeDto;
import com.ohgiraffers.hrbank.entity.Department;
import com.ohgiraffers.hrbank.entity.Employee;
import com.ohgiraffers.hrbank.entity.EmployeeStatus;
import com.ohgiraffers.hrbank.entity.File;
import com.ohgiraffers.hrbank.exception.DuplicateEmailException;
import com.ohgiraffers.hrbank.exception.EmployeeNotFoundException;
import com.ohgiraffers.hrbank.exception.FileProcessingException;
import com.ohgiraffers.hrbank.exception.InvalidRequestException;
import com.ohgiraffers.hrbank.exception.department.DepartmentNotFoundException;
import com.ohgiraffers.hrbank.mapper.EmployeeMapper;
import com.ohgiraffers.hrbank.repository.DepartmentRepository;
import com.ohgiraffers.hrbank.repository.EmployeeRepository;
import com.ohgiraffers.hrbank.repository.FileRepository;
import com.ohgiraffers.hrbank.service.ChangeLogService;
import com.ohgiraffers.hrbank.service.EmployeeService;
import com.ohgiraffers.hrbank.storage.FileStorage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicEmployeeService implements EmployeeService {
    private final EmployeeRepository employeeRepository;

    private final DepartmentRepository departmentRepository;

    private final EmployeeMapper employeeMapper;

    private final FileRepository fileRepository;
    private final FileStorage fileStorage;

    private final ChangeLogService changeLogService;

    // 파일 저장 경로 설정값 주입
    @Value("${hrbank.storage.local.root-path}")
    private String fileStorageRootPath;

    // JSON 직렬화를 위한 ObjectMapper (커서 인코딩용)
    private final ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule());

    @Override
    public EmployeeDto create(EmployeeCreateRequest employeeCreateRequest,
        Optional<FileCreateRequest> optionalFileCreateRequest) {
        Department department = departmentRepository.findDepartmentById(employeeCreateRequest.departmentId());
        if (department == null) {
            throw new DepartmentNotFoundException();
        }

        if (employeeRepository.existsByEmail(employeeCreateRequest.email())) {
            throw new DuplicateEmailException(employeeCreateRequest.email());
        }

        String memo = employeeCreateRequest.memo();

        File nullableProfile = optionalFileCreateRequest
            .map(profileRequest -> processProfileImage(profileRequest))
            .orElse(null);

        Employee employee = new Employee(
            employeeCreateRequest.name(),
            employeeCreateRequest.email(),
            "TEMP",   // 임시 사원번호
            department,
            employeeCreateRequest.position(),
            employeeCreateRequest.hireDate(),
            nullableProfile
        );

        Employee savedEmployee = employeeRepository.save(employee);

        String actualEmployeeNumber = generateEmployeeNumber(savedEmployee.getId());
        savedEmployee.setEmployeeNumber(actualEmployeeNumber);

        Employee finalEmployee = employeeRepository.save(savedEmployee);

        return employeeMapper.toDto(finalEmployee);
    }

    @Override
    public EmployeeDto update(Long employeeId, EmployeeUpdateRequest employeeUpdateRequest,
        Optional<FileCreateRequest> optionalFileCreateRequest) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        String newName = employeeUpdateRequest.name();
        String newEmail = employeeUpdateRequest.email();
        if (employeeRepository.existsByEmail(newEmail) && !employee.getEmail().equals(newEmail)) {
            throw new DuplicateEmailException(newEmail);
        }

        Department newDepartment = departmentRepository.findDepartmentById(employeeUpdateRequest.departmentId());
        if (newDepartment == null) {
            throw new DepartmentNotFoundException();
        }
        String newPosition = employeeUpdateRequest.position();
        LocalDate newHireDate = employeeUpdateRequest.hireDate();
        EmployeeStatus newStatus;
        try {
            newStatus = EmployeeStatus.valueOf(employeeUpdateRequest.status());
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestException(
                String.format("잘못된 직원 상태입니다: %s", employeeUpdateRequest.status())
            );
        }

        File nullableProfile = optionalFileCreateRequest
            .map(profileRequest -> processProfileImage(profileRequest))
            .orElse(null);

        String newMemo = employeeUpdateRequest.memo();

        employee.update(newName, newEmail, newDepartment, newPosition, newHireDate, newStatus, nullableProfile);
        employeeRepository.save(employee);

        return employeeMapper.toDto(employee);
    }

    @Override
    @Transactional
    public void delete(Long employeeId, HttpServletRequest request) {

        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        // 프로필 이미지 정보를 미리 저장 (Employee 삭제 전에)
        File profileImageToDelete = employee.getProfileImage();

        List<ChangeLogDiffDto> diffs = new ArrayList<>();
        diffs.add(new ChangeLogDiffDto("name", employee.getName(), "-"));
        diffs.add(new ChangeLogDiffDto("email", employee.getEmail(), "-"));
        diffs.add(new ChangeLogDiffDto("departmentName", employee.getDepartment().getName(), "-"));
        diffs.add(new ChangeLogDiffDto("position", employee.getPosition(), "-"));
        diffs.add(new ChangeLogDiffDto("hireDate", employee.getHireDate().toString(), "-"));
        diffs.add(new ChangeLogDiffDto("employeeNumber", employee.getEmployeeNumber(), "-"));
        diffs.add(new ChangeLogDiffDto("status", employee.getStatus().name(), "-"));
        if (employee.getProfileImage() != null) {
            diffs.add(new ChangeLogDiffDto(
                "profileImageId",
                employee.getProfileImage().getId().toString(),
                "-"
            ));
        }

        changeLogService.registerChangeLog(
            new ChangeLogRequest(
                "DELETED",
                employee.getEmployeeNumber(),
                "직원 소프트 삭제 처리",
                diffs
            ),
            request
        );

        // 1. 먼저 Employee 삭제 (외래키 제약 조건 해결)
        employee.softDelete();


        // 2. 그 다음에 프로필 이미지 삭제 (있는 경우에만)
        if (profileImageToDelete != null) {
            // 실제 파일 삭제 처리
            deletePhysicalFile(profileImageToDelete);

            // File 엔티티 삭제
            fileRepository.deleteById(profileImageToDelete.getId());
        }

        employeeRepository.save(employee);
    }

    @Override
    public EmployeeDto find(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        return employeeMapper.toDto(employee);
    }

    @Override
    public CursorPageResponseEmployeeDto findEmployees(EmployeeSearchRequest request) {

        // 페이지 크기 결정 및 페이징 설정
        Pageable pageable = PageRequest.of(0, request.size() + 1);

        // 검색 조건 생성 및 데이터 조회
        EmployeeSearchCondition searchCondition = createSearchCondition(request, pageable);
        List<Employee> employees = employeeRepository.searchEmployees(searchCondition);

        // 페이징 처리 및 응답 생성
        return buildCursorPageResponse(employees, request.size(), request);
    }

    // =================================================================
    // Private 헬퍼 메서드들
    // =================================================================

    /**
     * 프로필 이미지 처리 메서드
     */
    private File processProfileImage(FileCreateRequest profileRequest) {
        String fileName = profileRequest.fileName();
        String contentType = profileRequest.contentType();
        byte[] bytes = profileRequest.bytes();

        // 파일 확장자 추출
        String extension = extractExtension(fileName);

        try {
            // 파일 엔티티 생성 및 저장
            File file = new File(fileName, contentType, (long) bytes.length);
            File savedFile = fileRepository.save(file);

            // 실제 파일 저장 (FileStorage 인터페이스에 맞게)
            try (OutputStream out = fileStorage.put(savedFile.getId(), extension)) {
                out.write(bytes);
            } catch (IOException e) {
                throw new RuntimeException("프로필 이미지 저장 실패: " + fileName, e);
            }

            return savedFile;

        } catch (Exception e) {
            throw new FileProcessingException(fileName, e);
        }
    }

    /**
     * 검색 조건 객체 생성
     */
    private EmployeeSearchCondition createSearchCondition(EmployeeSearchRequest request, Pageable pageable) {
        String decodedCursor = decodeCursorIfPresent(request.cursor());

        return new EmployeeSearchCondition(
            request.nameOrEmail(),
            request.departmentName(),
            request.position(),
            request.employeeNumber(),
            request.hireDateFrom(),
            request.hireDateTo(),
            request.status(),
            request.idAfter(),
            decodedCursor,
            request.sortField(),
            request.sortDirection(),
            pageable
        );
    }

    /**
     * 커서가 있으면 디코딩, 없으면 null 반환
     */
    private String decodeCursorIfPresent(String cursor) {
        if (cursor == null || cursor.trim().isEmpty()) {
            return null;
        }

        try {
            byte[] decodedBytes = Base64.getDecoder().decode(cursor);
            return new String(decodedBytes, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            // 잘못된 커서 형식일 경우 null 반환 (처음부터 조회)
            return null;
        }
    }

    /**
     * 커서 페이지 응답 객체 생성
     */
    private CursorPageResponseEmployeeDto buildCursorPageResponse(
        List<Employee> employees,
        int pageSize,
        EmployeeSearchRequest request) {

        // 다음 페이지 존재 여부 확인
        boolean hasNextPage = employees.size() > pageSize;

        // 실제 반환할 데이터 (다음 페이지 확인용 +1 제거)
        List<Employee> actualContent = extractActualContent(employees, pageSize, hasNextPage);

        // 전체 개수 조회
        long totalElements = employeeRepository.countEmployeesWithConditions(
            request.nameOrEmail(),        // String nameOrEmail
            request.departmentName(),     // String departmentName
            request.position(),           // String position
            request.employeeNumber(),     // String employeeNumber
            request.hireDateFrom(),       // LocalDate hireDateFrom
            request.hireDateTo(),         // LocalDate hireDateTo
            request.status()              // EmployeeStatus status
        );

        // 다음 페이지 정보 생성
        NextPageInfo nextPageInfo = createNextPageInfo(actualContent, pageSize, hasNextPage, request);

        // DTO 변환
        List<EmployeeDto> employeeDtos = convertToEmployeeDtos(actualContent);

        return new CursorPageResponseEmployeeDto(
            employeeDtos,
            nextPageInfo.cursor(),
            nextPageInfo.idAfter(),
            pageSize,
            totalElements,
            hasNextPage
        );
    }

    /**
     * 실제 반환할 콘텐츠 추출 (페이지 크기만큼만)
     */
    private List<Employee> extractActualContent(List<Employee> employees, int pageSize, boolean hasNextPage) {
        return hasNextPage ? employees.subList(0, pageSize) : employees;
    }

    /**
     * 다음 페이지 정보 생성
     */
    private NextPageInfo createNextPageInfo(
        List<Employee> content,
        int pageSize,
        boolean hasNextPage,
        EmployeeSearchRequest request) {

        if (!hasNextPage || content.isEmpty()) {
            return new NextPageInfo(null, null);
        }

        Employee lastEmployee = content.get(pageSize - 1);
        String nextCursor = generateEncodedCursor(lastEmployee, request.sortField());
        Long nextIdAfter = lastEmployee.getId();

        return new NextPageInfo(nextCursor, nextIdAfter);
    }

    /**
     * Employee 리스트를 EmployeeDto 리스트로 변환
     */
    private List<EmployeeDto> convertToEmployeeDtos(List<Employee> employees) {
        return employees.stream()
            .map(employeeMapper::toDto)
            .toList();
    }

    /**
     * 커서 값 생성 및 Base64 인코딩
     *
     * @param employee 마지막 직원 정보
     * @param sortField 정렬 기준 필드
     * @return Base64로 인코딩된 커서
     */
    private String generateEncodedCursor(Employee employee, String sortField) {
        String cursorValue = extractCursorValue(employee, sortField);
        return Base64.getEncoder().encodeToString(
            cursorValue.getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * 정렬 필드에 따른 커서 값 추출
     */
    private String extractCursorValue(Employee employee, String sortField) {
        return switch (sortField) {
            case "hireDate" -> employee.getHireDate().toString();
            case "employeeNumber" -> employee.getEmployeeNumber();
            case "name" -> employee.getName();
            default -> {
                // 예상치 못한 정렬 필드일 경우 기본값으로 name 사용
                yield employee.getName();
            }
        };
    }

    /**
     * 사원번호 자동 생성 (예: EMP-2025-001)
     */
    private String generateEmployeeNumber(Long employeeId) {
        int year = LocalDate.now().getYear();
        return String.format("EMP-%d-%d", year, employeeId);
    }

    /**
     * 파일명에서 확장자 추출
     */
    private String extractExtension(String fileName) {
        int dotIdx = fileName.lastIndexOf(".");
        return (dotIdx != -1) ? fileName.substring(dotIdx) : "";
    }

    /**
     * 실제 물리적 파일 삭제
     * LocalFileStorage의 파일 저장 규칙에 맞춰 삭제
     */
    private void deletePhysicalFile(File profileImage) {
        try {
            String extension = extractExtension(profileImage.getName());

            // LocalFileStorage의 파일 경로 생성 규칙: root/id.extension
            // application.yml에서 설정된 경로를 사용
            String rootPath = fileStorageRootPath; // 설정에서 주입받는 경로
            java.nio.file.Path filePath = java.nio.file.Paths.get(rootPath, profileImage.getId() + extension);

            // 파일이 존재하는지 확인 후 삭제
            if (java.nio.file.Files.exists(filePath)) {
                java.nio.file.Files.delete(filePath);
            } else {
            }

        } catch (Exception e) {
            // 파일 삭제 실패해도 직원 삭제는 계속 진행
            System.err.println("프로필 이미지 파일 삭제 실패: " + e.getMessage());
        }
    }

    // =================================================================
    // 내부 클래스 - 다음 페이지 정보를 담는 레코드
    // =================================================================

    /**
     * 다음 페이지 정보를 담는 내부 레코드
     * - 코드의 가독성을 높이고 매개변수 전달을 간소화
     */
    private record NextPageInfo(String cursor, Long idAfter) {
    }
}
