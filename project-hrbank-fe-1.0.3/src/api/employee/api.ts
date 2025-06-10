import apiClient from '../client.ts';
import {
  EmployeeDto,
  EmployeeCreateRequest,
  EmployeeUpdateRequest,
  EmployeePageResponse,
  EmployeeSearchParams,
  EmployeeTrendDto,
  EmployeeTrendParams,
  EmployeeDistributionDto,
  EmployeeDistributionParams,
  EmployeeCountParams
} from './model.ts';

/**
 * 직원 목록 조회
 * @param params 검색 파라미터
 * @returns 직원 목록 페이지 응답
 */
export const getEmployees = async (params?: EmployeeSearchParams): Promise<EmployeePageResponse> => {
  const response = await apiClient.get<EmployeePageResponse>('/employees', { params });
  return response.data;
};

/**
 * 직원 상세 조회
 * @param id 직원 ID
 * @returns 직원 상세 정보
 */
export const getEmployeeById = async (id: number): Promise<EmployeeDto> => {
  const response = await apiClient.get<EmployeeDto>(`/employees/${id}`);
  return response.data;
};

/**
 * 직원 등록
 * @param data 직원 등록 데이터
 * @param profileImage 프로필 이미지 파일 (선택)
 * @returns 등록된 직원 정보
 */
export const createEmployee = async (
  data: EmployeeCreateRequest,
  profileImage?: File
): Promise<EmployeeDto> => {
  const formData = new FormData();
  formData.append('employee', new Blob([JSON.stringify(data)], { type: 'application/json' }));
  
  if (profileImage) {
    formData.append('profile', profileImage);
  }
  
  const response = await apiClient.post<EmployeeDto>('/employees', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
  
  return response.data;
};

/**
 * 직원 정보 수정
 * @param id 직원 ID
 * @param data 수정할 직원 데이터
 * @param profileImage 프로필 이미지 파일 (선택)
 * @returns 수정된 직원 정보
 */
export const updateEmployee = async (
  id: number,
  data: EmployeeUpdateRequest,
  profileImage?: File
): Promise<EmployeeDto> => {
  const formData = new FormData();
  formData.append('employee', new Blob([JSON.stringify(data)], { type: 'application/json' }));
  
  if (profileImage) {
    formData.append('profile', profileImage);
  }
  
  const response = await apiClient.patch<EmployeeDto>(`/employees/${id}`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
  
  return response.data;
};

/**
 * 직원 삭제
 * @param id 직원 ID
 */
export const deleteEmployee = async (id: number): Promise<void> => {
  await apiClient.delete(`/employees/${id}`);
};

/**
 * 직원 수 추이 조회
 * @param params 추이 조회 파라미터
 * @returns 직원 수 추이 데이터 배열
 */
export const getEmployeeTrend = async (params?: EmployeeTrendParams): Promise<EmployeeTrendDto[]> => {
  const response = await apiClient.get<EmployeeTrendDto[]>('/employees/stats/trend', { params });
  return response.data;
};

/**
 * 직원 분포 조회
 * @param params 분포 조회 파라미터
 * @returns 직원 분포 데이터 배열
 */
export const getEmployeeDistribution = async (params?: EmployeeDistributionParams): Promise<EmployeeDistributionDto[]> => {
  const response = await apiClient.get<EmployeeDistributionDto[]>('/employees/stats/distribution', { params });
  return response.data;
};

/**
 * 직원 수 조회
 * @param params 직원 수 조회 파라미터
 * @returns 직원 수
 */
export const getEmployeeCount = async (params?: EmployeeCountParams): Promise<number> => {
  const response = await apiClient.get<number>('/employees/count', { params });
  return response.data;
};

/**
 * 프로필 이미지 URL 생성
 * @param profileImageId 프로필 이미지 ID
 * @returns 프로필 이미지 URL
 */
export const getProfileImageUrl = (profileImageId?: number): string => {
  if (!profileImageId) {
    return '/assets/images/default-profile.svg'; // 기본 프로필 이미지 경로
  }
  return `${apiClient.defaults.baseURL}/files/${profileImageId}/download`;
}; 