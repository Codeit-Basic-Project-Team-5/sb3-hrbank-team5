// 파일 정보 인터페이스
export interface FileDto {
  id: number;              // 파일 ID
  originalName: string;    // 원본 파일명
  contentType: string;     // 콘텐츠 타입
  size: number;            // 파일 크기 (바이트)
  uploadedAt: string;      // 업로드 시간
} 