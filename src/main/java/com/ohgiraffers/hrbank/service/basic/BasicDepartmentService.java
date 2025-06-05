package com.ohgiraffers.hrbank.service.basic;

import com.ohgiraffers.hrbank.dto.data.DepartmentDto;
import com.ohgiraffers.hrbank.dto.request.DepartmentCreateRequest;
import com.ohgiraffers.hrbank.dto.request.DepartmentUpdateRequest;
import com.ohgiraffers.hrbank.entity.Department;
import com.ohgiraffers.hrbank.repository.DepartmentRepository;
import com.ohgiraffers.hrbank.service.DepartmentService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicDepartmentService implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    /** 부서 생성
     * 입력 :
     * DepartmentCreateRequest(name, description, establishedDate)
     * 출력 :
     * 생성된 Department
     */
    @Override
    public Department create(DepartmentCreateRequest request) {

        String name = request.name();
        if (departmentRepository.existsByName(name)){
            throw new IllegalArgumentException("이미 존재하는 이름입니다.");
        }
        String description = request.description();
        LocalDate established_date = request.establishedDate();

        Department department = new Department(name, description, established_date);

        return departmentRepository.save(department);
    }

    /** 전체 부서 목록 조회
     * 출력 :
     * DepartmentDto 리스트
     */
    @Override
    public List<DepartmentDto> findAll() {
        return departmentRepository.findAll()
            .stream()
            .map(this::toDto)
            .toList();
    }

    /** 부서 상세 조회
     * 출력 :
     * DepartmentDto
     */
    @Override
    public DepartmentDto findById(Long id) {
        return this.toDto(departmentRepository.findDepartmentById(id));
    }

    /** 부서 정보 수정
     * 입력 :
     * 부서 id, DepartmentUpdateRequest(name, description, establishedDate)
     * 출력 :
     * 수정된 Department
     */
    @Override
    public Department update(DepartmentUpdateRequest request) {
        if(!departmentRepository.existsById(request.id())){
            throw new IllegalArgumentException("존재하지 않는 부서입니다.");
        }
        Long id = request.id();
        String name = request.name();
        if (departmentRepository.existsByName(name)){
            throw new IllegalArgumentException("이미 존재하는 이름입니다.");
        }
        Department department = departmentRepository.findDepartmentById(id);

        department.update(name,request.description(),request.establishedDate());

        return departmentRepository.save(department);
    }
    /** 부서 정보 삭제
     * 입력 :
     * 부서 id
     */
    @Override
    public void delete(Long id) {
        if (!departmentRepository.existsById(id)){
            throw new IllegalArgumentException("존재하지 않는 Department입니다.");
        }
        Department department = departmentRepository.findDepartmentById(id);
        departmentRepository.delete(department);
    }

    /** Dto 변환 메서드
     * 입력 :
     * Department
     * 출력 :
     * Dto 객체
     */
    private DepartmentDto toDto(Department department){
        return new DepartmentDto(
            department.getId(),
            department.getName(),
            department.getDescription(),
            department.getEstablishedDate(),
            10L                                 // 직원정보 연결 전 임시 value
        );
    }
}
