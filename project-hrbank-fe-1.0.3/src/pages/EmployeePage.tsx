import {
  Alert,
  Box,
  Paper,
  SelectChangeEvent,
  Snackbar,
  Typography
} from '@mui/material';
import React, { useEffect, useState } from 'react';
import { getDepartments } from '../api/department/api.ts';
import { DepartmentDto } from '../api/department/model.ts';
import {
  createEmployee,
  deleteEmployee,
  getEmployees,
  updateEmployee
} from '../api/employee/api.ts';
import {
  EmployeeDto,
  EmployeeStatus
} from '../api/employee/model.ts';
import { ConfirmDeleteModal, EmployeeFormModal, PageContainer, PageTitle } from '../components/common';
import EmployeeTable from '../components/employee/EmployeeTable.tsx';
import EmployeeToolbar from '../components/employee/EmployeeToolbar.tsx';

const EmployeePage: React.FC = () => {
  const [employees, setEmployees] = useState<EmployeeDto[]>([]);
  const [departments, setDepartments] = useState<DepartmentDto[]>([]);
  const [loading, setLoading] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const [sortField, setSortField] = useState('name');
  const [sortDirection, setSortDirection] = useState<'asc' | 'desc'>('asc');
  const [totalCount, setTotalCount] = useState(0);
  const [hasMore, setHasMore] = useState(false);
  const [nextCursor, setNextCursor] = useState<string | undefined>(undefined);
  const [nextIdAfter, setNextIdAfter] = useState<number | undefined>(undefined);
  const [searchTimeout, setSearchTimeout] = useState<ReturnType<typeof setTimeout> | null>(null);
  
  // 추가 필터링 옵션
  const [departmentName, setDepartmentName] = useState('');
  const [position, setPosition] = useState('');
  const [status, setStatus] = useState<EmployeeStatus | ''>('');
  const [showFilters, setShowFilters] = useState(false);
  
  // 모달 상태 관리
  const [formModalOpen, setFormModalOpen] = useState(false);
  const [deleteModalOpen, setDeleteModalOpen] = useState(false);
  const [selectedEmployee, setSelectedEmployee] = useState<EmployeeDto | undefined>(undefined);
  
  // 알림 상태 관리
  const [snackbar, setSnackbar] = useState({
    open: false,
    message: '',
    severity: 'success' as 'success' | 'error'
  });

  const sortOptions = [
    { value: 'name:asc', label: '이름 오름차순' },
    { value: 'name:desc', label: '이름 내림차순' },
    { value: 'employeeNumber:asc', label: '사번 오름차순' },
    { value: 'employeeNumber:desc', label: '사번 내림차순' },
    { value: 'hireDate:asc', label: '입사일 오름차순' },
    { value: 'hireDate:desc', label: '입사일 내림차순' },
  ];

  useEffect(() => {
    fetchDepartments();
    fetchInitialEmployees();
  }, []);

  // 검색 조건이 변경될 때마다 디바운스 적용
  useEffect(() => {
    // 이전 타이머가 있으면 취소
    if (searchTimeout) {
      clearTimeout(searchTimeout);
    }
    
    // 새로운 타이머 설정 (500ms 후에 검색 실행)
    const timer = setTimeout(() => {
      fetchInitialEmployees();
    }, 200);
    
    setSearchTimeout(timer);
    
    // 컴포넌트 언마운트 시 타이머 정리
    return () => {
      if (searchTimeout) {
        clearTimeout(searchTimeout);
      }
    };
  }, [searchTerm, sortField, sortDirection, departmentName, position, status]);

  const fetchDepartments = async () => {
    try {
      const response = await getDepartments({ size: 100 });
      setDepartments(response.content);
    } catch (error) {
      console.error('부서 목록을 불러오는 중 오류가 발생했습니다:', error);
      showSnackbar('부서 목록을 불러오는 중 오류가 발생했습니다.', 'error');
    }
  };

  // 초기 데이터 로드 (페이지네이션 초기화)
  const fetchInitialEmployees = async () => {
    if (loading) return;
    
    // 페이지네이션 초기화
    setEmployees([]);
    setNextCursor(undefined);
    setNextIdAfter(undefined);
    setHasMore(true);
    
    setLoading(true);
    try {
      const response = await getEmployees({
        nameOrEmail: searchTerm || undefined,
        departmentName: departmentName || undefined,
        position: position || undefined,
        status: status || undefined,
        sortField,
        sortDirection,
        size: 30
      });
      
      setEmployees(response.content);
      setTotalCount(response.totalElements);
      setNextCursor(response.nextCursor);
      setNextIdAfter(response.nextIdAfter);
      setHasMore(response.hasNext);
    } catch (error) {
      console.error('직원 목록을 불러오는 중 오류가 발생했습니다:', error);
      showSnackbar('직원 목록을 불러오는 중 오류가 발생했습니다.', 'error');
    } finally {
      setLoading(false);
    }
  };

  // 추가 데이터 로드 (무한 스크롤)
  const fetchMoreEmployees = async () => {
    if (loading || !hasMore) return;
    
    setLoading(true);
    try {
      const response = await getEmployees({
        nameOrEmail: searchTerm || undefined,
        departmentName: departmentName || undefined,
        position: position || undefined,
        status: status || undefined,
        sortField,
        sortDirection,
        size: 30,
        cursor: nextCursor,
        idAfter: nextIdAfter
      });
      
      // 기존 데이터에 추가
      setEmployees(prev => [...prev, ...response.content]);
      setTotalCount(response.totalElements);
      setNextCursor(response.nextCursor);
      setNextIdAfter(response.nextIdAfter);
      setHasMore(response.hasNext);
    } catch (error) {
      console.error('직원 목록을 불러오는 중 오류가 발생했습니다:', error);
      showSnackbar('직원 목록을 불러오는 중 오류가 발생했습니다.', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleLoadMore = () => {
    fetchMoreEmployees();
  };

  // 검색 버튼 클릭 시 즉시 검색 실행
  const handleSearch = () => {
    if (searchTimeout) {
      clearTimeout(searchTimeout);
      setSearchTimeout(null);
    }
    fetchInitialEmployees();
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
    setDepartmentName('');
    setPosition('');
    setStatus('');
  };

  const handleAddEmployee = () => {
    setSelectedEmployee(undefined);
    setFormModalOpen(true);
  };

  const handleEditEmployee = (employee: EmployeeDto) => {
    setSelectedEmployee(employee);
    setFormModalOpen(true);
  };

  const handleDeleteEmployee = (employee: EmployeeDto) => {
    setSelectedEmployee(employee);
    setDeleteModalOpen(true);
  };

  const handleFormSubmit = async (formData: FormData) => {
    try {
      const employeeDataBlob = formData.get('employee');
      if (!employeeDataBlob || !(employeeDataBlob instanceof Blob)) {
        throw new Error('직원 데이터가 없습니다.');
      }
      
      const employeeDataText = await employeeDataBlob.text();
      const employeeData = JSON.parse(employeeDataText);
      
      const profileImage = formData.get('profile') as File | null;
      
      if (selectedEmployee) {
        // 직원 수정
        await updateEmployee(selectedEmployee.id, employeeData, profileImage || undefined);
        showSnackbar('직원 정보가 수정되었습니다.', 'success');
      } else {
        // 직원 추가
        await createEmployee(employeeData, profileImage || undefined);
        showSnackbar('직원이 추가되었습니다.', 'success');
      }
      fetchInitialEmployees();
    } catch (error) {
      console.error('직원 저장 중 오류가 발생했습니다:', error);
      showSnackbar('직원 저장 중 오류가 발생했습니다.', 'error');
      throw error;
    }
  };

  const handleDeleteConfirm = async () => {
    if (!selectedEmployee) return;
    
    try {
      await deleteEmployee(selectedEmployee.id);
      showSnackbar('직원이 삭제되었습니다.', 'success');
      fetchInitialEmployees();
    } catch (error) {
      console.error('직원 삭제 중 오류가 발생했습니다:', error);
      showSnackbar('직원 삭제 중 오류가 발생했습니다.', 'error');
      throw error;
    }
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
        <PageTitle>직원 목록</PageTitle>
        
        <EmployeeToolbar
          searchTerm={searchTerm}
          onSearchChange={setSearchTerm}
          onSearch={handleSearch}
          sortValue={`${sortField}:${sortDirection}`}
          onSortChange={handleSortChange}
          sortOptions={sortOptions}
          onAddClick={handleAddEmployee}
          departmentName={departmentName}
          onDepartmentNameChange={setDepartmentName}
          position={position}
          onPositionChange={setPosition}
          status={status}
          onStatusChange={setStatus}
          showFilters={showFilters}
          onToggleFilters={handleToggleFilters}
          onClearFilters={handleClearFilters}
        />
        
        <Box sx={{ flex: 1, overflow: 'hidden', display: 'flex', flexDirection: 'column' }}>
          <EmployeeTable
            employees={employees}
            loading={loading}
            onEdit={handleEditEmployee}
            onDelete={handleDeleteEmployee}
            hasMore={hasMore}
            onLoadMore={handleLoadMore}
            totalCount={totalCount}
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
            총 {totalCount}명
          </Typography>
        </Paper>
      </Box>

      {/* 직원 추가/수정 모달 */}
      <EmployeeFormModal
        open={formModalOpen}
        onClose={() => setFormModalOpen(false)}
        onSubmit={handleFormSubmit}
        employee={selectedEmployee}
        title={selectedEmployee ? '직원 수정' : '직원 등록'}
        departments={departments}
      />

      {/* 직원 삭제 확인 모달 */}
      <ConfirmDeleteModal
        open={deleteModalOpen}
        onClose={() => setDeleteModalOpen(false)}
        onConfirm={handleDeleteConfirm}
        title="직원 삭제"
        content={`'${selectedEmployee?.name}' 직원을 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.`}
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

export default EmployeePage; 