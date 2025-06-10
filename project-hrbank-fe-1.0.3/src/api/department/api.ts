import apiClient from '../client.ts';
import {
  DepartmentDto,
  DepartmentCreateRequest,
  DepartmentUpdateRequest,
  DepartmentPageResponse,
  DepartmentSearchParams
} from './model.ts';

/**
 * 부서 목록 조회
 * @param params 검색 파라미터
 * @returns 부서 목록 페이지 응답
 */
export const getDepartments = async (params?: DepartmentSearchParams): Promise<DepartmentPageResponse> => {
  const response = await apiClient.get<DepartmentPageResponse>('/departments', { params });
  return response.data;
};

/**
 * 부서 상세 조회
 * @param id 부서 ID
 * @returns 부서 상세 정보
 */
export const getDepartmentById = async (id: number): Promise<DepartmentDto> => {
  const response = await apiClient.get<DepartmentDto>(`/departments/${id}`);
  return response.data;
};

/**
 * 부서 등록
 * @param data 부서 등록 데이터
 * @returns 등록된 부서 정보
 */
export const createDepartment = async (data: DepartmentCreateRequest): Promise<DepartmentDto> => {
  const response = await apiClient.post<DepartmentDto>('/departments', data);
  return response.data;
};

/**
 * 부서 정보 수정
 * @param id 부서 ID
 * @param data 수정할 부서 데이터
 * @returns 수정된 부서 정보
 */
export const updateDepartment = async (
  id: number,
  data: DepartmentUpdateRequest
): Promise<DepartmentDto> => {
  const response = await apiClient.patch<DepartmentDto>(`/departments/${id}`, data);
  return response.data;
};

/**
 * 부서 삭제
 * @param id 부서 ID
 */
export const deleteDepartment = async (id: number): Promise<void> => {
  await apiClient.delete(`/departments/${id}`);
}; 