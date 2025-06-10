package com.ohgiraffers.hrbank.service.basic;

import com.ohgiraffers.hrbank.entity.EmployeeStatus;
import com.ohgiraffers.hrbank.repository.ChangeLogRepository;
import com.ohgiraffers.hrbank.repository.EmployeeRepository;
import com.ohgiraffers.hrbank.service.DashBoardService;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BasicDashBoardService implements DashBoardService {
    private final EmployeeRepository employeeRepository;
    private final ChangeLogRepository changeLogRepository;

    public long getCount(EmployeeStatus status, LocalDate fromDate, LocalDate toDate) {
        if (status == null && fromDate == null && toDate == null)
            return employeeRepository.count();

        // 총 직원 수
        if (status != null && fromDate == null && toDate == null)
            return employeeRepository.countByStatus(status);

        // 최근 업데이트 수정 건수 (1주일 기준)
        if (status == null && fromDate != null && toDate != null) {
            Instant start = fromDate.atStartOfDay(ZoneId.of("UTC")).toInstant();
            Instant end = toDate.atTime(LocalTime.MAX).atZone(ZoneId.of("UTC")).toInstant();
            return changeLogRepository.countByUpdatedAtBetween(start, end);
        }

        // 이번 달 입사 건수
        if (status != null && fromDate != null && toDate != null)
            return employeeRepository.countByStatusAndHireDateBetween(status, fromDate, toDate);

        throw new IllegalArgumentException("지원되지 않는 파라미터 조합입니다.");
    }
}