package com.ohgiraffers.hrbank.controller.api;

import com.ohgiraffers.hrbank.dto.request.ChangeLogRequest;
import com.ohgiraffers.hrbank.dto.response.ChangeLogCursorResponse;
import com.ohgiraffers.hrbank.dto.response.ChangeLogDetailResponse;
import com.ohgiraffers.hrbank.dto.response.ChangeLogDiffResponse;
import com.ohgiraffers.hrbank.service.ChangeLogService;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    // iso => 날짜 포맷팅
    @GetMapping
    public ResponseEntity<ChangeLogCursorResponse> listByCursor(
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        Instant cursor,
        @RequestParam(defaultValue = "30") int size,
        // Swagger 기본값: at
        @RequestParam(defaultValue = "at") String sortField,
        @RequestParam(defaultValue = "DESC") String sortDir
    ) {
        // at → updatedAt 로 치환
        if ("at".equalsIgnoreCase(sortField)) {
            sortField = "updatedAt";
        }
        return ResponseEntity.ok(
            service.searchWithCursor(cursor, size, sortField, sortDir)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChangeLogDetailResponse> detail(@PathVariable Long id) {
        return ResponseEntity.ok(service.getChangeLogDetail(id));
    }

    @GetMapping("/{id}/diffs")
    public ResponseEntity<List<ChangeLogDiffResponse>> diffs(@PathVariable Long id) {
        return ResponseEntity.ok(service.getDiffsByChangeLogId(id));
    }

}