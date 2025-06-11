package com.ohgiraffers.hrbank.controller;

import com.ohgiraffers.hrbank.dto.data.EmployeeDistributionDto;
import com.ohgiraffers.hrbank.dto.data.EmployeeDto;
import com.ohgiraffers.hrbank.dto.data.EmployeeTrendDto;
import com.ohgiraffers.hrbank.entity.EmployeeStatus;
import com.ohgiraffers.hrbank.exception.InvalidRequestException;
import com.ohgiraffers.hrbank.service.DashBoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//테스트용 컨트롤러
@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class DashBoardController {

    private final DashBoardService dashBoardService;

    @Operation(summary = "직원 수 조회")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(type = "integer", format = "int64"))
        ),
        @ApiResponse(
            responseCode = "400", description = "잘못된 요청",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500", description = "서버 오류",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/employees/count")
    public ResponseEntity<Long> getCount(
        @RequestParam(required = false) EmployeeStatus status,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate fromDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate toDate
    ) {
        long count;
        if (status == null && fromDate == null && toDate == null) {
            count = dashBoardService.countAllEmployees();
        } else if (status != null && fromDate == null && toDate == null) {
            count = dashBoardService.countByStatus(status);
        } else if (status != null && fromDate != null && toDate != null) {
            count = dashBoardService.countHiredBetween(status, fromDate, toDate);
        } else {
            throw new InvalidRequestException("올바른 파라미터 조합이 아닙니다.");
        }

        return ResponseEntity.ok(count);
    }

    @Operation(summary = "직원 분포 조회")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = EmployeeDistributionDto.class))
            )
        ),
        @ApiResponse(
            responseCode = "400", description = "잘못된 요청 또는 지원하지 않는 그룹화 기준",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500", description = "서버 오류",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/employees/stats/distribution")
    public ResponseEntity<List<EmployeeDistributionDto>> getDistribution(
        @RequestParam(defaultValue = "department") String groupBy,
        @RequestParam(defaultValue = "ACTIVE") EmployeeStatus status
    ) {
        List<EmployeeDistributionDto> responses = dashBoardService.getDistribution(groupBy, status);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "직원 수 추이 조회")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = EmployeeTrendDto.class))
            )
        ),
        @ApiResponse(
            responseCode = "400", description = "잘못된 요청 또는 지원하지 않는 시간 단위",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500", description = "서버 오류",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/employees/stats/trend")
    public ResponseEntity<List<EmployeeTrendDto>> getTrend(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
        @RequestParam(defaultValue = "month") String unit
    ) {
        List<EmployeeTrendDto> dtos = dashBoardService.getTrend(from, to, unit);
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "수정 이력 건수 조회")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(type = "integer", format = "int64"))
        ),
        @ApiResponse(
            responseCode = "400", description = "잘못된 요청 또는 유효하지 않은 날짜 범위",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "500", description = "서버 오류",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/change-logs/count")
    public ResponseEntity<Long> getCount(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate fromDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate toDate
    ) {
        long count = dashBoardService.countUpdatesBetween(fromDate, toDate);
        return ResponseEntity.ok(count);
    }
}
