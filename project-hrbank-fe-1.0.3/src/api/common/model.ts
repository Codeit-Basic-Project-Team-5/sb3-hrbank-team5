
// 커서 기반 페이지 응답 인터페이스
export interface CursorPageResponse<T> {
  content: T[];           // 페이지 내용
  totalElements: number;  // 전체 요소 수
  hasNext: boolean;       // 다음 페이지 존재 여부
  nextCursor?: string;    // 다음 페이지 커서
  nextIdAfter?: number;   // 다음 페이지 시작 ID
}

/**
 * 에러 응답 인터페이스
 */
export interface ErrorResponse {
  timestamp: string;      // 에러 발생 시간
  status: number;         // HTTP 상태 코드
  message: string;        // 에러 메시지
  details: string;        // 에러 상세 내용
} 