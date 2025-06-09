package com.ohgiraffers.hrbank.dto.request;

import com.ohgiraffers.hrbank.entity.StatusType;
import java.time.Instant;

public record BackupCursorPageRequest(
     String worker,
     StatusType status,
     Instant startedAtFrom,
     Instant startedAtTo,
     Long idAfter,
     int size,
     String sortField,
     String sortDirection,
     String cursor
){}
