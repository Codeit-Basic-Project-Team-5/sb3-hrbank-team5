package com.ohgiraffers.hrbank.service;

import com.ohgiraffers.hrbank.entity.EmployeeStatus;
import java.time.LocalDate;

public interface DashBoardService {

    long getCount(EmployeeStatus status, LocalDate fromDate, LocalDate toDate);
}
