package com.ohgiraffers.hrbank.service.basic;

import com.ohgiraffers.hrbank.dto.request.ChangeLogRequestDto;
import com.ohgiraffers.hrbank.entity.ChangeLog;
import com.ohgiraffers.hrbank.entity.ChangeLogDiff;
import com.ohgiraffers.hrbank.entity.Employee;
import com.ohgiraffers.hrbank.mapper.ChangeLogMapper;
import com.ohgiraffers.hrbank.repository.ChangeLogDiffRepository;
import com.ohgiraffers.hrbank.repository.ChangeLogRepository;
import com.ohgiraffers.hrbank.repository.EmployeeRepository;
import com.ohgiraffers.hrbank.service.ChangeLogService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
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


    public Long registerChangeLog(ChangeLogRequestDto dto, HttpServletRequest request) {
        Employee emp = employeeRepository.findByEmployeeNumber(dto.employee_id())
            .orElseThrow(() -> new NoSuchElementException(dto.employee_id() + " 사원번호는 존재하지 않습니다."));

        String ipAddress = getIpAddress(request);

        ChangeLog changeLog = changeLogMapper.toEntity(dto);
        changeLog.setEmployeeId(emp.getId().intValue());
        changeLog.setIpAddress(ipAddress);


        List<ChangeLogDiff> diffEntities = changeLogMapper.toDiffEntityList(changeLog, dto.diffs());
        changeLog.setDiffs(diffEntities);

        changeLogRepository.save(changeLog);

        return changeLog.getId();
    }

    //IP주소 받는 메서드
    public String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded=For");
        if (ipAddress != null && !ipAddress.isBlank()) {
            return ipAddress.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}