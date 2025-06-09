package com.ohgiraffers.hrbank.dto.data;

import com.ohgiraffers.hrbank.entity.StatusType;
import java.time.Instant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
public class BackupDto {
    Long id;
    String worker;
    Instant startedAt;
    Instant endedAt;
    StatusType status;
    Long fileId;
}
