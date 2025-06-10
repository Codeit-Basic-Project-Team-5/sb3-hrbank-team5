import apiClient from '../client.ts';
import {
  BackupDto,
  BackupPageResponse,
  BackupSearchParams,
  LatestBackupParams
} from './model.ts';

/**
 * 백업 목록 조회
 * @param params 검색 파라미터
 * @returns 백업 목록 페이지 응답
 */
export const getBackups = async (params?: BackupSearchParams): Promise<BackupPageResponse> => {
  const response = await apiClient.get<BackupPageResponse>('/backups', { params });
  return response.data;
};

/**
 * 최근 백업 정보 조회
 * @param params 최근 백업 조회 파라미터
 * @returns 최근 백업 정보
 */
export const getLatestBackup = async (params?: LatestBackupParams): Promise<BackupDto> => {
  const response = await apiClient.get<BackupDto>('/backups/latest', { params });
  return response.data;
};

/**
 * 데이터 백업 생성
 * @returns 생성된 백업 정보
 */
export const createBackup = async (): Promise<BackupDto> => {
  const response = await apiClient.post<BackupDto>('/backups');
  return response.data;
}; 