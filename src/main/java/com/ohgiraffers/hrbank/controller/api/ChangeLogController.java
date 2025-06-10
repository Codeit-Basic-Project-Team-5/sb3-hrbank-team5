package com.ohgiraffers.hrbank.controller.api;

import com.ohgiraffers.hrbank.dto.request.ChangeLogRequest;
import com.ohgiraffers.hrbank.dto.request.ChangeLogSearchRequest;
import com.ohgiraffers.hrbank.dto.response.ChangeLogListResponse;
import com.ohgiraffers.hrbank.dto.response.ChangeLogDetailResponse;
import com.ohgiraffers.hrbank.dto.response.ChangeLogDiffResponse;
import com.ohgiraffers.hrbank.service.ChangeLogService;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/change-logs")
@RequiredArgsConstructor
public class ChangeLogController {

    private final ChangeLogService service;

    @PostMapping
    public ResponseEntity<Long> register(
        @RequestBody ChangeLogRequest dto,
        HttpServletRequest request
    ) {
        return ResponseEntity.status(201)
            .body(service.registerChangeLog(dto, request));
    }

    @GetMapping
    public ResponseEntity<Page<ChangeLogListResponse>> list(
        ChangeLogSearchRequest criteria,
        @PageableDefault(size = 30, sort = "updatedAt", direction = Sort.Direction.ASC)
        Pageable pageable
    ) {
        Page<ChangeLogListResponse> page = service.searchChangeLogs(criteria, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChangeLogDetailResponse> detail(@PathVariable Long id) {
        return ResponseEntity.ok(service.getChangeLogDetail(id));
    }

    @GetMapping("/{id}/diffs")
    public ResponseEntity<List<ChangeLogDiffResponse>> diffs(@PathVariable Long id) {
        return ResponseEntity.ok(service.getDiffsByChangeLogId(id));
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count(
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        Instant fromDate,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        Instant toDate
    ) {
        return ResponseEntity.ok(service.countChangeLogs(fromDate, toDate));
    }
}