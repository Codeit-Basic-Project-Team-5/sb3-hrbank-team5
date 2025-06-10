import AccessTimeIcon from '@mui/icons-material/AccessTime';
import BusinessCenterIcon from '@mui/icons-material/BusinessCenter';
import PeopleIcon from '@mui/icons-material/People';
import UpdateIcon from '@mui/icons-material/Update';
import {
  Box,
  Card,
  CardContent,
  FormControl,
  Grid,
  InputLabel,
  MenuItem,
  Paper,
  Select,
  SelectChangeEvent,
  Typography
} from '@mui/material';
import { format, formatDistance, parseISO } from 'date-fns';
import { ko } from 'date-fns/locale';
import React, { useEffect, useState } from 'react';
import { getLatestBackup } from '../api/backup/api.ts';
import { getChangeLogsCount } from '../api/changelog/api.ts';
import {
  getEmployeeCount,
  getEmployeeDistribution,
  getEmployeeTrend
} from '../api/employee/api.ts';
import {
  EmployeeDistributionDto,
  EmployeeStatus,
  EmployeeTrendDto
} from '../api/employee/model.ts';
import { PageContainer, PageTitle } from '../components/common';
import {
  EmployeeDistributionChart,
  EmployeeTrendChart
} from '../components/dashboard';

const DashboardPage: React.FC = () => {
  // 직원 추이 데이터
  const [trendData, setTrendData] = useState<EmployeeTrendDto[]>([]);
  const [trendLoading, setTrendLoading] = useState(false);
  const [timeUnit, setTimeUnit] = useState<string>('month');
  
  // 부서별 직원 분포 데이터
  const [departmentDistributionData, setDepartmentDistributionData] = useState<EmployeeDistributionDto[]>([]);
  const [departmentDistributionLoading, setDepartmentDistributionLoading] = useState(false);
  
  // 직함별 직원 분포 데이터
  const [positionDistributionData, setPositionDistributionData] = useState<EmployeeDistributionDto[]>([]);
  const [positionDistributionLoading, setPositionDistributionLoading] = useState(false);
  
  // 직원 상태별 카운트 데이터
  const [statusData, setStatusData] = useState<{ status: EmployeeStatus | 'TOTAL', count: number }[]>([]);
  const [statusLoading, setStatusLoading] = useState(false);
  
  // 최근 변경 내역 수
  const [recentChanges, setRecentChanges] = useState<number>(0);
  const [recentChangesLoading, setRecentChangesLoading] = useState(false);
  
  // 이번달 입사자 수
  const [newHires, setNewHires] = useState<number>(0);
  const [newHiresLoading, setNewHiresLoading] = useState(false);
  
  // 마지막 백업 시간
  const [lastBackup, setLastBackup] = useState<string>('');
  const [lastBackupLoading, setLastBackupLoading] = useState(false);

  // 직원 추이 데이터 로드
  useEffect(() => {
    const fetchTrendData = async () => {
      setTrendLoading(true);
      try {
        const data = await getEmployeeTrend({ unit: timeUnit as any });
        setTrendData(data);
      } catch (error) {
        console.error('직원 추이 데이터를 불러오는 중 오류가 발생했습니다:', error);
      } finally {
        setTrendLoading(false);
      }
    };
    
    fetchTrendData();
  }, [timeUnit]);

  // 직원 분포 데이터 로드 (부서별)
  useEffect(() => {
    const fetchDepartmentDistributionData = async () => {
      setDepartmentDistributionLoading(true);
      try {
        const data = await getEmployeeDistribution({ groupBy: 'department' });
        setDepartmentDistributionData(data);
      } catch (error) {
        console.error('부서별 직원 분포 데이터를 불러오는 중 오류가 발생했습니다:', error);
      } finally {
        setDepartmentDistributionLoading(false);
      }
    };
    
    fetchDepartmentDistributionData();
  }, []);
  
  // 직원 분포 데이터 로드 (직함별)
  useEffect(() => {
    const fetchPositionDistributionData = async () => {
      setPositionDistributionLoading(true);
      try {
        const data = await getEmployeeDistribution({ groupBy: 'position' });
        setPositionDistributionData(data);
      } catch (error) {
        console.error('직함별 직원 분포 데이터를 불러오는 중 오류가 발생했습니다:', error);
      } finally {
        setPositionDistributionLoading(false);
      }
    };
    
    fetchPositionDistributionData();
  }, []);

  // 직원 상태별 카운트 데이터 로드
  useEffect(() => {
    const fetchStatusData = async () => {
      setStatusLoading(true);
      try {
        // 전체 직원 수
        const totalCount = await getEmployeeCount();
        
        // 재직중 직원 수
        const activeCount = await getEmployeeCount({ status: EmployeeStatus.ACTIVE });
        
        // 휴직중 직원 수
        const onLeaveCount = await getEmployeeCount({ status: EmployeeStatus.ON_LEAVE });
        
        // 퇴사 직원 수
        const resignedCount = await getEmployeeCount({ status: EmployeeStatus.RESIGNED });
        
        setStatusData([
          { status: 'TOTAL', count: totalCount },
          { status: EmployeeStatus.ACTIVE, count: activeCount },
          { status: EmployeeStatus.ON_LEAVE, count: onLeaveCount },
          { status: EmployeeStatus.RESIGNED, count: resignedCount }
        ]);
      } catch (error) {
        console.error('직원 상태별 카운트 데이터를 불러오는 중 오류가 발생했습니다:', error);
      } finally {
        setStatusLoading(false);
      }
    };
    
    fetchStatusData();
  }, []);
  
  // 최근 변경 내역 수 로드
  useEffect(() => {
    const fetchRecentChanges = async () => {
      setRecentChangesLoading(true);
      try {
        // 오늘 날짜 기준 최근 7일간의 변경 내역 수 조회
        const today = new Date();
        const sevenDaysAgo = new Date(today);
        sevenDaysAgo.setDate(today.getDate() - 7);
        
        // ISO-8601 형식으로 날짜 포맷팅 (Java Instant 타입 호환)
        const fromDate = sevenDaysAgo.toISOString().split('T')[0] + 'T00:00:00Z';
        const toDate = today.toISOString().split('T')[0] + 'T23:59:59Z';
        
        const count = await getChangeLogsCount({ fromDate, toDate });
        setRecentChanges(count);
      } catch (error) {
        console.error('최근 변경 내역 수를 불러오는 중 오류가 발생했습니다:', error);
      } finally {
        setRecentChangesLoading(false);
      }
    };
    
    fetchRecentChanges();
  }, []);
  
  // 이번달 입사자 수 로드
  useEffect(() => {
    const fetchNewHires = async () => {
      setNewHiresLoading(true);
      try {
        // 이번달 1일부터 오늘까지 입사한 직원 수 조회
        const today = new Date();
        const firstDayOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);
        
        // employee API는 기존 형식 유지
        const fromDate = format(firstDayOfMonth, 'yyyy-MM-dd');
        const toDate = format(today, 'yyyy-MM-dd');
        
        const count = await getEmployeeCount({ 
          fromDate, 
          toDate,
          status: EmployeeStatus.ACTIVE 
        });
        setNewHires(count);
      } catch (error) {
        console.error('이번달 입사자 수를 불러오는 중 오류가 발생했습니다:', error);
      } finally {
        setNewHiresLoading(false);
      }
    };
    
    fetchNewHires();
  }, []);
  
  // 마지막 백업 시간 로드
  useEffect(() => {
    const fetchLastBackup = async () => {
      setLastBackupLoading(true);
      try {
        const latestBackup = await getLatestBackup();
        if (latestBackup && latestBackup.endedAt) {
          const backupDate = parseISO(latestBackup.endedAt);
          const relativeTime = formatDistance(backupDate, new Date(), { 
            addSuffix: true,
            locale: ko 
          });
          setLastBackup(relativeTime);
        } else {
          setLastBackup('백업 정보 없음');
        }
      } catch (error) {
        console.error('마지막 백업 시간을 불러오는 중 오류가 발생했습니다:', error);
        setLastBackup('백업 정보 없음');
      } finally {
        setLastBackupLoading(false);
      }
    };
    
    fetchLastBackup();
  }, []);

  // 시간 단위 변경 핸들러
  const handleTimeUnitChange = (event: SelectChangeEvent) => {
    setTimeUnit(event.target.value);
  };

  // 요약 카드 컴포넌트
  const SummaryCard = ({ 
    icon, 
    title, 
    value, 
    unit,
    loading
  }: { 
    icon: React.ReactNode, 
    title: string, 
    value: string | number, 
    unit?: string,
    loading?: boolean
  }) => (
    <Card sx={{ 
      height: '100%', 
      display: 'flex', 
      alignItems: 'center',
      backgroundColor: '#f8faff',
      boxShadow: '0 2px 8px rgba(0,0,0,0.08)',
      borderRadius: 2,
      border: '1px solid #eef2ff'
    }}>
      <CardContent sx={{ display: 'flex', alignItems: 'center', width: '100%' }}>
        <Box sx={{ 
          display: 'flex', 
          alignItems: 'center', 
          justifyContent: 'center', 
          bgcolor: 'primary.main', 
          color: 'primary.contrastText', 
          borderRadius: '50%', 
          p: 1.5, 
          mr: 2,
          opacity: 0.9,
          boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
        }}>
          {icon}
        </Box>
        <Box>
          <Typography variant="body2" color="text.secondary" fontWeight={500}>
            {title}
          </Typography>
          <Box sx={{ display: 'flex', alignItems: 'baseline' }}>
            {loading ? (
              <Typography variant="body2" color="text.secondary">
                로딩 중...
              </Typography>
            ) : (
              <>
                <Typography variant="h4" component="div" fontWeight="bold" color="primary.dark">
                  {value}
                </Typography>
                {unit && (
                  <Typography variant="body1" color="text.secondary" sx={{ ml: 1 }}>
                    {unit}
                  </Typography>
                )}
              </>
            )}
          </Box>
        </Box>
      </CardContent>
    </Card>
  );

  return (
    <PageContainer>
      <Box sx={{ mb: 3 }}>
        <PageTitle>대시보드</PageTitle>
      </Box>
      
      {/* 요약 카드 */}
      <Grid container spacing={3} sx={{ mb: 3 }}>
        <Grid item xs={12} sm={6} md={3}>
          <SummaryCard 
            icon={<PeopleIcon />} 
            title="총 직원 수" 
            value={statusData.find(item => item.status === 'TOTAL')?.count || 0}
            unit="명"
            loading={statusLoading}
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <SummaryCard 
            icon={<UpdateIcon />} 
            title="최근 업데이트 수정" 
            value={recentChanges}
            unit="건"
            loading={recentChangesLoading}
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <SummaryCard 
            icon={<BusinessCenterIcon />} 
            title="이번달 입사" 
            value={newHires}
            unit="명"
            loading={newHiresLoading}
          />
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <SummaryCard 
            icon={<AccessTimeIcon />} 
            title="마지막 백업" 
            value={lastBackup}
            loading={lastBackupLoading}
          />
        </Grid>
      </Grid>
      
      {/* 직원 추이 차트 */}
      <Paper sx={{ 
        p: 2, 
        mb: 3, 
        backgroundColor: '#f8faff',
        boxShadow: '0 2px 8px rgba(0,0,0,0.06)',
        borderRadius: 2,
        border: '1px solid #eef2ff'
      }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
          <Typography variant="h6" color="primary.dark" fontWeight={600}>직원수 변동 추이</Typography>
          <FormControl size="small" sx={{ minWidth: 120 }}>
            <InputLabel>기간</InputLabel>
            <Select
              value={timeUnit}
              label="기간"
              onChange={handleTimeUnitChange}
            >
              <MenuItem value="day">일별</MenuItem>
              <MenuItem value="week">주별</MenuItem>
              <MenuItem value="month">월별</MenuItem>
              <MenuItem value="quarter">분기별</MenuItem>
              <MenuItem value="year">연도별</MenuItem>
            </Select>
          </FormControl>
        </Box>
        <EmployeeTrendChart 
          data={trendData} 
          loading={trendLoading} 
          title="" 
          timeUnit={timeUnit} 
        />
      </Paper>
      
      {/* 직원 분포 차트 */}
      <Grid container spacing={3}>
        <Grid item xs={12} md={6}>
          <Paper sx={{ 
            p: 2, 
            height: '100%',
            backgroundColor: '#f8faff',
            boxShadow: '0 2px 8px rgba(0,0,0,0.06)',
            borderRadius: 2,
            border: '1px solid #eef2ff'
          }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
              <Typography variant="h6" color="primary.dark" fontWeight={600}>부서별 직원 분포</Typography>
            </Box>
            <EmployeeDistributionChart 
              data={departmentDistributionData} 
              loading={departmentDistributionLoading} 
              title="" 
              groupBy="department" 
            />
          </Paper>
        </Grid>
        
        <Grid item xs={12} md={6}>
          <Paper sx={{ 
            p: 2, 
            height: '100%',
            backgroundColor: '#f8faff',
            boxShadow: '0 2px 8px rgba(0,0,0,0.06)',
            borderRadius: 2,
            border: '1px solid #eef2ff'
          }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
              <Typography variant="h6" color="primary.dark" fontWeight={600}>직함별 직원 분포</Typography>
            </Box>
            <EmployeeDistributionChart 
              data={positionDistributionData} 
              loading={positionDistributionLoading} 
              title="" 
              groupBy="position" 
            />
          </Paper>
        </Grid>
      </Grid>
    </PageContainer>
  );
};

export default DashboardPage; 