package com.ohgiraffers.hrbank.controller;

import com.ohgiraffers.hrbank.controller.api.DepartmentApi;
import com.ohgiraffers.hrbank.dto.data.DepartmentDto;
import com.ohgiraffers.hrbank.dto.request.DepartmentCreateRequest;
import com.ohgiraffers.hrbank.dto.request.DepartmentUpdateRequest;
import com.ohgiraffers.hrbank.dto.response.DepartmentPageResponse;
import com.ohgiraffers.hrbank.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/departments")
public class DepartmentController implements DepartmentApi {
  private final DepartmentService departmentService;

  /** 부서 생성
   * 입력 :
   * DepartmentCreateRequest(name, description, establishedDate)
   * 출력 :
   * 생성된 DepartmentDTO
   */
  @Override
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<DepartmentDto> create(
      @RequestBody DepartmentCreateRequest request
  ) {
    DepartmentDto createdDepartment = departmentService.create(request);

    return ResponseEntity.status(HttpStatus.CREATED).body(createdDepartment);
  }

  /** 부서 다건 조회
   * 입력값 : 소트필드=name , 소트디렉션 = asc , 사이즈 = 30 세가지.
   * 출력 :
   * contents List of DepartmentDTO
   * hasNext
   */
  @Override
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<DepartmentPageResponse<DepartmentDto>> findAll(
      @RequestParam(value = "nameOrDescription", required = false) String nameOrDescription,
      @RequestParam(value = "idAfter",required = false) Long idAfter,
      @RequestParam(value = "cursor",required = false) String cursor,
      @RequestParam(value = "sortField", defaultValue = "name") String sortField,
      @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
      @RequestParam(value = "size", defaultValue = "10") int size
  ) {
    DepartmentPageResponse<DepartmentDto> result = departmentService.findAllSorted(nameOrDescription, idAfter, cursor, sortField, sortDirection, size);
    return ResponseEntity.ok(result);
  }

  /** 부서 단건 조회
   * 입력 :
   * DepartmentId{pathVariable}
   * 출력 :
   * List of DepartmentDTO
   */
  @Override
  @GetMapping("/{departmentId}")
  public ResponseEntity<DepartmentDto> findById(
      @PathVariable Long departmentId
  ) {
    return ResponseEntity.status(HttpStatus.OK).body(departmentService.findById(departmentId));
  }

  /** 부서 정보 수정
   * 입력 :
   * DepartmentId{pathVariable}
   * DepartmentUpdateRequest(name, description, establishedDate)
   * 출력 :
   * DepartmentDTO
   */
  @Override
  @PatchMapping("/{departmentId}")
  public ResponseEntity<DepartmentDto> update(
      @PathVariable Long departmentId,
      @RequestBody DepartmentUpdateRequest request
  ) {
    return ResponseEntity.status(HttpStatus.OK).
        body(departmentService.update(departmentId, request));
  }
  /** 부서 정보 삭제
   * 입력 :
   * DepartmentId{pathVariable}
   */
  @Override
  @DeleteMapping("/{departmentId}")
  public ResponseEntity<Void> delete(
      @PathVariable Long departmentId
  ) {
    departmentService.delete(departmentId);
    return ResponseEntity.noContent().build();
  }
}
