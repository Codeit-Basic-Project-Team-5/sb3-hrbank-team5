package com.ohgiraffers.hrbank.service.basic;

import com.ohgiraffers.hrbank.dto.data.DepartmentDto;
import com.ohgiraffers.hrbank.dto.request.DepartmentCreateRequest;
import com.ohgiraffers.hrbank.dto.request.DepartmentUpdateRequest;
import com.ohgiraffers.hrbank.dto.response.DepartmentPageResponse;
import com.ohgiraffers.hrbank.entity.Department;
import com.ohgiraffers.hrbank.repository.DepartmentRepository;
import com.ohgiraffers.hrbank.service.DepartmentService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicDepartmentService implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    /** 부서 생성
     * 입력 : JSON request
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
     * 입력 : JSON request
     * 부서 id, DepartmentUpdateRequest(name, description, establishedDate)
     * 출력 :
     * 수정된 Department
     */
    @Override
    public Department update(Long id,DepartmentUpdateRequest request) {
        if(!departmentRepository.existsById(id)){
            throw new IllegalArgumentException("존재하지 않는 부서입니다.");
        }
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


    /** 부서 정보 정렬조회
     * 입력 : 이름or설명,마지막조회된부서id,커서위치,정렬기준,정렬방향
     * 부서 id
     */
    @Override
    public DepartmentPageResponse<DepartmentDto> findAllSorted(
        String nameOrDescription,
        Long idAfter,
        String cursor,
        String sortField,
        String sortDirection,
        int size) {

        Pageable pageable = PageRequest.of(0 , size+1 , Sort.by(Direction.fromString(sortDirection), sortField));

        //검색어 없을 시 빈 문자열 처리
        String keyword = (nameOrDescription == null || nameOrDescription.isBlank()) ? "" :nameOrDescription;

        // JPQL 커서 쿼리 실행
        List<Department> departments = departmentRepository.findByCursor(
            keyword.isEmpty() ? null : keyword,
            idAfter,
            pageable
        );

        long totalElements;
        if (keyword.isEmpty()) {
            totalElements = departmentRepository.count();
        } else {
            totalElements = departmentRepository.countByNameOrDescription(keyword);
        }
        // 다음 페이지 유무 연산
        boolean hasNextPage = departments.size() > size;
        if (hasNextPage){
            departments = departments.subList(0,size);
        }

        // DTO로 변환
        List<DepartmentDto> dtoList = departments.stream().map(DepartmentDto::fromEntity).toList();

        // 마지막 요소의 id값
        Long nextIdAfter = (!dtoList.isEmpty()) ? dtoList.get(dtoList.size() - 1).id() : null;

        // 마지막 요소의 cursor값
        // ★ nextCursor: 마지막 요소의 sortField 값
        String nextCursor = (!dtoList.isEmpty())
            ? extractSortFieldValue(dtoList.get(dtoList.size() - 1), sortField)
            : null;

        return new DepartmentPageResponse<>(
            dtoList,
            cursor, // 커서
            nextIdAfter,
            size,
            totalElements,
            hasNextPage
        );
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

    /** 지정된 sortField값 선택 추출 메서드
     */
    private String extractSortFieldValue(DepartmentDto dto, String sortField) {
        switch (sortField) {
            case "name": return dto.name();
            case "description": return dto.description();
            case "establishedDate": return dto.establishedDate().toString();
            // 기본값 null반환
            default: return null;
        }
    }
}
