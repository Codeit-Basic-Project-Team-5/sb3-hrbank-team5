package com.ohgiraffers.hrbank.dto.request;

import com.ohgiraffers.hrbank.entity.EmployeeStatus;
import java.time.LocalDate;

/**
 * 직원 목록 조회 검색 조건을 담는 Request DTO
 *
 * 검색 조건들:
 * - nameOrEmail: 이름 또는 이메일 부분 일치 검색
 * - departmentId: 부서 ID로 완전 일치 검색
 * - position: 직함 부분 일치 검색
 * - employeeNumber: 사원번호 부분 일치 검색
 * - hireDateFrom, hireDateTo: 입사일 범위 검색
 * - status: 직원 상태 완전 일치 검색
 */
public record EmployeeSearchRequest(
    String nameOrEmail,        // 이름 또는 이메일 (부분 일치)
    /* 부서 이름으로 변경 필요 */
    Long departmentId,         // 부서 ID (완전 일치)
    String position,           // 직함 (부분 일치)
    String employeeNumber,     // 사원번호 (부분 일치)
    LocalDate hireDateFrom,    // 입사일 시작 범위
    LocalDate hireDateTo,      // 입사일 끝 범위
    EmployeeStatus status,     // 직원 상태 (완전 일치)

    // 정렬 관련
    String sortBy,             // 정렬 기준: "name", "hireDate", "employeeNumber"
    String sortDirection,      // 정렬 방향: "asc", "desc"

    // 페이지네이션 관련
    Long lastId,               // 이전 페이지의 마지막 요소 ID (커서)
    Integer size               // 한 페이지 크기 (기본값 20)
) {

    /**
     * 기본값 설정을 위한 생성자
     */
    public EmployeeSearchRequest {
        // size가 null이거나 0 이하면 기본값 20 설정
        if (size == null || size <= 0) {
            size = 20;
        }

        // sortBy가 null이면 기본값 "name" 설정
        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = "name";
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
            departmentId != null ||
            position != null ||
            employeeNumber != null ||
            hireDateFrom != null ||
            hireDateTo != null ||
            status != null;
    }
}
