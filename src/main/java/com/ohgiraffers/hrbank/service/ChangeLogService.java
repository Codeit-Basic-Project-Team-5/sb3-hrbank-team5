package com.ohgiraffers.hrbank.service;

import com.ohgiraffers.hrbank.dto.request.ChangeLogRequest;
import com.ohgiraffers.hrbank.dto.request.ChangeLogSearchRequest;
import com.ohgiraffers.hrbank.dto.response.ChangeLogListResponse;
import com.ohgiraffers.hrbank.dto.response.ChangeLogDetailResponse;
import com.ohgiraffers.hrbank.dto.response.ChangeLogDiffResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChangeLogService {

    Long registerChangeLog(ChangeLogRequest dto, HttpServletRequest request);

    Page<ChangeLogListResponse> searchChangeLogs(
        ChangeLogSearchRequest criteria,
        Pageable pageable
    );

    ChangeLogDetailResponse getChangeLogDetail(Long id);

    List<ChangeLogDiffResponse> getDiffsByChangeLogId(Long changeLogId);

    Long countChangeLogs(Instant fromDate, Instant toDate);
}