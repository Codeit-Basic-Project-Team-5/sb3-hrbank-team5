package com.ohgiraffers.hrbank.service.basic;

import com.ohgiraffers.hrbank.dto.data.EmployeeDistributionDto;
import com.ohgiraffers.hrbank.dto.data.EmployeeTrendDto;
import com.ohgiraffers.hrbank.entity.EmployeeStatus;
import com.ohgiraffers.hrbank.exception.InvalidDateRangeException;
import com.ohgiraffers.hrbank.exception.UnsupportedUnitException;
import com.ohgiraffers.hrbank.repository.ChangeLogRepository;
import com.ohgiraffers.hrbank.repository.EmployeeRepository;
import com.ohgiraffers.hrbank.service.DashBoardService;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BasicDashBoardService implements DashBoardService {
    private final EmployeeRepository employeeRepository;
    private final ChangeLogRepository changeLogRepository;

    /**
     * 총 직원 수 조회
     */
    public long countAllEmployees() {
        return employeeRepository.count();
    }

    /**
     * 특정 상태의 직원 수 조회
     *
     * @param status 조회할 직원 상태 (ACTIVE, ON_LEAVE, RESIGNED)
     * @return 해당 상태의 직원 수 (long)
     */
    public long countByStatus(EmployeeStatus status) {
        return employeeRepository.countByStatus(status);
    }

    /**
     * 이번 달 입사한 직원 수 조회
     * 지정된 기간에 입사한, 특정 상태의 직원 수 조회
     *
     * @param status 조회할 직원 상태
     * @param from   조회 시작일
     * @param to     조회 종료일
     * @return 해당 조건에 맞는 직원 수 (long)
     * @throws InvalidDateRangeException from이 to보다 이후인 경우
     */
    public long countHiredBetween(EmployeeStatus status, LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new InvalidDateRangeException(from, to);
        }
        return employeeRepository.countByStatusAndHireDateBetween(status, from, to);
    }

    /**
     * 최근 업데이트 수정 건수 조회 (1주일 기준)
     * 지정된 기간 동안 변경 로그의 총 개수를 조회
     * <p>
     * 주어진 시작일(from)의 00:00:00부터
     * 종료일(to)의 23:59:59.999(UTC 기준)까지의 범위를 Instant로 변환하여 조회
     *
     * @param from 조회 시작일
     * @param to   조회 종료일
     * @return 지정된 기간 동안 변경된 로그 수 (long)
     * @throws InvalidDateRangeException from이 to보다 이후인 경우
     */
    public long countUpdatesBetween(LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new InvalidDateRangeException(from, to);
        }

        Instant start = from.atStartOfDay(ZoneId.of("UTC")).toInstant();
        Instant end = to.atTime(LocalTime.MAX).atZone(ZoneId.of("UTC")).toInstant();
        return changeLogRepository.countByUpdatedAtBetween(start, end);
    }

    /**
     * 지정된 기준(groupBy)과 상태(status)에 따라 직원 분포 정보를 조회
     * groupBy가 "department"이면 부서별, "position"이면 직무 별 분포 계산
     *
     * @param groupBy (department/position)
     * @param status  조회할 직원의 상태 (ACTIVE, ON_LEAVE, RESIGNED)
     * @return 각 그룹 별 직원 수와 전체 대비 백분율을 담은 EmployeeDistributionDto 리스트
     */
    public List<EmployeeDistributionDto> getDistribution(String groupBy, EmployeeStatus status) {
        List<Object[]> results = groupBy.equals("department")
            ? employeeRepository.countByDepartment(status)
            : employeeRepository.countByPosition(status);

        long total = results.stream()
            .mapToLong(row -> ((Number) row[1]).longValue())
            .sum();

        return results.stream()
            .map(row -> {
                String groupKey = (String) row[0];
                long count = ((Number) row[1]).longValue();
                double percentage = total == 0 ? 0.0 : (count * 100.0) / total;
                return new EmployeeDistributionDto(groupKey, count, percentage);
            })
            .toList();
    }

    /**
     * 지정된 기간과 단위에 따라 직원 수 추이 계산
     * unit("day", "week", "month", "quarter", "year")에 따라 주기별 집계 결과 생성
     *
     * @param from  조회 시작일 (to에서 unit 기준 12개 이전 날짜가 기본값)
     * @param to    조회 종료일 (오늘-1일이 기본값)
     * @param unit  집계 단위 ("day", "week", "month", "quarter", "year")
     * @return 각 기간별 종료 날짜, 직원 수, 이전 대비 변화량, 변화율을 담은 EmployeeTrendDto 리스트
     */
    public List<EmployeeTrendDto> getTrend(LocalDate from, LocalDate to, String unit) {
        List<EmployeeTrendDto> trends = new ArrayList<>();
        ChronoUnit chronoUnit = parseUnit(unit);
        int unitSize = getUnitSize(unit);

        LocalDate endDate = (to != null) ? to : LocalDate.now().minusDays(1);
        LocalDate startDate = (from != null) ? from : endDate.minus( 12L * getUnitSize(unit), parseUnit(unit) );

        long prevCount = 0;
        while (!startDate.isAfter(endDate)) {
            LocalDate next = startDate.plus(unitSize, chronoUnit);
            LocalDate rangeEnd = next.minusDays(1);

            if (rangeEnd.isAfter(endDate)) {
                rangeEnd = endDate;
            }

            long count = employeeRepository.countWorkingUpToDate(rangeEnd);

            long change = trends.isEmpty() ? 0 : count - prevCount;
            double changeRate = prevCount == 0
                ? 0.0
                : ((double) change / prevCount) * 100;

            trends.add(new EmployeeTrendDto(rangeEnd, count, change, changeRate));

            prevCount = count;
            startDate = next;
        }

        return trends;
    }

    /**
     * 문자열 단위(unit)를 ChronoUnit 타입으로 변환
     *
     * @param unit ("day", "week", "month", "quarter", "year")
     * @return 해당하는 ChronoUnit (quarter는 MONTHS)
     * @throws UnsupportedUnitException 지원되지 않는 단위일 경우
     */
    private ChronoUnit parseUnit(String unit) {
        return switch (unit.toLowerCase()) {
            case "day"     -> ChronoUnit.DAYS;
            case "week"    -> ChronoUnit.WEEKS;
            case "month", "quarter" -> ChronoUnit.MONTHS;
            case "year"    -> ChronoUnit.YEARS;
            default -> throw new UnsupportedUnitException(unit);
        };
    }

    /**
     * 단위(unit)에 따른 구간 크기를 반환합니다.
     *
     * @param unit "quarter"이면 3, 그 외에는 1
     * @return 구간 크기
     */
    private int getUnitSize(String unit) {
        return "quarter".equalsIgnoreCase(unit) ? 3 : 1;
    }
}