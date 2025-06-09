package com.ohgiraffers.hrbank.controller;


import com.ohgiraffers.hrbank.controller.api.BackupApi;
import com.ohgiraffers.hrbank.dto.data.BackupDto;
import com.ohgiraffers.hrbank.dto.request.BackupCreateRequest;
import com.ohgiraffers.hrbank.dto.request.CursorPageRequest;
import com.ohgiraffers.hrbank.dto.response.CursorPageResponseBackupDto;
import com.ohgiraffers.hrbank.entity.StatusType;
import com.ohgiraffers.hrbank.service.BackupService;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/backups")
public class BackupController implements BackupApi {

    private final BackupService backupService;

    @Override
    @PostMapping
    public ResponseEntity<BackupDto> create(BackupCreateRequest request) {
        BackupDto dto = backupService.create(request);
        return ResponseEntity.ok(dto);
    }

    @Override
    @GetMapping
    public ResponseEntity<CursorPageResponseBackupDto> getAll(String worker, StatusType status, Instant startedAtFrom, Instant startedAtTo, int size,
        String sortField, String sortDirection, String cursor) {
        CursorPageRequest request = new CursorPageRequest(
            worker, status, startedAtFrom, startedAtTo, size, sortField, sortDirection, cursor
        );

        CursorPageResponseBackupDto result = backupService.findAll(request);
        return ResponseEntity.ok(result);
    }

    @Override
    @GetMapping("/latest")
    public ResponseEntity<BackupDto> getLatest(StatusType status) {
        BackupDto dto = backupService.getLatest(status);
        return ResponseEntity.ok(dto);
    }

}
