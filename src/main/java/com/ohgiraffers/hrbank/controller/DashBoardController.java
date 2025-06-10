package com.ohgiraffers.hrbank.controller;

import com.ohgiraffers.hrbank.entity.EmployeeStatus;
import com.ohgiraffers.hrbank.service.DashBoardService;
import java.time.LocalDate;
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
        long count = dashBoardService.getCount(status, fromDate, toDate);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/change-logs/count")
    public ResponseEntity<Long> getCount(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate fromDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate toDate
    ) {
        long count = dashBoardService.getCount(null, fromDate, toDate);
        return ResponseEntity.ok(count);
    }
}
