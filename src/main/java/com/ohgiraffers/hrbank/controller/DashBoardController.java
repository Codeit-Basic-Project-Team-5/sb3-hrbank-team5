package com.ohgiraffers.hrbank.controller;

import com.ohgiraffers.hrbank.dto.data.EmployeeDistributionDto;
import com.ohgiraffers.hrbank.dto.data.EmployeeTrendDto;
import com.ohgiraffers.hrbank.entity.EmployeeStatus;
import com.ohgiraffers.hrbank.service.DashBoardService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//테스트용 컨트롤러
@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class DashBoardController {

    private final DashBoardService dashBoardService;

    @GetMapping("/employees/count")
    public ResponseEntity<Long> getCount(
        @RequestParam(required = false) EmployeeStatus status,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate fromDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate toDate
    ) {
        long count;
        if (status == null && fromDate == null && toDate == null) {
            count = dashBoardService.countAllEmployees();
        } else if (status != null && fromDate == null && toDate == null) {
            count = dashBoardService.countByStatus(status);
        } else if (status != null && fromDate != null && toDate != null) {
            count = dashBoardService.countHiredBetween(status, fromDate, toDate);
        } else {
            throw new IllegalArgumentException("올바른 파라미터 조합이 아닙니다.");
        }

        return ResponseEntity.ok(count);
    }

    @GetMapping("/employees/stats/distribution")
    public ResponseEntity<List<EmployeeDistributionDto>> getDistribution(
        @RequestParam(defaultValue = "department") String groupBy,
        @RequestParam(defaultValue = "ACTIVE") EmployeeStatus status
    ) {
        List<EmployeeDistributionDto> responses = dashBoardService.getDistribution(groupBy, status);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/employees/stats/trend")
    public ResponseEntity<List<EmployeeTrendDto>> getTrend(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
        @RequestParam(defaultValue = "month") String unit
    ) {
        List<EmployeeTrendDto> dtos = dashBoardService.getTrend(from, to, unit);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/change-logs/count")
    public ResponseEntity<Long> getCount(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate fromDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate toDate
    ) {
        long count = dashBoardService.countUpdatesBetween(fromDate, toDate);
        return ResponseEntity.ok(count);
    }
}
