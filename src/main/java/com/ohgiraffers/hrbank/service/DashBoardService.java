package com.ohgiraffers.hrbank.service;

import com.ohgiraffers.hrbank.dto.data.EmployeeDistributionDto;
import com.ohgiraffers.hrbank.entity.EmployeeStatus;
import java.time.LocalDate;
import java.util.List;

public interface DashBoardService {

    long getCount(EmployeeStatus status, LocalDate fromDate, LocalDate toDate);

    List<EmployeeDistributionDto> getDistribution(String groupBy, EmployeeStatus status);
}
