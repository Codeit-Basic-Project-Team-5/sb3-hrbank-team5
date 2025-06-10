import {
  Alert,
  Box,
  Paper,
  Snackbar,
  Typography
} from '@mui/material';
import React, { useEffect, useState } from 'react';
import {
  createBackup,
  getBackups
} from '../api/backup/api.ts';
import {
  BackupDto,
  BackupStatus
} from '../api/backup/model.ts';
import apiClient from '../api/client.ts';
import { BackupTable, BackupToolbar } from '../components/backup';
import { PageContainer, PageTitle } from '../components/common';

const BackupPage: React.FC = () => {
  const [backups, setBackups] = useState<BackupDto[]>([]);
  const [loading, setLoading] = useState(false);
  const [worker, setWorker] = useState('');
  const [sortOption, setSortOption] = useState('startedAt:DESC');
  const [totalCount, setTotalCount] = useState(0);
  const [hasMore, setHasMore] = useState(false);
  const [nextCursor, setNextCursor] = useState<string | undefined>(undefined);
  const [nextIdAfter, setNextIdAfter] = useState<number | undefined>(undefined);
  const [searchTimeout, setSearchTimeout] = useState<ReturnType<typeof setTimeout> | null>(null);
  
  // 추가 필터링 옵션
  const [status, setStatus] = useState<BackupStatus | ''>('');
  const [startedAtFrom, setStartedAtFrom] = useState<string>('');
  const [startedAtTo, setStartedAtTo] = useState<string>('');
  
  // 백업 생성 상태
  const [isCreatingBackup, setIsCreatingBackup] = useState(false);
  
  // 알림 상태 관리
  const [snackbar, setSnackbar] = useState({
    open: false,
    message: '',
    severity: 'success' as 'success' | 'error'
  });

  useEffect(() => {
    fetchInitialBackups();
  }, []);

  // 검색 조건이 변경될 때마다 디바운스 적용
  useEffect(() => {
    // 이전 타이머가 있으면 취소
    if (searchTimeout) {
      clearTimeout(searchTimeout);
    }
    
    // 새로운 타이머 설정 (200ms 후에 검색 실행)
    const timer = setTimeout(() => {
      fetchInitialBackups();
    }, 200);
    
    setSearchTimeout(timer);
    
    // 컴포넌트 언마운트 시 타이머 정리
    return () => {
      if (searchTimeout) {
        clearTimeout(searchTimeout);
      }
    };
  }, [worker, sortOption, status, startedAtFrom, startedAtTo]);

  // ISO-8601 형식으로 변환하는 함수
  const formatDateTimeForAPI = (dateTimeString: string, isEndDate: boolean = false): string | undefined => {
    if (!dateTimeString) return undefined;
    
    try {
      // 날짜만 입력된 경우 (YYYY-MM-DD)
      // 시작일의 경우 00:00:00, 종료일의 경우 23:59:59를 추가
      const date = new Date(dateTimeString);
      
      if (isEndDate) {
        // 종료일의 경우 해당 날짜의 마지막 시간 (23:59:59.999)
        date.setHours(23, 59, 59, 999);
      } else {
        // 시작일의 경우 해당 날짜의 시작 시간 (00:00:00.000)
        date.setHours(0, 0, 0, 0);
      }
      
      return date.toISOString(); // ISO-8601 형식 (YYYY-MM-DDThh:mm:ss.sssZ)
    } catch (e) {
      console.error('날짜 변환 중 오류:', e);
      return undefined;
    }
  };

  // 초기 백업 목록 조회 (필터 적용)
  const fetchInitialBackups = async () => {
    // 페이지네이션 초기화
    setBackups([]);
    setNextCursor(undefined);
    setNextIdAfter(undefined);
    setHasMore(true);
    
    setLoading(true);
    
    try {
      const [sortField, sortDirection] = sortOption.split(':');
      
      const response = await getBackups({
        worker: worker || undefined,
        status: status || undefined,
        startedAtFrom: formatDateTimeForAPI(startedAtFrom),
        startedAtTo: formatDateTimeForAPI(startedAtTo, true),
        sortField,
        sortDirection: sortDirection as 'ASC' | 'DESC',
        size: 30
      });
      
      setBackups(response.content);
      setTotalCount(response.totalElements);
      setNextCursor(response.nextCursor);
      setNextIdAfter(response.nextIdAfter);
      setHasMore(response.hasNext);
    } catch (error) {
      console.error('백업 목록을 불러오는 중 오류가 발생했습니다:', error);
      showSnackbar('백업 목록을 불러오는 중 오류가 발생했습니다.', 'error');
    } finally {
      setLoading(false);
    }
  };

  // 추가 데이터 로드 (무한 스크롤)
  const fetchMoreBackups = async () => {
    if (loading || !hasMore) return;
    
    setLoading(true);
    try {
      const [sortField, sortDirection] = sortOption.split(':');
      
      const response = await getBackups({
        worker: worker || undefined,
        status: status || undefined,
        startedAtFrom: formatDateTimeForAPI(startedAtFrom),
        startedAtTo: formatDateTimeForAPI(startedAtTo, true),
        sortField,
        sortDirection: sortDirection as 'ASC' | 'DESC',
        cursor: nextCursor,
        idAfter: nextIdAfter,
        size: 30
      });
      
      // 기존 데이터에 추가
      setBackups(prev => [...prev, ...response.content]);
      setNextCursor(response.nextCursor);
      setNextIdAfter(response.nextIdAfter);
      setHasMore(response.hasNext);
    } catch (error) {
      console.error('추가 백업 목록을 불러오는 중 오류가 발생했습니다:', error);
      showSnackbar('추가 백업 목록을 불러오는 중 오류가 발생했습니다.', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleLoadMore = () => {
    fetchMoreBackups();
  };

  const handleWorkerChange = (value: string) => {
    setWorker(value);
  };

  const handleSortChange = (value: string) => {
    setSortOption(value);
  };

  const handleStatusChange = (value: BackupStatus | '') => {
    setStatus(value);
  };

  const handleStartedAtFromChange = (date: string) => {
    setStartedAtFrom(date);
  };

  const handleStartedAtToChange = (date: string) => {
    setStartedAtTo(date);
  };

  const handleClearFilters = () => {
    setWorker('');
    setStatus('');
    setStartedAtFrom('');
    setStartedAtTo('');
  };

  const handleCreateBackup = async () => {
    if (isCreatingBackup) return;
    
    setIsCreatingBackup(true);
    try {
      await createBackup();
      showSnackbar('백업이 성공적으로 요청되었습니다.', 'success');
      fetchInitialBackups();
    } catch (error) {
      console.error('백업 생성 중 오류가 발생했습니다:', error);
      showSnackbar('백업 생성 중 오류가 발생했습니다.', 'error');
    } finally {
      setIsCreatingBackup(false);
    }
  };

  const handleDownload = (backup: BackupDto) => {
    if (!backup.fileId) {
      showSnackbar('다운로드할 파일이 없습니다.', 'error');
      return;
    }
    
    // 파일 다운로드 링크 생성 및 클릭
    const downloadUrl = `${apiClient.defaults.baseURL}/files/${backup.fileId}/download`;
    const link = document.createElement('a');
    link.href = downloadUrl;
    link.setAttribute('download', `backup_${backup.id}.zip`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  const showSnackbar = (message: string, severity: 'success' | 'error') => {
    setSnackbar({
      open: true,
      message,
      severity
    });
  };

  const handleCloseSnackbar = () => {
    setSnackbar(prev => ({ ...prev, open: false }));
  };

  return (
    <PageContainer>
      <Box sx={{ display: 'flex', flexDirection: 'column', height: 'calc(100vh - 120px)' }}>
        <PageTitle>데이터 백업</PageTitle>
        
        <BackupToolbar
          worker={worker}
          onWorkerChange={handleWorkerChange}
          status={status}
          onStatusChange={handleStatusChange}
          startedAtFrom={startedAtFrom}
          onStartedAtFromChange={handleStartedAtFromChange}
          startedAtTo={startedAtTo}
          onStartedAtToChange={handleStartedAtToChange}
          sortOption={sortOption}
          onSortChange={handleSortChange}
          onCreateBackup={handleCreateBackup}
          onClearFilters={handleClearFilters}
          isCreatingBackup={isCreatingBackup}
        />
        
        <Box sx={{ flex: 1, overflow: 'hidden', display: 'flex', flexDirection: 'column' }}>
          <BackupTable
            backups={backups}
            loading={loading}
            hasMore={hasMore}
            totalCount={totalCount}
            onLoadMore={handleLoadMore}
            onDownload={handleDownload}
          />
        </Box>
        
        <Paper 
          elevation={0} 
          sx={{ 
            display: 'flex', 
            justifyContent: 'flex-end', 
            padding: '8px 16px',
            borderTop: '1px solid #eee',
            backgroundColor: '#f9f9f9'
          }}
        >
          <Typography variant="body2" color="text.secondary">
            총 {totalCount}건
          </Typography>
        </Paper>
      </Box>
      
      <Snackbar
        open={snackbar.open}
        autoHideDuration={6000}
        onClose={handleCloseSnackbar}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      >
        <Alert 
          onClose={handleCloseSnackbar} 
          severity={snackbar.severity}
          variant="filled"
          sx={{ width: '100%' }}
        >
          {snackbar.message}
        </Alert>
      </Snackbar>
    </PageContainer>
  );
};

export default BackupPage; 