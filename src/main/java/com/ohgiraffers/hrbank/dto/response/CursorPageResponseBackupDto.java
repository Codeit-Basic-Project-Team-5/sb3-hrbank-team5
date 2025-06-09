package com.ohgiraffers.hrbank.dto.response;

import com.ohgiraffers.hrbank.dto.data.BackupDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CursorPageResponseBackupDto {

    /**
     * 페이지 데이터 내용
     */
    private List<BackupDto> content;

    /**
     * 다음 페이지 커서 (null일 경우 다음 페이지 없음)
     */
    private String nextCursor;

    /**
     * 현재 페이지의 마지막 요소 ID
     */
    private Long nextIdAfter;

    /**
     * 요청한 페이지 크기
     */
    private int size;

    /**
     * 전체 요소 수
     */
    private long totalElements;

    /**
     * 다음 페이지 존재 여부
     */
    private boolean hasNext;
}