package com.ohgiraffers.hrbank.service.basic;

import com.ohgiraffers.hrbank.dto.data.EmployeeDto;
import com.ohgiraffers.hrbank.dto.request.EmployeeCreateRequest;
import com.ohgiraffers.hrbank.dto.request.EmployeeUpdateRequest;
import com.ohgiraffers.hrbank.dto.request.FileCreateRequest;
import com.ohgiraffers.hrbank.entity.Department;
import com.ohgiraffers.hrbank.entity.Employee;
import com.ohgiraffers.hrbank.entity.EmployeeStatus;
import com.ohgiraffers.hrbank.entity.File;
import com.ohgiraffers.hrbank.mapper.EmployeeMapper;
import com.ohgiraffers.hrbank.repository.DepartmentRepository;
import com.ohgiraffers.hrbank.repository.EmployeeRepository;
import com.ohgiraffers.hrbank.repository.FileRepository;
import com.ohgiraffers.hrbank.service.EmployeeService;
import com.ohgiraffers.hrbank.storage.FileStorage;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    @Override
    public EmployeeDto create(EmployeeCreateRequest employeeCreateRequest,
        Optional<FileCreateRequest> optionalFileCreateRequest) {
        Department department = departmentRepository.findDepartmentById(employeeCreateRequest.departmentId());

        String memo = employeeCreateRequest.memo();

        File nullableProfile = optionalFileCreateRequest
            .map(profileRequest -> {
                String fileName = profileRequest.fileName();
                String contentType = profileRequest.contentType();
                byte[] bytes = profileRequest.bytes();

                // 파일 확장자 추출
                String extension = extractExtension(fileName);

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
            })
            .orElse(null);

        Employee employee = new Employee(
            employeeCreateRequest.name(),
            employeeCreateRequest.email(),
            generateEmployeeNumber(),   // 사원번호 자동 생성
            department,
            employeeCreateRequest.position(),
            employeeCreateRequest.hireDate(),
            nullableProfile
        );
       employeeRepository.save(employee);

        return employeeMapper.toDto(employee);
    }

    @Override
    public EmployeeDto update(Long employeeId, EmployeeUpdateRequest employeeUpdateRequest,
        Optional<FileCreateRequest> optionalFileCreateRequest) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new NoSuchElementException("Employee with id " + employeeId + " not found"));

        String newName = employeeUpdateRequest.name();
        String newEmail = employeeUpdateRequest.email();
        if (employeeRepository.existsByEmail(newEmail) && !employee.getEmail().equals(newEmail)) {
            throw new IllegalArgumentException("Employee with email " + newEmail + " already exists");
        }

        Department newDepartment = departmentRepository.findDepartmentById(employeeUpdateRequest.departmentId());
        String newPosition = employeeUpdateRequest.position();
        LocalDate newHireDate = employeeUpdateRequest.hireDate();
        EmployeeStatus newStatus = EmployeeStatus.valueOf(employeeUpdateRequest.status());

        File nullableProfile = optionalFileCreateRequest
            .map(profileRequest -> {
                String fileName = profileRequest.fileName();
                String contentType = profileRequest.contentType();
                byte[] bytes = profileRequest.bytes();

                // 파일 확장자 추출
                String extension = extractExtension(fileName);

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
            })
            .orElse(null);

        String newMemo = employeeUpdateRequest.memo();

        employee.update(newName, newEmail, newDepartment, newPosition, newHireDate, newStatus, nullableProfile);
        return employeeMapper.toDto(employee);
    }

    @Override
    public void delete(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new NoSuchElementException("Employee with id " + employeeId + " not found"));

        // 프로필 이미지 정보를 미리 저장 (Employee 삭제 전에)
        File profileImageToDelete = employee.getProfileImage();

        // 1. 먼저 Employee 삭제 (외래키 제약 조건 해결)
        employeeRepository.deleteById(employeeId);

        // 2. 그 다음에 프로필 이미지 삭제 (있는 경우에만)
        if (profileImageToDelete != null) {
            // 실제 파일 삭제 처리
            deletePhysicalFile(profileImageToDelete);

            // File 엔티티 삭제
            fileRepository.deleteById(profileImageToDelete.getId());
        }
    }

    @Override
    public EmployeeDto find(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new NoSuchElementException("Employee with id " + employeeId + " not found"));

        return employeeMapper.toDto(employee);
    }

    /**
     * 사원번호 자동 생성 (예: EMP-2025-001)
     */
    private String generateEmployeeNumber() {
        // 현재 연도 기준으로 생성
        int year = LocalDate.now().getYear();

        // 해당 연도의 기존 직원 수 + 1
        long count = employeeRepository.count();
        String sequence = String.format("%03d", count + 1);

        return String.format("EMP-%d-%s", year, sequence);
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
                System.out.println("프로필 이미지 파일 삭제 완료: " + filePath);
            } else {
                System.out.println("삭제할 파일이 존재하지 않음: " + filePath);
            }

        } catch (Exception e) {
            // 파일 삭제 실패해도 직원 삭제는 계속 진행
            System.err.println("프로필 이미지 파일 삭제 실패: " + e.getMessage());
            // 필요시 로깅 프레임워크 사용: log.error("프로필 이미지 삭제 실패", e);
        }
    }
}
