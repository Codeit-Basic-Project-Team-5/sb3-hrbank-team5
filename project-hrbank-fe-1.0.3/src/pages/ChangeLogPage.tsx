import React, { useState, useEffect } from 'react';
import { 
  Box, 
  Typography, 
  SelectChangeEvent,
  Snackbar,
  Alert,
  Paper
} from '@mui/material';
import { PageContainer, PageTitle } from '../components/common';
import ChangeLogTable from '../components/changelog/ChangeLogTable.tsx';
import ChangeLogToolbar from '../components/changelog/ChangeLogToolbar.tsx';
import ChangeLogDetailModal from '../components/changelog/ChangeLogDetailModal.tsx';
import { 
  ChangeLogDto, 
  ChangeLogType
} from '../api/changelog/model.ts';
import { 
  getChangeLogs
} from '../api/changelog/api.ts';

const ChangeLogPage: React.FC = () => {
  const [changeLogs, setChangeLogs] = useState<ChangeLogDto[]>([]);
  const [loading, setLoading] = useState(false);
  const [employeeNumber, setEmployeeNumber] = useState('');
  const [memo, setMemo] = useState('');
  const [ipAddress, setIpAddress] = useState('');
  const [type, setType] = useState<ChangeLogType | ''>('');
  const [atFrom, setAtFrom] = useState<Date | null>(null);
  const [atTo, setAtTo] = useState<Date | null>(null);
  const [sortField, setSortField] = useState('at');
  const [sortDirection, setSortDirection] = useState<'asc' | 'desc'>('desc');
  const [totalCount, setTotalCount] = useState(0);
  const [hasMore, setHasMore] = useState(false);
  const [nextCursor, setNextCursor] = useState<string | undefined>(undefined);
  const [nextIdAfter, setNextIdAfter] = useState<number | undefined>(undefined);
  const [searchTimeout, setSearchTimeout] = useState<ReturnType<typeof setTimeout> | null>(null);
  
  // 필터 표시 상태
  const [showFilters, setShowFilters] = useState(false);
  
  // 상세 모달 상태
  const [detailModalOpen, setDetailModalOpen] = useState(false);
  const [selectedChangeLog, setSelectedChangeLog] = useState<ChangeLogDto | undefined>(undefined);
  
  // 알림 상태 관리
  const [snackbar, setSnackbar] = useState({
    open: false,
    message: '',
    severity: 'success' as 'success' | 'error'
  });

  const sortOptions = [
    { value: 'at:desc', label: '최신순' },
    { value: 'at:asc', label: '오래된순' },
    { value: 'ipAddress:asc', label: 'IP 주소 오름차순' },
    { value: 'ipAddress:desc', label: 'IP 주소 내림차순' },
  ];

  useEffect(() => {
    fetchInitialChangeLogs();
  }, []);

  // 검색 조건이 변경될 때마다 디바운스 적용
  useEffect(() => {
    // 이전 타이머가 있으면 취소
    if (searchTimeout) {
      clearTimeout(searchTimeout);
    }
    
    // 새로운 타이머 설정 (200ms 후에 검색 실행)
    const timer = setTimeout(() => {
      fetchInitialChangeLogs();
    }, 200);
    
    setSearchTimeout(timer);
    
    // 컴포넌트 언마운트 시 타이머 정리
    return () => {
      if (searchTimeout) {
        clearTimeout(searchTimeout);
      }
    };
  }, [employeeNumber, memo, ipAddress, type, atFrom, atTo, sortField, sortDirection]);

  // 날짜를 ISO-8601 형식의 문자열로 변환 (Java Instant 호환)
  const formatDateToString = (date: Date | null, isEndDate: boolean = false): string => {
    if (!date) return '';
    
    // 날짜 복제 (원본 변경 방지)
    const newDate = new Date(date);
    
    if (isEndDate) {
      // 종료일인 경우 해당 날짜의 23:59:59.999로 설정
      newDate.setHours(23, 59, 59, 999);
    } else {
      // 시작일인 경우 해당 날짜의 00:00:00.000으로 설정
      newDate.setHours(0, 0, 0, 0);
    }
    
    // ISO 문자열 반환 (Java Instant와 호환)
    return newDate.toISOString();
  };

  // 초기 데이터 로드 (페이지네이션 초기화)
  const fetchInitialChangeLogs = async () => {
    if (loading) return;
    
    // 페이지네이션 초기화
    setChangeLogs([]);
    setNextCursor(undefined);
    setNextIdAfter(undefined);
    setHasMore(true);
    
    setLoading(true);
    try {
      const response = await getChangeLogs({
        employeeNumber: employeeNumber || undefined,
        memo: memo || undefined,
        ipAddress: ipAddress || undefined,
        type: type || undefined,
        atFrom: atFrom ? formatDateToString(atFrom, false) : undefined,
        atTo: atTo ? formatDateToString(atTo, true) : undefined,
        sortField,
        sortDirection,
        size: 30
      });
      
      setChangeLogs(response.content);
      setTotalCount(response.totalElements);
      setNextCursor(response.nextCursor);
      setNextIdAfter(response.nextIdAfter);
      setHasMore(response.hasNext);
    } catch (error) {
      console.error('수정 이력을 불러오는 중 오류가 발생했습니다:', error);
      showSnackbar('수정 이력을 불러오는 중 오류가 발생했습니다.', 'error');
    } finally {
      setLoading(false);
    }
  };

  // 추가 데이터 로드 (무한 스크롤)
  const fetchMoreChangeLogs = async () => {
    if (loading || !hasMore) return;
    
    setLoading(true);
    try {
      const response = await getChangeLogs({
        employeeNumber: employeeNumber || undefined,
        memo: memo || undefined,
        ipAddress: ipAddress || undefined,
        type: type || undefined,
        atFrom: atFrom ? formatDateToString(atFrom, false) : undefined,
        atTo: atTo ? formatDateToString(atTo, true) : undefined,
        sortField,
        sortDirection,
        size: 30,
        cursor: nextCursor,
        idAfter: nextIdAfter
      });
      
      // 기존 데이터에 추가
      setChangeLogs(prev => [...prev, ...response.content]);
      setTotalCount(response.totalElements);
      setNextCursor(response.nextCursor);
      setNextIdAfter(response.nextIdAfter);
      setHasMore(response.hasNext);
    } catch (error) {
      console.error('수정 이력을 불러오는 중 오류가 발생했습니다:', error);
      showSnackbar('수정 이력을 불러오는 중 오류가 발생했습니다.', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleLoadMore = () => {
    fetchMoreChangeLogs();
  };

  // 검색 버튼 클릭 시 즉시 검색 실행 (더 이상 필요 없음)
  const handleSearch = () => {
    if (searchTimeout) {
      clearTimeout(searchTimeout);
      setSearchTimeout(null);
    }
    fetchInitialChangeLogs();
  };

  const handleSortChange = (e: SelectChangeEvent<string>) => {
    const [field, direction] = e.target.value.split(':');
    setSortField(field);
    setSortDirection(direction as 'asc' | 'desc');
  };

  const handleToggleFilters = () => {
    setShowFilters(!showFilters);
  };

  const handleClearFilters = () => {
    setEmployeeNumber('');
    setMemo('');
    setIpAddress('');
    setType('');
    setAtFrom(null);
    setAtTo(null);
  };

  const handleViewDetails = (changeLog: ChangeLogDto) => {
    setSelectedChangeLog(changeLog);
    setDetailModalOpen(true);
  };

  const showSnackbar = (message: string, severity: 'success' | 'error') => {
    setSnackbar({
      open: true,
      message,
      severity
    });
  };

  const handleCloseSnackbar = () => {
    setSnackbar({
      ...snackbar,
      open: false
    });
  };

  return (
    <PageContainer>
      <Box sx={{ display: 'flex', flexDirection: 'column', height: 'calc(100vh - 120px)' }}>
        <PageTitle>수정 이력</PageTitle>
        
        <ChangeLogToolbar
          employeeNumber={employeeNumber}
          onEmployeeNumberChange={setEmployeeNumber}
          memo={memo}
          onMemoChange={setMemo}
          ipAddress={ipAddress}
          onIpAddressChange={setIpAddress}
          type={type}
          onTypeChange={setType}
          atFrom={atFrom}
          onAtFromChange={setAtFrom}
          atTo={atTo}
          onAtToChange={setAtTo}
          sortValue={`${sortField}:${sortDirection}`}
          onSortChange={handleSortChange}
          sortOptions={sortOptions}
          onSearch={handleSearch}
          showFilters={showFilters}
          onToggleFilters={handleToggleFilters}
          onClearFilters={handleClearFilters}
        />
        
        <Box sx={{ flex: 1, overflow: 'hidden', display: 'flex', flexDirection: 'column' }}>
          <ChangeLogTable
            changeLogs={changeLogs}
            loading={loading}
            hasMore={hasMore}
            onLoadMore={handleLoadMore}
            totalCount={totalCount}
            onViewDetails={handleViewDetails}
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

      {/* 수정 이력 상세 모달 */}
      <ChangeLogDetailModal
        open={detailModalOpen}
        onClose={() => setDetailModalOpen(false)}
        changeLog={selectedChangeLog}
      />

      {/* 알림 스낵바 */}
      <Snackbar
        open={snackbar.open}
        autoHideDuration={5000}
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

export default ChangeLogPage; 