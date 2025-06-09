package com.ohgiraffers.hrbank.controller.api;

import com.ohgiraffers.hrbank.dto.data.BackupDto;
import com.ohgiraffers.hrbank.dto.request.BackupCreateRequest;
import com.ohgiraffers.hrbank.dto.response.CursorPageResponseBackupDto;
import com.ohgiraffers.hrbank.entity.StatusType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Instant;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "데이터 백업 관리", description = "데이터 백업 관리 API")
public interface BackupApi {

    /**
     * STEP 1~4: 데이터 백업 생성
     */
    @Operation(summary = "데이터 백업 생성", description = "데이터 백업을 생성합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "백업 생성 성공", content = @Content(schema = @Schema(implementation = BackupDto.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "이미 진행 중인 백업이 있음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<BackupDto> create(BackupCreateRequest request);

    /**
     * 데이터 백업 목록 조회
     */
    @Operation(summary = "데이터 백업 목록 조회", description = "데이터 백업 목록을 조회합니다.")
    public ResponseEntity<CursorPageResponseBackupDto> getAll(
        @Parameter(description = "작업자 IP") @RequestParam(required = false) String worker,
        @Parameter(description = "백업 상태") @RequestParam(required = false) StatusType status,
        @Parameter(description = "시작 시각(부터)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startedAtFrom,
        @Parameter(description = "시작 시각(까지)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startedAtTo,
        @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size,
        @Parameter(description = "정렬 필드") @RequestParam(defaultValue = "startedAt") String sortField,
        @Parameter(description = "정렬 방향") @RequestParam(defaultValue = "DESC") String sortDirection,
        @Parameter(description = "커서") @RequestParam(required = false) String cursor
    );

    /**
     * 상태별 최신 백업 조회
     */
    @Operation(summary = "최근 백업 정보 조회", description = "지정된 상태의 가장 최근 백업 정보를 조회합니다.")
    public ResponseEntity<BackupDto> getLatest(
        @Parameter(description = "백업 상태") @RequestParam(defaultValue = "COMPLETED") StatusType status
    );

}
