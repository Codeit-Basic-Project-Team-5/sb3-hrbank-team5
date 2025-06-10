package com.ohgiraffers.hrbank.service;

import com.ohgiraffers.hrbank.entity.EmployeeStatus;
import java.time.LocalDate;

public interface DashBoardService {

    long getEmployeeCount(EmployeeStatus status, LocalDate fromDate, LocalDate toDate);
}
