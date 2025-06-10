import { CursorPageResponse } from '../common/model.ts';

// 변경 이력 유형 열거형
export enum ChangeLogType {
  CREATED = 'CREATED', // 생성
  UPDATED = 'UPDATED', // 수정
  DELETED = 'DELETED'  // 삭제
}

// 직원 정보 수정 이력 인터페이스
export interface ChangeLogDto {
  id: number;              // 이력 ID
  type: ChangeLogType;     // 유형 (직원 추가, 정보 수정, 직원 삭제)
  employeeNumber: string;  // 직원 사번
  memo: string;            // 내용
  ipAddress: string;       // IP 주소
  at: string;              // 수정 일시
}

// 직원 정보 수정 이력 변경 내용 인터페이스
export interface DiffDto {
  propertyName: string;    // 속성 이름
  before?: string;          // 수정 전 값
  after?: string;           // 수정 후 값
}

// 변경 이력 목록 조회 응답 타입
export type ChangeLogPageResponse = CursorPageResponse<ChangeLogDto>;

// 변경 이력 목록 조회 파라미터 인터페이스
export interface ChangeLogSearchParams {
  employeeNumber?: string;  // 대상 직원 사번
  type?: ChangeLogType;     // 이력 유형
  memo?: string;            // 내용
  ipAddress?: string;       // IP 주소
  atFrom?: string;          // 수정 일시(부터)
  atTo?: string;            // 수정 일시(까지)
  idAfter?: number;         // 이전 페이지 마지막 요소 ID
  cursor?: string;          // 커서 (이전 페이지의 마지막 ID)
  size?: number;            // 페이지 크기 (기본값: 10)
  sortField?: string;       // 정렬 필드 (ipAddress, at)
  sortDirection?: 'asc' | 'desc'; // 정렬 방향 (기본값: desc)
}

// 변경 이력 건수 조회 파라미터 인터페이스
export interface ChangeLogCountParams {
  fromDate?: string;  // 시작 일시
  toDate?: string;    // 종료 일시
} 