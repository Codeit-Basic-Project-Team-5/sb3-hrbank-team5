package com.ohgiraffers.hrbank.repository;

import com.ohgiraffers.hrbank.dto.data.EmployeeSearchCondition;
import com.ohgiraffers.hrbank.entity.Employee;
import com.ohgiraffers.hrbank.entity.EmployeeStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class EmployeeRepositoryCustomImpl implements EmployeeRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 조건에 따른 직원 검색
     *
     * @param condition 검색 조건
     * @return 검색된 직원 목록
     */
    @Override
    public List<Employee> searchEmployees(EmployeeSearchCondition condition) {

        // Criteria Query 초기 설정
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Employee> query = criteriaBuilder.createQuery(Employee.class);
        Root<Employee> employeeRoot = query.from(Employee.class);

        // Where 조건 생성
        List<Predicate> wherePredicates = buildWherePredicates(criteriaBuilder, employeeRoot, condition);

        // 정렬 및 커서 조건 적용
        applySortingAndCursor(criteriaBuilder, query, employeeRoot, condition);

        // Where 조건 적용
        if (!wherePredicates.isEmpty()) {
            query.where(wherePredicates.toArray(new Predicate[0]));
        }

        // 쿼리 실행
        TypedQuery<Employee> typedQuery = entityManager.createQuery(query)
            .setMaxResults(condition.pageable().getPageSize());

        return typedQuery.getResultList();
    }

    // =================================================================
    // Where 조건 생성 메서드들
    // =================================================================

    /**
     * 모든 Where 조건을 생성
     */
    private List<Predicate> buildWherePredicates(
        CriteriaBuilder cb,
        Root<Employee> employee,
        EmployeeSearchCondition condition) {

        List<Predicate> predicates = new ArrayList<>();

        // 각 검색 조건 추가
        addNameOrEmailCondition(cb, employee, condition, predicates);
        addEmployeeNumberCondition(cb, employee, condition, predicates);
        addDepartmentNameCondition(cb, employee, condition, predicates);
        addPositionCondition(cb, employee, condition, predicates);
        addHireDateRangeCondition(cb, employee, condition, predicates);
        addStatusCondition(cb, employee, condition, predicates);

        return predicates;
    }

    /**
     * 이름 또는 이메일 검색 조건
     */
    private void addNameOrEmailCondition(
        CriteriaBuilder cb,
        Root<Employee> employee,
        EmployeeSearchCondition condition,
        List<Predicate> predicates) {

        if (condition.nameOrEmail() != null && !condition.nameOrEmail().trim().isEmpty()) {
            String searchPattern = createLikePattern(condition.nameOrEmail());

            Predicate nameLike = cb.like(employee.get("name"), searchPattern);
            Predicate emailLike = cb.like(employee.get("email"), searchPattern);

            predicates.add(cb.or(nameLike, emailLike));
        }
    }

    /**
     * 사원번호 검색 조건
     */
    private void addEmployeeNumberCondition(
        CriteriaBuilder cb,
        Root<Employee> employee,
        EmployeeSearchCondition condition,
        List<Predicate> predicates) {

        if (condition.employeeNumber() != null && !condition.employeeNumber().trim().isEmpty()) {
            String searchPattern = createLikePattern(condition.employeeNumber());
            predicates.add(cb.like(employee.get("employeeNumber"), searchPattern));
        }
    }

    /**
     * 부서명 검색 조건
     */
    private void addDepartmentNameCondition(
        CriteriaBuilder cb,
        Root<Employee> employee,
        EmployeeSearchCondition condition,
        List<Predicate> predicates) {

        if (condition.departmentName() != null && !condition.departmentName().trim().isEmpty()) {
            String searchPattern = createLikePattern(condition.departmentName());
            predicates.add(cb.like(employee.get("department").get("name"), searchPattern));
        }
    }

    /**
     * 직책 검색 조건
     */
    private void addPositionCondition(
        CriteriaBuilder cb,
        Root<Employee> employee,
        EmployeeSearchCondition condition,
        List<Predicate> predicates) {

        if (condition.position() != null && !condition.position().trim().isEmpty()) {
            String searchPattern = createLikePattern(condition.position());
            predicates.add(cb.like(employee.get("position"), searchPattern));
        }
    }

    /**
     * 입사일 범위 검색 조건
     */
    private void addHireDateRangeCondition(
        CriteriaBuilder cb,
        Root<Employee> employee,
        EmployeeSearchCondition condition,
        List<Predicate> predicates) {

        // 입사일 시작 조건
        if (condition.hireDateFrom() != null) {
            predicates.add(cb.greaterThanOrEqualTo(
                employee.get("hireDate"), condition.hireDateFrom()));
        }

        // 입사일 종료 조건
        if (condition.hireDateTo() != null) {
            predicates.add(cb.lessThanOrEqualTo(
                employee.get("hireDate"), condition.hireDateTo()));
        }
    }

    /**
     * 상태 검색 조건
     */
    private void addStatusCondition(
        CriteriaBuilder cb,
        Root<Employee> employee,
        EmployeeSearchCondition condition,
        List<Predicate> predicates) {

        if (condition.status() != null) {
            predicates.add(cb.equal(employee.get("status"), condition.status()));
        }
    }

    // =================================================================
    // 정렬 및 커서 처리 메서드들
    // =================================================================

    /**
     * 정렬 및 커서 조건 적용
     */
    private void applySortingAndCursor(
        CriteriaBuilder cb,
        CriteriaQuery<Employee> query,
        Root<Employee> employee,
        EmployeeSearchCondition condition) {

        String sortDirection = condition.sortDirection();
        String sortField = condition.sortField();

        if ("asc".equalsIgnoreCase(sortDirection)) {
            handleAscendingSort(cb, query, employee, condition);
        } else if ("desc".equalsIgnoreCase(sortDirection)) {
            handleDescendingSort(cb, query, employee, condition);
        } else {
            // 기본 정렬: 이름 기준 오름차순
            query.orderBy(cb.asc(employee.get("name")));
        }
    }

    /**
     * 오름차순 정렬 및 커서 처리
     */
    private void handleAscendingSort(
        CriteriaBuilder cb,
        CriteriaQuery<Employee> query,
        Root<Employee> employee,
        EmployeeSearchCondition condition) {

        String sortField = condition.sortField();

        // 커서 조건이 있는 경우
        if (hasCursorCondition(condition)) {
            List<Predicate> cursorPredicates = buildAscendingCursorPredicates(cb, employee, condition);
            if (!cursorPredicates.isEmpty()) {
                query.where(cursorPredicates.toArray(new Predicate[0]));
            }
        }

        // 정렬 적용
        query.orderBy(
            cb.asc(employee.get(sortField)),
            cb.asc(employee.get("id")) // 동일한 값일 때 ID로 2차 정렬
        );
    }

    /**
     * 내림차순 정렬 및 커서 처리
     */
    private void handleDescendingSort(
        CriteriaBuilder cb,
        CriteriaQuery<Employee> query,
        Root<Employee> employee,
        EmployeeSearchCondition condition) {

        String sortField = condition.sortField();

        // 커서 조건이 있는 경우
        if (hasCursorCondition(condition)) {
            List<Predicate> cursorPredicates = buildDescendingCursorPredicates(cb, employee, condition);
            if (!cursorPredicates.isEmpty()) {
                query.where(cursorPredicates.toArray(new Predicate[0]));
            }
        }

        // 정렬 적용
        query.orderBy(
            cb.desc(employee.get(sortField)),
            cb.desc(employee.get("id")) // 동일한 값일 때 ID로 2차 정렬
        );
    }

    /**
     * 커서 조건이 있는지 확인
     */
    private boolean hasCursorCondition(EmployeeSearchCondition condition) {
        return condition.idAfter() != null &&
            condition.cursor() != null &&
            !condition.cursor().trim().isEmpty();
    }

    /**
     * 오름차순 커서 조건 생성
     */
    private List<Predicate> buildAscendingCursorPredicates(
        CriteriaBuilder cb,
        Root<Employee> employee,
        EmployeeSearchCondition condition) {

        List<Predicate> predicates = new ArrayList<>();
        String sortField = condition.sortField();

        if ("hireDate".equals(sortField)) {
            LocalDate cursorDate = LocalDate.parse(condition.cursor());
            predicates.add(createDateCursorPredicate(cb, employee, cursorDate, condition.idAfter(), true));
        } else {
            predicates.add(createStringCursorPredicate(cb, employee, sortField, condition.cursor(), condition.idAfter(), true));
        }

        return predicates;
    }

    /**
     * 내림차순 커서 조건 생성
     */
    private List<Predicate> buildDescendingCursorPredicates(
        CriteriaBuilder cb,
        Root<Employee> employee,
        EmployeeSearchCondition condition) {

        List<Predicate> predicates = new ArrayList<>();
        String sortField = condition.sortField();

        if ("hireDate".equals(sortField)) {
            LocalDate cursorDate = LocalDate.parse(condition.cursor());
            predicates.add(createDateCursorPredicate(cb, employee, cursorDate, condition.idAfter(), false));
        } else {
            predicates.add(createStringCursorPredicate(cb, employee, sortField, condition.cursor(), condition.idAfter(), false));
        }

        return predicates;
    }

    /**
     * 검색 조건에 맞는 총 직원 수 조회 (페이지네이션 정보용)
     * @Query 대신 Criteria API로 구현하여 안정성 확보
     */
    @Override
    public Long countEmployeesWithConditions(
        String nameOrEmail,
        String departmentName,
        String position,
        String employeeNumber,
        LocalDate hireDateFrom,
        LocalDate hireDateTo,
        EmployeeStatus status) {

        // Count 쿼리를 위한 Criteria 설정
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<Employee> employeeRoot = countQuery.from(Employee.class);

        // COUNT 함수 적용
        countQuery.select(criteriaBuilder.count(employeeRoot));

        // 검색 조건 생성 (searchEmployees와 동일한 로직)
        List<Predicate> wherePredicates = buildCountWherePredicates(
            criteriaBuilder, employeeRoot,
            nameOrEmail, departmentName, position, employeeNumber,
            hireDateFrom, hireDateTo, status
        );

        // Where 조건 적용
        if (!wherePredicates.isEmpty()) {
            countQuery.where(wherePredicates.toArray(new Predicate[0]));
        }

        // 쿼리 실행하여 개수 반환
        return entityManager.createQuery(countQuery).getSingleResult();
    }

    /**
     * Count 쿼리용 Where 조건 생성
     * searchEmployees의 조건과 동일하게 유지
     */
    private List<Predicate> buildCountWherePredicates(
        CriteriaBuilder cb,
        Root<Employee> employee,
        String nameOrEmail,
        String departmentName,
        String position,
        String employeeNumber,
        LocalDate hireDateFrom,
        LocalDate hireDateTo,
        EmployeeStatus status) {

        List<Predicate> predicates = new ArrayList<>();

        // 이름 또는 이메일 검색 조건
        addNameOrEmailCondition(cb, employee, nameOrEmail, predicates);

        // 사원번호 검색 조건
        addEmployeeNumberCondition(cb, employee, employeeNumber, predicates);

        // 부서명 검색 조건
        addDepartmentNameCondition(cb, employee, departmentName, predicates);

        // 직책 검색 조건
        addPositionCondition(cb, employee, position, predicates);

        // 입사일 범위 검색 조건
        addHireDateRangeCondition(cb, employee, hireDateFrom, hireDateTo, predicates);

        // 상태 검색 조건
        addStatusCondition(cb, employee, status, predicates);

        return predicates;
    }

    // =================================================================
    // Count 쿼리용 조건 생성 메서드들 (기존 메서드와 동일한 로직)
    // =================================================================

    /**
     * 이름 또는 이메일 검색 조건 (Count용)
     */
    private void addNameOrEmailCondition(
        CriteriaBuilder cb,
        Root<Employee> employee,
        String nameOrEmail,
        List<Predicate> predicates) {

        if (nameOrEmail != null && !nameOrEmail.trim().isEmpty()) {
            String searchPattern = createLikePattern(nameOrEmail);

            Predicate nameLike = cb.like(
                cb.lower(employee.get("name")),
                searchPattern.toLowerCase()
            );
            Predicate emailLike = cb.like(
                cb.lower(employee.get("email")),
                searchPattern.toLowerCase()
            );

            predicates.add(cb.or(nameLike, emailLike));
        }
    }

    /**
     * 사원번호 검색 조건 (Count용)
     */
    private void addEmployeeNumberCondition(
        CriteriaBuilder cb,
        Root<Employee> employee,
        String employeeNumber,
        List<Predicate> predicates) {

        if (employeeNumber != null && !employeeNumber.trim().isEmpty()) {
            String searchPattern = createLikePattern(employeeNumber);
            predicates.add(cb.like(
                cb.lower(employee.get("employeeNumber")),
                searchPattern.toLowerCase()
            ));
        }
    }

    /**
     * 부서명 검색 조건 (Count용)
     */
    private void addDepartmentNameCondition(
        CriteriaBuilder cb,
        Root<Employee> employee,
        String departmentName,
        List<Predicate> predicates) {

        if (departmentName != null && !departmentName.trim().isEmpty()) {
            String searchPattern = createLikePattern(departmentName);
            predicates.add(cb.like(
                cb.lower(employee.get("department").get("name")),
                searchPattern.toLowerCase()
            ));
        }
    }

    /**
     * 직책 검색 조건 (Count용)
     */
    private void addPositionCondition(
        CriteriaBuilder cb,
        Root<Employee> employee,
        String position,
        List<Predicate> predicates) {

        if (position != null && !position.trim().isEmpty()) {
            String searchPattern = createLikePattern(position);
            predicates.add(cb.like(
                cb.lower(employee.get("position")),
                searchPattern.toLowerCase()
            ));
        }
    }

    /**
     * 입사일 범위 검색 조건 (Count용)
     */
    private void addHireDateRangeCondition(
        CriteriaBuilder cb,
        Root<Employee> employee,
        LocalDate hireDateFrom,
        LocalDate hireDateTo,
        List<Predicate> predicates) {

        // 입사일 시작 조건
        if (hireDateFrom != null) {
            predicates.add(cb.greaterThanOrEqualTo(
                employee.get("hireDate"), hireDateFrom));
        }

        // 입사일 종료 조건
        if (hireDateTo != null) {
            predicates.add(cb.lessThanOrEqualTo(
                employee.get("hireDate"), hireDateTo));
        }
    }

    /**
     * 상태 검색 조건 (Count용)
     */
    private void addStatusCondition(
        CriteriaBuilder cb,
        Root<Employee> employee,
        EmployeeStatus status,
        List<Predicate> predicates) {

        if (status != null) {
            predicates.add(cb.equal(employee.get("status"), status));
        }
    }

    // =================================================================
    // 유틸리티 메서드들
    // =================================================================

    /**
     * LIKE 패턴 생성 (%value%)
     */
    private String createLikePattern(String value) {
        return "%" + value.trim() + "%";
    }

    /**
     * 날짜 필드 커서 조건 생성
     */
    private Predicate createDateCursorPredicate(
        CriteriaBuilder cb,
        Root<Employee> employee,
        LocalDate cursorDate,
        Long idAfter,
        boolean ascending) {

        Path<LocalDate> datePath = employee.get("hireDate");
        Path<Long> idPath = employee.get("id");

        if (ascending) {
            return cb.or(
                cb.greaterThan(datePath, cursorDate),
                cb.and(
                    cb.equal(datePath, cursorDate),
                    cb.greaterThan(idPath, idAfter)
                )
            );
        } else {
            return cb.or(
                cb.lessThan(datePath, cursorDate),
                cb.and(
                    cb.equal(datePath, cursorDate),
                    cb.lessThan(idPath, idAfter)
                )
            );
        }
    }

    /**
     * 문자열 필드 커서 조건 생성
     */
    private Predicate createStringCursorPredicate(
        CriteriaBuilder cb,
        Root<Employee> employee,
        String sortField,
        String cursor,
        Long idAfter,
        boolean ascending) {

        Path<String> fieldPath = employee.get(sortField);
        Path<Long> idPath = employee.get("id");

        if (ascending) {
            return cb.or(
                cb.greaterThan(fieldPath, cursor),
                cb.and(
                    cb.equal(fieldPath, cursor),
                    cb.greaterThan(idPath, idAfter)
                )
            );
        } else {
            return cb.or(
                cb.lessThan(fieldPath, cursor),
                cb.and(
                    cb.equal(fieldPath, cursor),
                    cb.lessThan(idPath, idAfter)
                )
            );
        }
    }
}

