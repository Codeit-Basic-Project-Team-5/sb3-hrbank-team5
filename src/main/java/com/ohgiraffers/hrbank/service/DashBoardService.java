package com.ohgiraffers.hrbank.service;

import com.ohgiraffers.hrbank.dto.data.EmployeeDistributionDto;
import com.ohgiraffers.hrbank.dto.data.EmployeeTrendDto;
import com.ohgiraffers.hrbank.entity.EmployeeStatus;
import java.time.LocalDate;
import java.util.List;

public interface DashBoardService {

    long countAllEmployees();

    long countByStatus(EmployeeStatus status);

    long countHiredBetween(EmployeeStatus status, LocalDate from, LocalDate to);

    long countUpdatesBetween(LocalDate from, LocalDate to);

    List<EmployeeDistributionDto> getDistribution(String groupBy, EmployeeStatus status);

    List<EmployeeTrendDto> getTrend(LocalDate from, LocalDate to, String unit);
}
