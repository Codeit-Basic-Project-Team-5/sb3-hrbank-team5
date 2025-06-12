package com.ohgiraffers.hrbank.service.basic;

import com.ohgiraffers.hrbank.dto.data.ChangeLogDiffDto;
import com.ohgiraffers.hrbank.dto.data.EmployeeDto;
import com.ohgiraffers.hrbank.dto.request.ChangeLogRequest;
import com.ohgiraffers.hrbank.dto.response.ChangeLogCursorResponse;
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
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        changeLog.setEmployeeId(emp.getId());
        changeLog.setIpAddress(ipAddress);

        List<ChangeLogDiff> diffEntities = changeLogMapper.toDiffEntityList(changeLog, dto.diffs());
        changeLog.setDiffs(diffEntities);

        changeLogRepository.save(changeLog);
        return changeLog.getId();
    }

    @Override
    public void logEmployeeCreate(
        EmployeeDto after,
        String memo,
        HttpServletRequest request
    ) {
        List<ChangeLogDiffDto> diffs = new ArrayList<>();

        diffs.add(new ChangeLogDiffDto("name", "-", after.name()));
        diffs.add(new ChangeLogDiffDto("email", "-", after.email()));
        diffs.add(new ChangeLogDiffDto("departmentName", "-", after.departmentName()));
        diffs.add(new ChangeLogDiffDto("position", "-", after.position()));
        diffs.add(new ChangeLogDiffDto("hireDate", "-", after.hireDate().toString()));
        diffs.add(new ChangeLogDiffDto("employeeNumber", "-", after.employeeNumber()));
        diffs.add(new ChangeLogDiffDto("status", "-", after.status()));

        ChangeLogRequest req = new ChangeLogRequest(
            "CREATED",
            after.employeeNumber(),
            memo,
            diffs
        );
        registerChangeLog(req, request);
    }

    @Override
    @Transactional(readOnly = true)
    public ChangeLogCursorResponse searchWithCursor(
        Instant cursor,
        int size,
        String sortField,
        String sortDirection,
        String employeeNumber,
        String memo,
        String ipAddress,
        String type,
        Instant atFrom,
        Instant atTo
    ) {

        Instant effectiveCursor;
        if (cursor != null) {
            effectiveCursor = cursor;
        } else {
            effectiveCursor = Instant.now().plus(1, ChronoUnit.DAYS);
        }

        if ("at".equals(sortField)) {
            sortField = "updatedAt";
        }

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Sort sortOption = Sort.by(new Sort.Order(direction, sortField))
            .and(Sort.by(direction, "id"));

        Pageable page = PageRequest.of(0, size + 1, sortOption);
        String empP;
        if (nonBlank(employeeNumber)) {
            empP = "%" + employeeNumber + "%";
        } else {
            empP = null;
        }

        String memoP;
        if (nonBlank(memo)) {
            memoP = "%" + memo + "%";
        } else {
            memoP = null;
        }

        String ipP;
        if (nonBlank(ipAddress)) {
            ipP = "%" + ipAddress + "%";
        } else {
            ipP = null;
        }

        List<ChangeLog> logs;
        if (atFrom != null && atTo != null) {
            logs = changeLogRepository.findAllByFilterWithDate(
                effectiveCursor, empP, memoP, ipP, type, atFrom, atTo, page
            );
        } else if (atFrom != null) {
            logs = changeLogRepository.findAllByFilterFromOnly(
                effectiveCursor, empP, memoP, ipP, type, atFrom, page
            );
        } else if (atTo != null) {
            logs = changeLogRepository.findAllByFilterToOnly(
                effectiveCursor, empP, memoP, ipP, type, atTo, page
            );
        } else {
            logs = changeLogRepository.findAllByFilterNoDate(
                effectiveCursor, empP, memoP, ipP, type, page
            );
        }

        long total;
        if (atFrom != null && atTo != null) {
            total = changeLogRepository.countByFilterWithDate(
                effectiveCursor, empP, memoP, ipP, type, atFrom, atTo
            );
        } else if (atFrom != null) {
            total = changeLogRepository.countByFilterFromOnly(
                effectiveCursor, empP, memoP, ipP, type, atFrom
            );
        } else if (atTo != null) {
            total = changeLogRepository.countByFilterToOnly(
                effectiveCursor, empP, memoP, ipP, type, atTo
            );
        } else {
            total = changeLogRepository.countByFilterNoDate(
                effectiveCursor, empP, memoP, ipP, type
            );
        }

        boolean hasNext = logs.size() > size;
        List<ChangeLogListResponse> pageContent = logs.stream()
            .limit(size)
            .map(log -> new ChangeLogListResponse(
                log.getId(),
                log.getType(),
                log.getEmployee().getEmployeeNumber(),    // → employeeNumber
                log.getMemo(),
                log.getIpAddress(),
                log.getUpdatedAt()      // → at
            ))
            .toList();

        Instant nextCursor;
        if (hasNext) {
            nextCursor = pageContent.get(pageContent.size() - 1).at();
        } else {
            nextCursor = null;
        }

        Long nextIdAfter;
        if (hasNext) {
            nextIdAfter = pageContent.get(pageContent.size() - 1).id();
        } else {
            nextIdAfter = null;
        }
        return new ChangeLogCursorResponse(
            pageContent,
            nextCursor,
            nextIdAfter,
            size,
            total,
            hasNext
        );
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
                diff.getNewValue()
            ))
            .toList();
        return new ChangeLogDetailResponse(
            log.getId(),
            log.getType(),
            log.getEmployee().getEmployeeNumber(),      // → employeeNumber
            log.getMemo(),
            log.getIpAddress(),
            log.getUpdatedAt(),       // → at
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
                diff.getNewValue()
            ))
            .toList();
    }

    private boolean nonBlank(String s) {
        return s != null && !s.isBlank();
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