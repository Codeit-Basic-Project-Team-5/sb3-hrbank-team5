package com.ohgiraffers.hrbank.service.basic;

import com.ohgiraffers.hrbank.dto.data.BackupDto;
import com.ohgiraffers.hrbank.dto.data.EmployeeDistributionDto;
import com.ohgiraffers.hrbank.entity.Backup;
import com.ohgiraffers.hrbank.entity.EmployeeStatus;
import com.ohgiraffers.hrbank.mapper.BackupMapper;
import com.ohgiraffers.hrbank.repository.BackupRepository;
import com.ohgiraffers.hrbank.repository.ChangeLogRepository;
import com.ohgiraffers.hrbank.repository.EmployeeRepository;
import com.ohgiraffers.hrbank.service.DashBoardService;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BasicDashBoardService implements DashBoardService {
    private final EmployeeRepository employeeRepository;
    private final ChangeLogRepository changeLogRepository;
    private final BackupRepository backupRepository;
    private final BackupMapper backupMapper;

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

    public BackupDto getLatestBackup(String status) {
        Backup backup = backupRepository.findLatestBackupByStatus(status)
            .orElseThrow(() -> new NoSuchElementException("해당 상태의 백업이 존재하지 않습니다. (status: " + status + ")"));

        return backupMapper.toDto(backup);
    }
}