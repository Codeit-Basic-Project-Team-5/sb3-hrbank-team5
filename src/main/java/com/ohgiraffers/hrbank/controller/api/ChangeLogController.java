package com.ohgiraffers.hrbank.controller.api;

import com.ohgiraffers.hrbank.dto.request.ChangeLogRequest;
import com.ohgiraffers.hrbank.service.ChangeLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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
}