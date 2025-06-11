package com.ohgiraffers.hrbank.service;

import com.ohgiraffers.hrbank.dto.request.ChangeLogRequest;
import com.ohgiraffers.hrbank.dto.response.ChangeLogCursorResponse;
import com.ohgiraffers.hrbank.dto.response.ChangeLogDetailResponse;
import com.ohgiraffers.hrbank.dto.response.ChangeLogDiffResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;

public interface ChangeLogService {

    Long registerChangeLog(ChangeLogRequest dto, HttpServletRequest request);

    ChangeLogCursorResponse searchWithCursor(Instant cursor, int size, String sortField, String sortDir);

    ChangeLogDetailResponse getChangeLogDetail(Long id);

    List<ChangeLogDiffResponse> getDiffsByChangeLogId(Long changeLogId);
}