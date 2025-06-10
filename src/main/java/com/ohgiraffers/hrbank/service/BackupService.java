package com.ohgiraffers.hrbank.service;

import com.ohgiraffers.hrbank.dto.data.BackupDto;
import com.ohgiraffers.hrbank.dto.request.BackupCursorPageRequest;
import com.ohgiraffers.hrbank.dto.response.CursorPageResponseBackupDto;
import com.ohgiraffers.hrbank.entity.StatusType;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface BackupService {

    public CursorPageResponseBackupDto findAll(BackupCursorPageRequest backupCursorPageRequest);
    public BackupDto create(HttpServletRequest request);
    public BackupDto getLatest(StatusType status);
}
