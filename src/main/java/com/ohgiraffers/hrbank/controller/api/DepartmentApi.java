package com.ohgiraffers.hrbank.controller.api;


import com.ohgiraffers.hrbank.dto.data.DepartmentDto;
import com.ohgiraffers.hrbank.dto.request.DepartmentCreateRequest;
import com.ohgiraffers.hrbank.dto.request.DepartmentUpdateRequest;
import com.ohgiraffers.hrbank.dto.response.DepartmentPageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "부서 관리", description = "부서 관리 API")
public interface DepartmentApi {

  @Operation(summary = "부서 등록", description = "새로운 부서를 등록합니다.")   // 부서 생성 (POST /api/departments)
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "등록 성공",
          content = @Content(schema = @Schema(implementation = DepartmentDto.class))
      ),
      @ApiResponse(
          responseCode = "400", description = "잘못된 요청 또는 중복된 이름.",
          content = @Content(examples = @ExampleObject(value = "Department with name{name} already exists."))
      ),
      @ApiResponse(
          responseCode = "500", description = "서버 오류",
          content = @Content(examples = @ExampleObject(value = " "))
      )
  })
  ResponseEntity<DepartmentDto> create(
      @Parameter(
          description = "Department 생성 정보",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DepartmentDto.class))
      )DepartmentCreateRequest request
  );

  @Operation(summary = "부서 목록 조회",description = "부서 목록을 조회합니다.")   // 전체 부서 조회 (Get /api/departments)
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "조회 성공",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = DepartmentPageResponse.class)))
      ),
      @ApiResponse(
          responseCode = "400", description = "잘못된 요청",
          content = @Content(examples = @ExampleObject(value = "Department with Id {departmentId} not found."))
      ),
      @ApiResponse(
          responseCode = "500", description = "서버 오류",
          content = @Content(examples = @ExampleObject(value = "Department with Id {departmentId} not found."))
      )
  })
  ResponseEntity<DepartmentPageResponse<DepartmentDto>> findAll(
      @Parameter(description = "부서 이름 또는 설명") @RequestParam(value = "nameOrDescription", required = false) String nameOrDescription,
      @Parameter(description = "이전 페이지 마지막 요소 ID") @RequestParam(value = "idAfter",required = false) Long idAfter,
      @Parameter(description = "커서 (다음 페이지 시작점)") @RequestParam(value = "cursor",required = false) String cursor,
      @Parameter(description = "정렬 필드 (name 또는 establishedDate") @RequestParam(value = "sortField", defaultValue = "establishedDate") String sortField,
      @Parameter(description = "정렬 방향 (asc 또는 desc, 기본값: asc)") @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
      @Parameter(description = "페이지 크기 (기본값: 10)") @RequestParam(value = "size", defaultValue = "10") int size);

  @Operation(summary = "부서 상세조회", description = "부서 상세 정보를 조회합니다.")    //부서 단건 조회 (GET /api/departments/{departmentId})
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "조회 성공",
          content = @Content(schema = @Schema(implementation = DepartmentDto.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "부서를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "Department with Id {departmentId} not found."))
      ),
      @ApiResponse(
          responseCode = "500", description = "서버 오류",
          content = @Content(examples = @ExampleObject(value = "Department with Id {departmentId} not found."))
      )
  })
  ResponseEntity<DepartmentDto> findById(Long id);

  @Operation(summary = "부서 정보 수정", description = "부서 정보를 수정합니다.") // 부서 수정 (PATCH /api/departments/{userId})
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "수정 성공",
          content = @Content(schema = @Schema(implementation = DepartmentDto.class))
      ),
      @ApiResponse(
          responseCode = "400", description = "잘못된 요청 또는 중복된 이름",
          content = @Content(examples = @ExampleObject("Department with name {name} already exists"))
      ),
      @ApiResponse(
          responseCode = "404", description = "부서를 찾을 수 없음",
          content = @Content(examples = @ExampleObject("Department with id {departmentId} not found"))
      ),
      @ApiResponse(
          responseCode = "500", description = "서버 오류",
          content = @Content(examples = @ExampleObject(value = "Department with Id {departmentId} not found."))
      )
  })
  ResponseEntity<DepartmentDto> update(
      @Parameter(description = "부서 Id") Long departmentId,
      @Parameter(description = "부서 정보") DepartmentUpdateRequest request);

  @Operation(summary = "부서 삭제", description = "부서를 삭제합니다.")   // 부서 삭제 (DELETE /api/departments/{departmentId}
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "204", description = "삭제 성공"
      ),
      @ApiResponse(
          responseCode = "404", description = "부서를 찾을 수 없음.",
          content = @Content(examples = @ExampleObject("Department with id {departmentId} not found"))
      ),
      @ApiResponse(
          responseCode = "400", description = "소속 직원이 있는 부서는 삭제할 수 없음",
          content = @Content(examples = @ExampleObject("Department with Employee cannot be deleted"))
      ),
      @ApiResponse(
          responseCode = "500", description = "서버 오류",
          content = @Content(examples = @ExampleObject(value = "Department with Id {departmentId} not found."))
      )
  })
  ResponseEntity<Void> delete(Long id);

}
