package com.ohgiraffers.hrbank.controller;

import com.ohgiraffers.hrbank.controller.api.DepartmentApi;
import com.ohgiraffers.hrbank.dto.data.DepartmentDto;
import com.ohgiraffers.hrbank.dto.request.DepartmentCreateRequest;
import com.ohgiraffers.hrbank.dto.request.DepartmentUpdateRequest;
import com.ohgiraffers.hrbank.entity.Department;
import com.ohgiraffers.hrbank.service.DepartmentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
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
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<DepartmentDto> create(
      @RequestPart("departmentCreateRequest")DepartmentCreateRequest request
  ) {
    Department createdDepartment = departmentService.create(request);

    return ResponseEntity.status(HttpStatus.CREATED).body(DepartmentDto.fromEntity(createdDepartment));
  }

  /** 부서 다건 조회
   * 출력 :
   * List of DepartmentDTO
   */
  @Override
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<DepartmentDto>> findAll() {
    List<DepartmentDto> departments = departmentService.findAll();
    return departments.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.status(HttpStatus.OK).body(departments);
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
      @RequestPart("departmentUpdateRequest") DepartmentUpdateRequest request
  ) {
    return ResponseEntity.status(HttpStatus.OK).
        body(DepartmentDto.fromEntity(
            departmentService.update(departmentId,request)
            )
        );
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
