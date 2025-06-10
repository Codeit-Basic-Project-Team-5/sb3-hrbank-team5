import apiClient from '../client.ts';

/**
 * 파일 다운로드
 * @param id 파일 ID
 * @returns 파일 Blob 데이터
 */
export const downloadFile = async (id: number): Promise<Blob> => {
  const response = await apiClient.get<Blob>(`/files/${id}/download`, {
    responseType: 'blob'
  });
  return response.data;
};

/**
 * 파일 다운로드 URL 생성
 * @param id 파일 ID
 * @returns 파일 다운로드 URL
 */
export const getFileDownloadUrl = (id: number): string => {
  return `${apiClient.defaults.baseURL}/files/${id}/download`;
}; 