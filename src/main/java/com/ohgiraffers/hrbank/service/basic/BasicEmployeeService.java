package com.ohgiraffers.hrbank.service.basic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ohgiraffers.hrbank.dto.data.EmployeeDto;
import com.ohgiraffers.hrbank.dto.request.EmployeeCreateRequest;
import com.ohgiraffers.hrbank.dto.request.EmployeeSearchRequest;
import com.ohgiraffers.hrbank.dto.request.EmployeeUpdateRequest;
import com.ohgiraffers.hrbank.dto.request.FileCreateRequest;
import com.ohgiraffers.hrbank.dto.response.CursorPageResponseEmployeeDto;
import com.ohgiraffers.hrbank.entity.Department;
import com.ohgiraffers.hrbank.entity.Employee;
import com.ohgiraffers.hrbank.entity.EmployeeStatus;
import com.ohgiraffers.hrbank.entity.File;
import com.ohgiraffers.hrbank.exception.DepartmentNotFoundException;
import com.ohgiraffers.hrbank.exception.DuplicateEmailException;
import com.ohgiraffers.hrbank.exception.EmployeeNotFoundException;
import com.ohgiraffers.hrbank.exception.FileProcessingException;
import com.ohgiraffers.hrbank.exception.InvalidCursorException;
import com.ohgiraffers.hrbank.exception.InvalidRequestException;
import com.ohgiraffers.hrbank.mapper.EmployeeMapper;
import com.ohgiraffers.hrbank.repository.DepartmentRepository;
import com.ohgiraffers.hrbank.repository.EmployeeRepository;
import com.ohgiraffers.hrbank.repository.FileRepository;
import com.ohgiraffers.hrbank.service.EmployeeService;
import com.ohgiraffers.hrbank.storage.FileStorage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
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
            throw new DepartmentNotFoundException(employeeCreateRequest.departmentId());
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
            throw new DepartmentNotFoundException(employeeUpdateRequest.departmentId());
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
        return employeeMapper.toDto(employee);
    }

    @Override
    public void delete(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        // 프로필 이미지 정보를 미리 저장 (Employee 삭제 전에)
        File profileImageToDelete = employee.getProfileImage();

        try {
            // 1. 먼저 Employee 삭제 (외래키 제약 조건 해결)
            employeeRepository.deleteById(employeeId);

            // 2. 그 다음에 프로필 이미지 삭제 (있는 경우에만)
            if (profileImageToDelete != null) {
                // 실제 파일 삭제 처리
                deletePhysicalFile(profileImageToDelete);

                // File 엔티티 삭제
                fileRepository.deleteById(profileImageToDelete.getId());
            }
        } catch (Exception e) {
            throw new RuntimeException("직원 삭제 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public EmployeeDto find(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        return employeeMapper.toDto(employee);
    }

    @Override
    public CursorPageResponseEmployeeDto findEmployees(EmployeeSearchRequest searchRequest) {
        // 1. 검색 조건 추출
        String nameOrEmail = searchRequest.nameOrEmail();
        String departmentName = searchRequest.departmentName();
        String position = searchRequest.position();
        String employeeNumber = searchRequest.employeeNumber();
        LocalDate hireDateFrom = searchRequest.hireDateFrom();
        LocalDate hireDateTo = searchRequest.hireDateTo();
        EmployeeStatus status = searchRequest.status();

        // 2. 정렬 및 페이지네이션 정보 추출
        String sortField = searchRequest.sortField();
        boolean isDescending = searchRequest.isDescending();
        int size = searchRequest.size();

        // cursor와 idAfter 중 우선순위 결정
        Long idAfter = null;
        Object lastSortValue = null;

        if (searchRequest.cursor() != null && !searchRequest.cursor().isEmpty()) {
            try {
                // cursor가 있으면 cursor 우선 사용
                CursorInfo cursorInfo = decodeCursor(searchRequest.cursor());
                idAfter = cursorInfo.idAfter();
                lastSortValue = cursorInfo.sortValue();
            } catch (Exception e) {
                throw new InvalidCursorException("잘못된 커서 형식입니다: " + searchRequest.cursor());
            }
        } else if (searchRequest.idAfter() != null) {
            // cursor가 없으면 idAfter 사용 (기존 방식)
            idAfter = searchRequest.idAfter();
            lastSortValue = extractLastSortValue(idAfter, sortField);
        }

        // 3. Pageable 생성 (size + 1로 설정하여 다음 페이지 존재 여부 확인)
        Pageable pageable = PageRequest.of(0, size + 1);

        // 4. 정렬 기준에 따라 적절한 Repository 메서드 호출
        List<Employee> employees = fetchEmployeesBySortCriteria(
            sortField, nameOrEmail, departmentName, position, employeeNumber,
            hireDateFrom, hireDateTo, status, idAfter, lastSortValue, isDescending, pageable
        );

        // 5. 다음 페이지 존재 여부 확인
        boolean hasNext = employees.size() > size;
        if (hasNext) {
            employees = employees.subList(0, size); // 실제 반환할 데이터만 유지
        }

        // 6. Employee -> EmployeeDto 변환
        List<EmployeeDto> employeeDtos = employees.stream()
            .map(employeeMapper::toDto)
            .toList();

        // 7. 다음 커서 생성
        String nextCursor = null;
        Long nextIdAfter = null;
        if (hasNext && !employees.isEmpty()) {
            Employee lastEmployee = employees.get(employees.size() - 1);
            nextIdAfter = lastEmployee.getId();
            nextCursor = generateNextCursor(lastEmployee, sortField);
        }

        // 8. 총 개수 조회 (첫 페이지일 때만 조회하여 성능 최적화)
        Long totalElements = null;
        if (idAfter == null) { // 첫 페이지인 경우
            totalElements = employeeRepository.countEmployeesWithConditions(
                nameOrEmail, departmentName, position, employeeNumber,
                hireDateFrom, hireDateTo, status
            );
        }

        // 9. 응답 생성
        return new CursorPageResponseEmployeeDto(
            employeeDtos,
            nextCursor,
            nextIdAfter,
            size,
            totalElements,
            hasNext
        );
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
     * Cursor 디코딩 메서드
     */
    private CursorInfo decodeCursor(String cursor) {
        try {
            // Base64 디코딩
            byte[] decodedBytes = Base64.getDecoder().decode(cursor);
            String decodedJson = new String(decodedBytes, StandardCharsets.UTF_8);

            // JSON 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            @SuppressWarnings("unchecked")
            Map<String, Object> cursorMap = objectMapper.readValue(decodedJson, Map.class);

            Long lastId = Long.valueOf(cursorMap.get("id").toString());
            String sortBy = cursorMap.get("sortBy").toString();
            Object sortValue = cursorMap.get("sortValue");

            // sortValue 타입 변환
            Object typedSortValue = convertSortValue(sortValue, sortBy);

            return new CursorInfo(lastId, sortBy, typedSortValue);

        } catch (Exception e) {
            throw new InvalidCursorException("잘못된 커서 형식입니다: " + cursor);
        }
    }

    /**
     * Cursor 정보를 담는 내부 클래스
     */
    private record CursorInfo(Long idAfter, String sortField, Object sortValue) {}

    /**
     * sortValue를 적절한 타입으로 변환
     */
    private Object convertSortValue(Object sortValue, String sortBy) {
        if (sortValue == null) return null;

        try {
            return switch (sortBy) {
                case "name", "employeeNumber" -> sortValue.toString();
                case "hireDate" -> LocalDate.parse(sortValue.toString());
                default -> {
                    throw new InvalidRequestException("지원하지 않는 정렬 기준입니다: " + sortBy);
                }
            };
        } catch (Exception e) {
            throw new InvalidCursorException("커서의 정렬 값 변환에 실패했습니다.");
        }
    }

    /**
     * 정렬 기준에 따라 적절한 Repository 메서드를 호출 (lastSortValue를 직접 받도록 수정)
     */
    private List<Employee> fetchEmployeesBySortCriteria(
        String sortField, String nameOrEmail, String departmentName, String position,
        String employeeNumber, LocalDate hireDateFrom, LocalDate hireDateTo,
        EmployeeStatus status, Long idAfter, Object lastSortValue, boolean isDescending, Pageable pageable) {

        try {
            return switch (sortField) {
                case "name" -> employeeRepository.findEmployeesWithCursorByName(
                    nameOrEmail, departmentName, position, employeeNumber,
                    hireDateFrom, hireDateTo, status, idAfter, (String) lastSortValue,
                    isDescending, pageable
                );
                case "hireDate" -> employeeRepository.findEmployeesWithCursorByHireDate(
                    nameOrEmail, departmentName, position, employeeNumber,
                    hireDateFrom, hireDateTo, status, idAfter, (LocalDate) lastSortValue,
                    isDescending, pageable
                );
                case "employeeNumber" -> employeeRepository.findEmployeesWithCursorByEmployeeNumber(
                    nameOrEmail, departmentName, position, employeeNumber,
                    hireDateFrom, hireDateTo, status, idAfter, (String) lastSortValue,
                    isDescending, pageable
                );

                default -> throw new InvalidRequestException("지원하지 않는 정렬 기준입니다: " + sortField);
            };
        } catch (Exception e) {
            throw new RuntimeException("직원 목록 조회 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 이전 페이지의 마지막 정렬 값을 추출
     */
    private Object extractLastSortValue(Long idAfter, String sortField) {
        if (idAfter == null) {
            return null;
        }

        // 이전 페이지 마지막 직원 정보 조회
        Optional<Employee> lastEmployee = employeeRepository.findById(idAfter);
        if (lastEmployee.isEmpty()) {
            return null;
        }

        Employee employee = lastEmployee.get();
        return switch (sortField) {
            case "name" -> employee.getName();
            case "hireDate" -> employee.getHireDate();
            case "employeeNumber" -> employee.getEmployeeNumber();
            default -> null;
        };
    }

    /**
     * 다음 페이지를 위한 커서 생성
     * 커서는 마지막 요소의 정렬 값과 ID를 Base64로 인코딩한 문자열
     */
    private String generateNextCursor(Employee lastEmployee, String sortField) {
        try {
            // 커서 정보를 담을 Map 생성
            var cursorInfo = new java.util.HashMap<String, Object>();
            cursorInfo.put("id", lastEmployee.getId());
            cursorInfo.put("sortField", sortField);

            // 정렬 기준에 따라 값 추가
            switch (sortField) {
                case "name" -> cursorInfo.put("sortValue", lastEmployee.getName());
                case "hireDate" -> cursorInfo.put("sortValue", lastEmployee.getHireDate().toString());
                case "employeeNumber" -> cursorInfo.put("sortValue", lastEmployee.getEmployeeNumber());
                default -> throw new InvalidRequestException("지원하지 않는 정렬 기준입니다: " + sortField);
            }

            // JSON으로 직렬화 후 Base64 인코딩
            String json = objectMapper.writeValueAsString(cursorInfo);
            return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));

        } catch (Exception e) {
            throw new RuntimeException("커서 생성 실패", e);
        }
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
}
