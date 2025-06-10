package com.ohgiraffers.hrbank.dto.request;

import com.ohgiraffers.hrbank.entity.EmployeeStatus;
import java.time.LocalDate;

/**
 * 직원 목록 조회 검색 조건을 담는 Request DTO
 *
 * 검색 조건들:
 * - nameOrEmail: 이름 또는 이메일 부분 일치 검색
 * - departmentName: 부서명으로 부분 일치 검색
 * - position: 직함 부분 일치 검색
 * - employeeNumber: 사원번호 부분 일치 검색
 * - hireDateFrom, hireDateTo: 입사일 범위 검색
 * - status: 직원 상태 완전 일치 검색
 */
public record EmployeeSearchRequest(
    String nameOrEmail,        // 이름 또는 이메일 (부분 일치)
    String departmentName,       // 부서명 (부분 일치)
    String position,           // 직함 (부분 일치)
    String employeeNumber,     // 사원번호 (부분 일치)
    LocalDate hireDateFrom,    // 입사일 시작 범위
    LocalDate hireDateTo,      // 입사일 끝 범위
    EmployeeStatus status,     // 직원 상태 (완전 일치)

    // 정렬 관련
    String sortField,             // 정렬 기준: "name", "hireDate", "employeeNumber"
    String sortDirection,      // 정렬 방향: "asc", "desc"

    // 페이지네이션 관련
    Long idAfter,               // 이전 페이지의 마지막 요소 ID (커서)
    String cursor,              // 커서 (다음 페이지 시작점)
    Integer size               // 한 페이지 크기 (기본값 20)
) {

    /**
     * 기본값 설정을 위한 생성자
     */
    public EmployeeSearchRequest {
        // size가 null이거나 0 이하면 기본값 30 설정
        if (size == null || size <= 0) {
            size = 30;
        }

        // sortField가 null이면 기본값 "name" 설정
        if (sortField == null || sortField.trim().isEmpty()) {
            sortField = "name";
        }

        // sortDirection이 null이면 기본값 "asc" 설정
        if (sortDirection == null || sortDirection.trim().isEmpty()) {
            sortDirection = "asc";
        }
    }

    /**
     * 정렬 방향이 내림차순인지 확인
     */
    public boolean isDescending() {
        return "desc".equalsIgnoreCase(sortDirection);
    }

    /**
     * 검색 조건이 있는지 확인
     */
    public boolean hasSearchCondition() {
        return nameOrEmail != null ||
            departmentName != null ||
            position != null ||
            employeeNumber != null ||
            hireDateFrom != null ||
            hireDateTo != null ||
            status != null;
    }

    /**
     * 부서 검색 조건이 있는지 확인 (ID 또는 이름)
     */
    public boolean hasDepartmentCondition() {
        return departmentName != null && !departmentName.trim().isEmpty();
    }
}
