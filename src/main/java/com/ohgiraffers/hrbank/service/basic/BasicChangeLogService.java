package com.ohgiraffers.hrbank.service.basic;

import com.ohgiraffers.hrbank.dto.request.ChangeLogRequest;
import com.ohgiraffers.hrbank.dto.request.ChangeLogSearchRequest;
import com.ohgiraffers.hrbank.dto.response.ChangeLogDetailResponse;
import com.ohgiraffers.hrbank.dto.response.ChangeLogDiffResponse;
import com.ohgiraffers.hrbank.dto.response.ChangeLogListResponse;
import com.ohgiraffers.hrbank.entity.ChangeLog;
import com.ohgiraffers.hrbank.entity.ChangeLogDiff;
import com.ohgiraffers.hrbank.entity.Employee;
import com.ohgiraffers.hrbank.mapper.ChangeLogMapper;
import com.ohgiraffers.hrbank.repository.ChangeLogDiffRepository;
import com.ohgiraffers.hrbank.repository.ChangeLogRepository;
import com.ohgiraffers.hrbank.repository.EmployeeRepository;
import com.ohgiraffers.hrbank.service.ChangeLogService;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BasicChangeLogService implements ChangeLogService {

    private final ChangeLogRepository changeLogRepository;
    private final ChangeLogDiffRepository changeLogDiffRepository;
    private final EmployeeRepository employeeRepository;
    private final ChangeLogMapper changeLogMapper;

    @Override
    public Long registerChangeLog(ChangeLogRequest dto, HttpServletRequest request) {
        Employee emp = employeeRepository.findByEmployeeNumber(dto.employeeId())
            .orElseThrow(() -> new NoSuchElementException(dto.employeeId() + " 사원번호는 존재하지 않습니다."));

        String ipAddress = getIpAddress(request);

        ChangeLog changeLog = changeLogMapper.toEntity(dto);
        changeLog.setEmployeeId(emp.getEmployeeNumber());
        changeLog.setIpAddress(ipAddress);

        List<ChangeLogDiff> diffEntities = changeLogMapper.toDiffEntityList(changeLog, dto.diffs());
        changeLog.setDiffs(diffEntities);

        changeLogRepository.save(changeLog);

        return changeLog.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChangeLogListResponse> searchChangeLogs(
        ChangeLogSearchRequest request,
        Pageable pageable
    ) {
        Page<ChangeLog> page = changeLogRepository.search(
            request.employeeIdPartial(),
            request.memoPartial(),
            request.ipAddressPartial(),
            request.type(),
            request.from(),
            request.to(),
            pageable
        );
        return page.map(log -> new ChangeLogListResponse(
            log.getId(),
            log.getType(),
            String.valueOf(log.getEmployeeId()),
            log.getMemo(),
            log.getIpAddress(),
            log.getUpdatedAt()
        ));
    }

    @Override
    @Transactional(readOnly = true)
    public ChangeLogDetailResponse getChangeLogDetail(Long id) {
        ChangeLog log = changeLogRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(id + " 은 수정 이력이 존재하지 않습니다."));

        List<ChangeLogDiffResponse> diffs = changeLogDiffRepository
            .findByChangeLogIdOrderByCreatedAtDesc(id)
            .stream()
            .map(diff -> new ChangeLogDiffResponse(
                diff.getFieldName(),
                diff.getOldValue(),
                diff.getNewValue(),
                diff.getCreatedAt()
            ))
            .toList();

        return new ChangeLogDetailResponse(
            log.getId(),
            log.getType(),
            String.valueOf(log.getEmployeeId()),
            log.getMemo(),
            log.getIpAddress(),
            log.getUpdatedAt(),
            diffs
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChangeLogDiffResponse> getDiffsByChangeLogId(Long changeLogId) {
        return changeLogDiffRepository
            .findByChangeLogIdOrderByCreatedAtDesc(changeLogId)
            .stream()
            .map(diff -> new ChangeLogDiffResponse(
                diff.getFieldName(),
                diff.getOldValue(),
                diff.getNewValue(),
                diff.getCreatedAt()
            ))
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Long countChangeLogs(Instant fromDate, Instant toDate) {
        Instant now = Instant.now();
        Instant from;
        if (fromDate != null) {
            from = fromDate;
        } else {
            from = now.minus(7, ChronoUnit.DAYS);
        }

        Instant to;
        if (toDate != null) {
            to = toDate;
        } else {
            to = now;
        }
        return changeLogRepository.countByUpdatedAtBetween(from, to);
    }


    //IP주소 받는 메서드
    private String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded=For");
        if (ipAddress != null && !ipAddress.isBlank()) {
            return ipAddress.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}