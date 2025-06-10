import { CursorPageResponse } from '../common/model.ts';

// 백업 상태 열거형
export enum BackupStatus {
  IN_PROGRESS = 'IN_PROGRESS', // 진행중
  COMPLETED = 'COMPLETED',     // 완료
  FAILED = 'FAILED',            // 실패
  SKIPPED = 'SKIPPED'            // 건너뜀
}

// 데이터 백업 정보 인터페이스
export interface BackupDto {
  id: number;              // 백업 ID
  worker: string;          // 작업자
  startedAt: string;       // 시작 시간
  endedAt?: string;        // 종료 시간
  status: BackupStatus;    // 상태 (진행중, 완료, 실패)
  fileId?: number;         // 백업 파일 ID
}

// 백업 목록 조회 응답 타입
export type BackupPageResponse = CursorPageResponse<BackupDto>;

// 백업 목록 조회 파라미터 인터페이스
export interface BackupSearchParams {
  worker?: string;         // 작업자
  status?: BackupStatus;   // 상태
  startedAtFrom?: string;  // 시작 시간(부터)
  startedAtTo?: string;    // 시작 시간(까지)
  idAfter?: number;        // 이전 페이지 마지막 요소 ID
  cursor?: string;         // 커서 (이전 페이지의 마지막 ID)
  size?: number;           // 페이지 크기 (기본값: 10)
  sortField?: string;      // 정렬 필드 (startedAt, endedAt, status)
  sortDirection?: 'ASC' | 'DESC'; // 정렬 방향 (기본값: DESC)
}

// 최근 백업 정보 조회 파라미터 인터페이스
export interface LatestBackupParams {
  status?: BackupStatus;   // 백업 상태 (기본값: COMPLETED)
} 