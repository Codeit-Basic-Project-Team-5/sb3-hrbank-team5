package com.ohgiraffers.hrbank.dto.response;

import com.ohgiraffers.hrbank.dto.data.BackupDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


public record CursorPageResponseBackupDto (

    /**
     * 페이지 데이터 내용
     */
     List<BackupDto> content,

    /**
     * 다음 페이지 커서 (null일 경우 다음 페이지 없음)
     */
     String nextCursor,

    /**
     * 현재 페이지의 마지막 요소 ID
     */
     Long nextIdAfter,

    /**
     * 요청한 페이지 크기
     */
     int size,

    /**
     * 전체 요소 수
     */
     Long totalElements,

    /**
     * 다음 페이지 존재 여부
     */
     boolean hasNext
    ){}
