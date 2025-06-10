import axios, { InternalAxiosRequestConfig, AxiosResponse, AxiosError } from 'axios';
import { ErrorResponse } from './common/model.ts';

// API 기본 URL 설정
const BASE_URL = '/api';

// axios 인스턴스 생성
const apiClient = axios.create({
  baseURL: BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 전역 에러 핸들러 (외부에서 설정 가능)
let globalErrorHandler: ((error: ErrorResponse) => void) | null = null;

// 전역 에러 핸들러 설정 함수
export const setGlobalErrorHandler = (handler: (error: ErrorResponse) => void) => {
  globalErrorHandler = handler;
};

// 요청 인터셉터 설정
apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // 요청 전에 수행할 작업
    // 예: 토큰 추가, 로깅 등
    return config;
  },
  (error: AxiosError) => {
    return Promise.reject(error);
  }
);

// 응답 인터셉터 설정
apiClient.interceptors.response.use(
  (response: AxiosResponse) => {
    // 응답 데이터 가공 등
    return response;
  },
  (error: AxiosError) => {
    // 에러 처리
    console.error('API 요청 오류:', error);
    
    // 서버에서 반환한 에러 응답 처리
    if (error.response && error.response.data) {
      const errorData = error.response.data as ErrorResponse;
      
      // 전역 에러 핸들러가 설정되어 있으면 호출
      if (globalErrorHandler) {
        globalErrorHandler(errorData);
      }
    }
    
    return Promise.reject(error);
  }
);

export default apiClient; 