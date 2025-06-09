package com.ohgiraffers.hrbank.controller.api;


import com.ohgiraffers.hrbank.dto.data.DepartmentDto;
import com.ohgiraffers.hrbank.dto.request.DepartmentCreateRequest;
import com.ohgiraffers.hrbank.dto.request.DepartmentUpdateRequest;
import com.ohgiraffers.hrbank.entity.Department;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Tag(name = "Department", description = "DepartmentApi")
public interface DepartmentApi {

  @Operation(summary = "Department 등록")   // 부서 생성 (POST /api/departments)
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201", description = "Department가 성공적으로 생성됨",
          content = @Content(schema = @Schema(implementation = Department.class))
      ),
      @ApiResponse(
          responseCode = "400", description = "같은 이름을 사용하는 부서가 이미 존재함.",
          content = @Content(examples = @ExampleObject(value = "Department with name{name} already exists."))
      )
  })
  ResponseEntity<DepartmentDto> create(
      @Parameter(
          description = "Department 생성 정보",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Department.class))
      )DepartmentCreateRequest request
  );

  @Operation(summary = "전체 Depart 목록 조회")   // 전체 부서 조회 (Get /api/departments)
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "Department 목록 조회 성공",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = DepartmentDto.class)))
      )
  })
  ResponseEntity<List<DepartmentDto>> findAll();

  @Operation(summary = "ID로 부서 상세조회")    //부서 단건 조회 (GET /api/departments/{departmentId})
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "Department 정보 조회 성공",
          content = @Content(schema = @Schema(implementation = DepartmentDto.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "해당 Department Id를 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "Department with Id {departmentId} not found."))
      )
  })
  ResponseEntity<DepartmentDto> findById(Long id);

  @Operation(summary = "Department 정보 수정") // 부서 수정 (PATCH /api/departments/{userId})
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "Department 정보가 성공적으로 수정됨",
          content = @Content(schema = @Schema(implementation = Department.class))
      ),
      @ApiResponse(
          responseCode = "404", description = "Department 를 찾을 수 없음.",
          content = @Content(examples = @ExampleObject("Department with id {departmentId} not found"))
      ),
      @ApiResponse(
          responseCode = "400", description = "같은 name을 사용하는 Department 이미 존재함.",
          content = @Content(examples = @ExampleObject("Department with name {name} already exists"))
      )
  })
  ResponseEntity<DepartmentDto> update(
      @Parameter(description = "수정할 Department Id") Long departmentId,
      @Parameter(description = "수정할 Department 정보") DepartmentUpdateRequest request);

  @Operation(summary = "Department 정보 삭제")   // 부서 삭제 (DELETE /api/departments/{departmentId}
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "204", description = "Department 정보가 성공적으로 삭제됨"
      ),
      @ApiResponse(
          responseCode = "404", description = "Department 를 찾을 수 없음.",
          content = @Content(examples = @ExampleObject("Department with id {departmentId} not found"))
      )
  })
  ResponseEntity<Void> delete(Long id);

}
