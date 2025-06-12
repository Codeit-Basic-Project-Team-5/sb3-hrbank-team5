# README.md

# **5팀 - HRBank**

[5조 Notion](https://www.notion.so/5-207649136c1180e79ab9d2751a0a9b1b?pvs=21)

## **팀원 구성**

강문구 https://github.com/Kangmoongu

박인규 [https://github.com/Leichtstar](https://github.com/Leichtstar/sb3-hrbank-team5.git)

고희준 https://github.com/barrackkk

김유빈 https://github.com/Im-Ubin

정윤지 https://github.com/okodeee

---

## **프로젝트 소개**

- 프로그래밍 교육 사이트의 Spring 백엔드 시스템 구축
- 프로젝트 기간: 2025.06.03 ~ 2025.06.13

---

## **기술 스택**

- Backend: Spring Boot, Spring Data JPA
- Database: Postgresql
- 공통 Tool: Git & Github, Discord, Notion

---

## **팀원별 구현 기능 상세**

### 강문구
![backup-ezgif com-video-to-gif-converter](https://github.com/user-attachments/assets/4c921c38-a739-4d59-be12-88860724400a)


- **데이터 백업 프로세스**
    - 직원 데이터 변경 여부 확인 후 필요시 CSV 파일 백업 생성
    - OOM 방지를 위한 대용량 데이터 분할 처리 구현
    - 성공/실패에 따른 상태 업데이트 및 에러 로그 관리
- **자동 배치 백업 시스템**
    - Spring Scheduler를 활용한 주기적(1시간) 자동 백업 실행
    - 애플리케이션 설정을 통한 배치 주기 설정 가능
- **백업 이력 관리 API**
    - 작업자(부분일치), 시작시간(범위), 상태(완전일치) 조건으로 이력 조회
    - 시작/종료 시간 정렬 및 커서 기반 페이지네이션 구현

### 박인규
![departments-ezgif com-video-to-gif-converter](https://github.com/user-attachments/assets/137fd801-1d9d-41d7-ad0d-916eb8260f02)


- **부서 신규 등록 , 부서 수정**
    - 부서 `{이름}` , `{설명}` , `{설립일자}`를 인자로 부서 생성 및 수정 구현
    - 부서이름은 중복불가값으로 이미 존재하는 값 요청시 에러 로그 반환
- **전체 부서 조회 및 선택적 조건조회,정렬**
    - 전체 부서의 `{부서명}` , `{설명}` , `{소속직원 수}` , `{설립일}` 조회 구현
    - 검색어를 통해 `{부서명}` 또는 `{설명}`에 검색어를 포함하는 요소 선택조회 구현
    - 부서명과 설립일을 기준으로 오름차순,내림차순 정렬 구현
    - 무한 스크롤 형태의 커서기반 페이지네이션 구현.
- **지정된 부서 삭제 기능**
    - 소속 부서원이 없는 조건으로 삭제 로직 구현
- **각 기능 수행시 발생하는 예외값 처리**
    - 등록,수정시 중복된 이름 예외값 정의
    - 삭제시 잔여 부서원 존재할 경우 예외값 정이
    - 직원관리 등에서 단일부서 조회시 해당 부서가 존재하지 않는 경우에 대한 예외값 정의

### 김유빈
![dashboard-ezgif com-video-to-gif-converter](https://github.com/user-attachments/assets/d6b4c079-45f6-4493-b9ab-07b6ca201281)


**파일 관리**

- **파일 저장 기능**
    - DB에 파일 메타데이터 저장과 실제 로컬에 파일을 저장하는 비즈니스 로직 구현
- **파일 다운로드 API**
    - 특정 파일 고유 id 값에 따른 다운로드 API 엔드포인트 구현

**대시보드 관리**

- **직원 수 조회 API**
    - 지정된 조건에 맞는 직원 수를 조회하는 API 엔드포인트 구현
- **직원 분포 조회 API**
    - 지정된 기준으로 그룹화된 직원 분포를 조회하는 API 엔드포인트 구현
- **직원 수 추이 조회 API**
    - 지정된 기간 및 시간 단위로 그룹화된 직원 수 추이를 조회하는 API 엔드포인트 구현
- **수령 이력 건수 조회 API**
    - 최근 직원 정보 수정 이력 건수를 조회하는 API 엔드포인트 구현

### 정윤지
![employee-ezgif com-video-to-gif-converter](https://github.com/user-attachments/assets/ecf5a41b-813a-4bdf-8920-e46baf02a04c)


**직원 정보 관리**

- **직원 등록**
    - **{이름}**, **{이메일}**, **{부서}**, **{직함}**, **{입사일}**, **{프로필 이미지}**를 통한 직원 등록
- **직원 정보 수정**.
- **직원 정보 삭제**
- **직원 정보 목록 조회**
    - **{이름 또는 이메일}**, **{부서}**, **{직함}**, **{사원번호}**, **{입사일}**, **{상태}**로 직원 목록 조회
    - **{이름}**, **{입사일}**, **{사원번호}**로 정렬 및 페이지네이션 구현
- **직원 정보 상세 조회**

## **고희준**
![changelog-ezgif com-video-to-gif-converter](https://github.com/user-attachments/assets/22a9fce9-4175-42b4-8557-fb5f5067e04e)

**직원 정보 수정 이력 관리**

- **수정 이력 등록**
    - 직원 추가, 직원 정보 수정 화면에서 입력하는 메모 정보를 통해 등록
- **이력 목록 조회**
    - **대상 직원 사번**, **메모**, **IP 주소** (부분 일치 조건) , **시간** (범위 조건),  **유형**  (완전 일치 조건) 으로 이력 목록 조회
    - IP 주소, 시간으로 정렬 및 페이지네이션 구현

- **이력 상세 변경 내용 조회**
    
    <img width="477" alt="image (1)" src="https://github.com/user-attachments/assets/0982bd0e-1f14-4f59-99ea-2a108af9ee83" />



- ## **프로젝트 구조**
```
src
 ┣ main
 ┃ ┣ java/com/ohgiraffers/hrbank
 ┃ ┃ ┣ configuration
 ┃ ┃ ┃ ┗ SwaggerConfig.java
 ┃ ┃ ┣ controller
 ┃ ┃ ┃ ┣ api
 ┃ ┃ ┃ ┃ ┣ BackupApi.java
 ┃ ┃ ┃ ┃ ┣ ChangeLogController.java
 ┃ ┃ ┃ ┃ ┣ DepartmentApi.java
 ┃ ┃ ┃ ┃ ┗ FileApi.java
 ┃ ┃ ┃ ┣ BackupController.java
 ┃ ┃ ┃ ┣ DashBoardController.java
 ┃ ┃ ┃ ┣ DepartmentController.java
 ┃ ┃ ┃ ┣ EmployeeController.java
 ┃ ┃ ┃ ┗ FileController.java
 ┃ ┃ ┣ dto
 ┃ ┃ ┃ ┣ data
 ┃ ┃ ┃ ┃ ┣ BackupDto.java
 ┃ ┃ ┃ ┃ ┣ ChangeLogDiffDto.java
 ┃ ┃ ┃ ┃ ┣ ChangeLogDto.java
 ┃ ┃ ┃ ┃ ┣ DepartmentDto.java
 ┃ ┃ ┃ ┃ ┣ EmployeeDistributionDto.java
 ┃ ┃ ┃ ┃ ┣ EmployeeDto.java
 ┃ ┃ ┃ ┃ ┣ EmployeeSearchCondition.java
 ┃ ┃ ┃ ┃ ┗ EmployeeTrendDto.java
 ┃ ┃ ┃ ┣ request
 ┃ ┃ ┃ ┃ ┣ BackupCursorPageRequest.java
 ┃ ┃ ┃ ┃ ┣ ChangeLogRequest.java
 ┃ ┃ ┃ ┃ ┣ ChangeLogSearchRequest.java
 ┃ ┃ ┃ ┃ ┣ DepartmentCreateRequest.java
 ┃ ┃ ┃ ┃ ┣ DepartmentUpdateRequest.java
 ┃ ┃ ┃ ┃ ┣ EmployeeCreateRequest.java
 ┃ ┃ ┃ ┃ ┣ EmployeeSearchRequest.java
 ┃ ┃ ┃ ┃ ┣ EmployeeUpdateRequest.java
 ┃ ┃ ┃ ┃ ┗ FileCreateRequest.java
 ┃ ┃ ┃ ┗ response
 ┃ ┃ ┃ ┃ ┣ ChangeLogCursorResponse.java
 ┃ ┃ ┃ ┃ ┣ ChangeLogDetailResponse.java
 ┃ ┃ ┃ ┃ ┣ ChangeLogDiffResponse.java
 ┃ ┃ ┃ ┃ ┣ ChangeLogListResponse.java
 ┃ ┃ ┃ ┃ ┣ ChangeLogResponse.java
 ┃ ┃ ┃ ┃ ┣ CursorPageResponseBackupDto.java
 ┃ ┃ ┃ ┃ ┣ CursorPageResponseEmployeeDto.java
 ┃ ┃ ┃ ┃ ┣ DepartmentPageResponse.java
 ┃ ┃ ┃ ┃ ┗ ErrorResponse.java
 ┃ ┃ ┣ entity
 ┃ ┃ ┃ ┣ Backup.java
 ┃ ┃ ┃ ┣ ChangeLog.java
 ┃ ┃ ┃ ┣ ChangeLogDiff.java
 ┃ ┃ ┃ ┣ Department.java
 ┃ ┃ ┃ ┣ Employee.java
 ┃ ┃ ┃ ┣ EmployeeStatus.java
 ┃ ┃ ┃ ┣ File.java
 ┃ ┃ ┃ ┗ StatusType.java
 ┃ ┃ ┣ exception
 ┃ ┃ ┃ ┣ department
 ┃ ┃ ┃ ┃ ┣ DepartmentHasEmployeesException.java
 ┃ ┃ ┃ ┃ ┗ DepartmentNotFoundException.java
 ┃ ┃ ┃ ┣ DepartmentExceptionHandler.java
 ┃ ┃ ┃ ┣ DuplicateEmailException.java
 ┃ ┃ ┃ ┣ DuplicatedNameException.java
 ┃ ┃ ┃ ┣ EmployeeNotFoundException.java
 ┃ ┃ ┃ ┣ FileNotFoundException.java
 ┃ ┃ ┃ ┣ FileProcessingException.java
 ┃ ┃ ┃ ┣ GlobalExceptionHandler.java
 ┃ ┃ ┃ ┣ InvalidCursorException.java
 ┃ ┃ ┃ ┣ InvalidDateRangeException.java
 ┃ ┃ ┃ ┣ InvalidRequestException.java
 ┃ ┃ ┃ ┗ UnsupportedUnitException.java
 ┃ ┃ ┣ mapper
 ┃ ┃ ┃ ┣ BackupMapper.java
 ┃ ┃ ┃ ┣ ChangeLogMapper.java
 ┃ ┃ ┃ ┗ EmployeeMapper.java
 ┃ ┃ ┣ repository
 ┃ ┃ ┃ ┣ BackupRepository.java
 ┃ ┃ ┃ ┣ ChangeLogDiffRepository.java
 ┃ ┃ ┃ ┣ ChangeLogRepository.java
 ┃ ┃ ┃ ┣ DepartmentRepository.java
 ┃ ┃ ┃ ┣ EmployeeDashboardRepository.java
 ┃ ┃ ┃ ┣ EmployeeRepository.java
 ┃ ┃ ┃ ┣ EmployeeRepositoryCustom.java
 ┃ ┃ ┃ ┣ EmployeeRepositoryCustomImpl.java
 ┃ ┃ ┃ ┗ FileRepository.java
 ┃ ┃ ┣ service
 ┃ ┃ ┃ ┣ basic
 ┃ ┃ ┃ ┣ BackupService.java
 ┃ ┃ ┃ ┣ ChangeLogService.java
 ┃ ┃ ┃ ┣ DashBoardService.java
 ┃ ┃ ┃ ┣ DepartmentService.java
 ┃ ┃ ┃ ┣ EmployeeService.java
 ┃ ┃ ┃ ┗ FileService.java
 ┃ ┃ ┣ storage
 ┃ ┃ ┃ ┣ FileStorage.java
 ┃ ┃ ┃ ┗ LocalFileStorage.java
 ┃ ┃ ┗ HrBankApplication.java
```
