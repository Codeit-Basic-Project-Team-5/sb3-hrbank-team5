import {
  Box,
  Paper,
  SelectChangeEvent,
  Typography
} from '@mui/material';
import React, { useEffect, useState } from 'react';
import {
  createDepartment,
  deleteDepartment,
  getDepartments,
  updateDepartment
} from '../api/department/api.ts';
import {
  DepartmentCreateRequest,
  DepartmentDto,
  DepartmentUpdateRequest
} from '../api/department/model.ts';
import { ConfirmDeleteModal, DepartmentFormModal, PageContainer, PageTitle } from '../components/common';
import DepartmentTable from '../components/department/DepartmentTable.tsx';
import DepartmentToolbar from '../components/department/DepartmentToolbar.tsx';
import { useError } from '../contexts/ErrorContext.tsx';

const DepartmentPage: React.FC = () => {
  const { showMessage } = useError();
  const [departments, setDepartments] = useState<DepartmentDto[]>([]);
  const [loading, setLoading] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const [sortField, setSortField] = useState('name');
  const [sortDirection, setSortDirection] = useState<'asc' | 'desc'>('asc');
  const [totalCount, setTotalCount] = useState(0);
  const [hasMore, setHasMore] = useState(false);
  const [nextCursor, setNextCursor] = useState<string | undefined>(undefined);
  const [nextIdAfter, setNextIdAfter] = useState<number | undefined>(undefined);
  
  // 모달 상태 관리
  const [formModalOpen, setFormModalOpen] = useState(false);
  const [deleteModalOpen, setDeleteModalOpen] = useState(false);
  const [selectedDepartment, setSelectedDepartment] = useState<DepartmentDto | undefined>(undefined);

  const sortOptions = [
    { value: 'name:asc', label: '부서명 오름차순' },
    { value: 'name:desc', label: '부서명 내림차순' },
    { value: 'establishedDate:asc', label: '설립일 오름차순' },
    { value: 'establishedDate:desc', label: '설립일 내림차순' },
  ];

  useEffect(() => {
    // 검색어나 정렬 조건이 변경되면 처음부터 다시 로드
    fetchInitialDepartments();
  }, [searchTerm, sortField, sortDirection]);

  // 초기 데이터 로드 (페이지네이션 초기화)
  const fetchInitialDepartments = async () => {
    if (loading) return;
    
    // 페이지네이션 초기화
    setDepartments([]);
    setNextCursor(undefined);
    setNextIdAfter(undefined);
    setHasMore(true);
    
    setLoading(true);
    try {
      const response = await getDepartments({
        nameOrDescription: searchTerm || undefined,
        sortField,
        sortDirection,
        size: 30
      });
      
      setDepartments(response.content);
      setTotalCount(response.totalElements);
      setNextCursor(response.nextCursor);
      setNextIdAfter(response.nextIdAfter);
      setHasMore(response.hasNext);
    } catch (error) {
      console.error('부서 목록을 불러오는 중 오류가 발생했습니다:', error);
      showMessage('부서 목록을 불러오는 중 오류가 발생했습니다.', 'error');
    } finally {
      setLoading(false);
    }
  };

  // 추가 데이터 로드 (무한 스크롤)
  const fetchMoreDepartments = async () => {
    if (loading || !hasMore) return;
    
    setLoading(true);
    try {
      const response = await getDepartments({
        nameOrDescription: searchTerm || undefined,
        sortField,
        sortDirection,
        size: 30,
        cursor: nextCursor,
        idAfter: nextIdAfter
      });
      
      // 기존 데이터에 추가
      setDepartments(prev => [...prev, ...response.content]);
      setTotalCount(response.totalElements);
      setNextCursor(response.nextCursor);
      setNextIdAfter(response.nextIdAfter);
      setHasMore(response.hasNext);
    } catch (error) {
      console.error('부서 목록을 불러오는 중 오류가 발생했습니다:', error);
      showMessage('부서 목록을 불러오는 중 오류가 발생했습니다.', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleLoadMore = () => {
    fetchMoreDepartments();
  };

  const handleSearch = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      fetchInitialDepartments();
    }
  };

  const handleSortChange = (e: SelectChangeEvent<string>) => {
    const [field, direction] = e.target.value.split(':');
    setSortField(field);
    setSortDirection(direction as 'asc' | 'desc');
  };

  const handleAddDepartment = () => {
    setSelectedDepartment(undefined);
    setFormModalOpen(true);
  };

  const handleEditDepartment = (department: DepartmentDto) => {
    setSelectedDepartment(department);
    setFormModalOpen(true);
  };

  const handleDeleteDepartment = (department: DepartmentDto) => {
    setSelectedDepartment(department);
    setDeleteModalOpen(true);
  };

  const handleFormSubmit = async (data: DepartmentCreateRequest | DepartmentUpdateRequest) => {
    try {
      if (selectedDepartment) {
        // 부서 수정
        await updateDepartment(selectedDepartment.id, data as DepartmentUpdateRequest);
        showMessage('부서 정보가 수정되었습니다.', 'success');
      } else {
        // 부서 추가
        await createDepartment(data as DepartmentCreateRequest);
        showMessage('부서가 추가되었습니다.', 'success');
      }
      fetchInitialDepartments();
    } catch (error) {
      console.error('부서 저장 중 오류가 발생했습니다:', error);
      // 에러는 전역 에러 핸들러에서 처리됨
      throw error;
    }
  };

  const handleDeleteConfirm = async () => {
    if (!selectedDepartment) return;
    
    try {
      await deleteDepartment(selectedDepartment.id);
      showMessage('부서가 삭제되었습니다.', 'success');
      fetchInitialDepartments();
    } catch (error) {
      console.error('부서 삭제 중 오류가 발생했습니다:', error);
      // 에러는 전역 에러 핸들러에서 처리됨
      throw error;
    }
  };

  return (
    <PageContainer>
      <Box sx={{ display: 'flex', flexDirection: 'column', height: 'calc(100vh - 120px)' }}>
        <PageTitle>부서 관리</PageTitle>
        
        <DepartmentToolbar
          searchTerm={searchTerm}
          onSearchChange={setSearchTerm}
          onSearch={handleSearch}
          sortValue={`${sortField}:${sortDirection}`}
          sortOptions={sortOptions}
          onSortChange={handleSortChange}
          onAddClick={handleAddDepartment}
        />
        <Box sx={{ flex: 1, overflow: 'hidden', display: 'flex', flexDirection: 'column' }}>
          <DepartmentTable
            departments={departments}
            loading={loading}
            hasMore={hasMore}
            totalCount={totalCount}
            onLoadMore={handleLoadMore}
            onEdit={handleEditDepartment}
            onDelete={handleDeleteDepartment}
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
            총 {totalCount}팀
          </Typography>
        </Paper>
      </Box>
      
      <DepartmentFormModal
        open={formModalOpen}
        onClose={() => setFormModalOpen(false)}
        onSubmit={handleFormSubmit}
        department={selectedDepartment}
        title={selectedDepartment ? '부서 수정' : '부서 추가'}
      />

      <ConfirmDeleteModal
        open={deleteModalOpen}
        onClose={() => setDeleteModalOpen(false)}
        onConfirm={handleDeleteConfirm}
        title="부서 삭제"
        content={`'${selectedDepartment?.name}' 부서를 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.`}
      />
    </PageContainer>
  );
};

export default DepartmentPage; 