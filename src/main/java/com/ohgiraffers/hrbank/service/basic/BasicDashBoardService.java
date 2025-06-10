package com.ohgiraffers.hrbank.service.basic;

import com.ohgiraffers.hrbank.repository.EmployeeRepository;
import com.ohgiraffers.hrbank.service.DashBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class BasicDashBoardService implements DashBoardService {
    private final EmployeeRepository employeeRepository;

    public long getTotalEmployeeCount() {
        return employeeRepository.count();
}