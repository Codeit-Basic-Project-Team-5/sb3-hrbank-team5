package com.ohgiraffers.hrbank.service.basic;

import com.ohgiraffers.hrbank.entity.EmployeeStatus;
import com.ohgiraffers.hrbank.repository.EmployeeRepository;
import com.ohgiraffers.hrbank.service.DashBoardService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BasicDashBoardService implements DashBoardService {
    private final EmployeeRepository employeeRepository;

    public long getEmployeeCount(EmployeeStatus status, LocalDate fromDate, LocalDate toDate) {
        if (status == null && fromDate == null && toDate == null)
            return employeeRepository.count();

        if (status != null && fromDate == null && toDate == null)
            return employeeRepository.countByStatus(status);

        if (status == null && fromDate != null && toDate != null)
            return employeeRepository.countByHireDateBetween(fromDate, toDate);

        if (status != null && fromDate != null && toDate != null)
            return employeeRepository.countByStatusAndHireDateBetween(status, fromDate, toDate);

        throw new IllegalArgumentException("지원되지 않는 파라미터 조합입니다.");
    }
}