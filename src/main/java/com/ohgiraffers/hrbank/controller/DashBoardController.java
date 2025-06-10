package com.ohgiraffers.hrbank.controller;

import com.ohgiraffers.hrbank.service.DashBoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//테스트용 컨트롤러
@RestController
@RequiredArgsConstructor
@RequestMapping("api/dashboard")
public class DashBoardController {

    private final DashBoardService dashBoardService;

//    @Operation(summary = "직원 수 조회")
//    @ApiResponses({
//        @ApiResponse(
//            responseCode = "200", description = "조회 성공",
//            content = {}
//        )
//    })
//    @GetMapping("/count")
//    public ResponseEntity<Map<String, Long>> getEmployeeCount() {
//        long count = dashBoardService.getTotalEmployeeCount();
//        return ResponseEntity.ok(Map.of("count", count));
//    }
}
