import { CursorPageResponse } from '../common/model.ts';

// 직원 상태 열거형
export enum EmployeeStatus {
  ACTIVE = 'ACTIVE',     // 재직중
  ON_LEAVE = 'ON_LEAVE', // 휴직중
  RESIGNED = 'RESIGNED'  // 퇴사
}

// 직원 등록 요청 인터페이스
export interface EmployeeCreateRequest {
  name: string;           // 직원 이름
  email: string;          // 직원 이메일
  departmentId: number;   // 부서 ID
  position: string;       // 직함
  hireDate: string;       // 입사일 (YYYY-MM-DD 형식)
  memo?: string;          // 수정 내용 (이력 기록용)
}

// 직원 수정 요청 인터페이스
export interface EmployeeUpdateRequest {
  name?: string;           // 직원 이름
  email?: string;          // 직원 이메일
  departmentId?: number;   // 부서 ID
  position?: string;       // 직함
  hireDate?: string;       // 입사일 (YYYY-MM-DD 형식)
  status?: EmployeeStatus; // 상태 (재직중, 휴직중, 퇴사)
  memo?: string;           // 수정 내용 (이력 기록용)
}

// 직원 정보 인터페이스
export interface EmployeeDto {
  id: number;              // 직원 ID
  name: string;            // 직원 이름
  email: string;           // 직원 이메일
  employeeNumber: string;  // 사원 번호
  departmentId: number;    // 부서 ID
  departmentName: string;  // 부서 이름
  position: string;        // 직함
  hireDate: string;        // 입사일 (YYYY-MM-DD 형식)
  status: EmployeeStatus;  // 상태 (재직중, 휴직중, 퇴사)
  profileImageId?: number; // 프로필 이미지 ID
}

// 직원 수 추이 정보 인터페이스
export interface EmployeeTrendDto {
  date: string;       // 시점 (날짜)
  count: number;      // 직원 수
  change: number;     // 증감 (이전 시점 대비)
  changeRate: number; // 증감률 (이전 시점 대비, %)
}

// 직원 분포 정보 인터페이스
export interface EmployeeDistributionDto {
  groupKey: string;   // 분류명 (부서명 또는 직무명)
  count: number;      // 직원 수
  percentage: number; // 비율 (%)
}

// 직원 목록 조회 응답 타입
export type EmployeePageResponse = CursorPageResponse<EmployeeDto>;

// 직원 목록 조회 파라미터 인터페이스
export interface EmployeeSearchParams {
  nameOrEmail?: string;        // 직원 이름 또는 이메일
  employeeNumber?: string;     // 사원 번호
  departmentName?: string;     // 부서 이름
  position?: string;           // 직함
  hireDateFrom?: string;       // 입사일 시작
  hireDateTo?: string;         // 입사일 종료
  status?: EmployeeStatus;     // 상태 (재직중, 휴직중, 퇴사)
  idAfter?: number;           // 이전 페이지 마지막 요소 ID
  cursor?: string;            // 커서 (다음 페이지 시작점)
  size?: number;              // 페이지 크기 (기본값: 10)
  sortField?: string;         // 정렬 필드 (name, employeeNumber, hireDate)
  sortDirection?: 'asc' | 'desc'; // 정렬 방향 (asc 또는 desc, 기본값: asc)
}

// 직원 수 추이 조회 파라미터 인터페이스
export interface EmployeeTrendParams {
  from?: string;                                // 시작 일시 (YYYY-MM-DD 형식)
  to?: string;                                  // 종료 일시 (YYYY-MM-DD 형식)
  unit?: 'day' | 'week' | 'month' | 'quarter' | 'year'; // 시간 단위 (기본값: month)
}

// 직원 분포 조회 파라미터 인터페이스
export interface EmployeeDistributionParams {
  groupBy?: 'department' | 'position'; // 그룹화 기준 (부서별, 직무별, 기본값: department)
  status?: EmployeeStatus;            // 직원 상태 (재직중, 휴직중, 퇴사)
}

// 직원 수 조회 파라미터 인터페이스
export interface EmployeeCountParams {
  status?: EmployeeStatus; // 직원 상태 (재직중, 휴직중, 퇴사)
  fromDate?: string;       // 입사일 시작 (YYYY-MM-DD 형식)
  toDate?: string;         // 입사일 종료 (YYYY-MM-DD 형식)
} 