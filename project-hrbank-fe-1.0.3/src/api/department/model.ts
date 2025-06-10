import { CursorPageResponse } from '../common/model.ts';

// 부서 등록 요청 인터페이스
export interface DepartmentCreateRequest {
  name: string;           // 부서 이름
  description: string;    // 부서 설명
  establishedDate: string; // 부서 설립일 (YYYY-MM-DD 형식)
}

// 부서 수정 요청 인터페이스
export interface DepartmentUpdateRequest {
  name?: string;           // 부서 이름
  description?: string;    // 부서 설명
  establishedDate?: string; // 부서 설립일 (YYYY-MM-DD 형식)
}

// 부서 정보 인터페이스
export interface DepartmentDto {
  id: number;              // 부서 ID
  name: string;            // 부서 이름
  description: string;     // 부서 설명
  establishedDate: string; // 부서 설립일 (YYYY-MM-DD 형식)
  employeeCount: number;   // 소속 직원 수
}

// 부서 목록 조회 응답 타입
export type DepartmentPageResponse = CursorPageResponse<DepartmentDto>;

// 부서 목록 조회 파라미터 인터페이스
export interface DepartmentSearchParams {
  nameOrDescription?: string; // 부서 이름 또는 설명
  idAfter?: number;          // 이전 페이지 마지막 요소 ID
  cursor?: string;           // 커서 (다음 페이지 시작점)
  size?: number;             // 페이지 크기 (기본값: 10)
  sortField?: string;        // 정렬 필드 (name 또는 establishedDate)
  sortDirection?: 'asc' | 'desc'; // 정렬 방향 (asc 또는 desc, 기본값: asc)
} 