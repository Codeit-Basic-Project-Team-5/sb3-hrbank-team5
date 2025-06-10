import apiClient from '../client.ts';
import {
  ChangeLogCountParams,
  ChangeLogPageResponse,
  ChangeLogSearchParams,
  DiffDto
} from './model.ts';

/**
 * 변경 이력 목록 조회
 * @param params 검색 파라미터
 * @returns 변경 이력 목록 페이지 응답
 */
export const getChangeLogs = async (params?: ChangeLogSearchParams): Promise<ChangeLogPageResponse> => {
  const response = await apiClient.get<ChangeLogPageResponse>('/change-logs', { params });
  return response.data;
};

/**
 * 변경 이력 상세 조회 (변경 내용 포함)
 * @param id 이력 ID
 * @returns 변경 상세 내용 배열
 */
export const getChangeLogDiffs = async (id: number): Promise<DiffDto[]> => {
  const response = await apiClient.get<DiffDto[]>(`/change-logs/${id}/diffs`);
  return response.data;
};

/**
 * 변경 이력 건수 조회
 * @param params 건수 조회 파라미터
 * @returns 변경 이력 건수
 */
export const getChangeLogsCount = async (params?: ChangeLogCountParams): Promise<number> => {
  const response = await apiClient.get<number>('/change-logs/count', { params });
  return response.data;
}; 