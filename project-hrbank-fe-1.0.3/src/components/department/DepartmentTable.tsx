import {
  Delete as DeleteIcon,
  Edit as EditIcon
} from '@mui/icons-material';
import {
  Box,
  CircularProgress,
  IconButton,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Tooltip,
  Typography
} from '@mui/material';
import React from 'react';
import InfiniteScroll from 'react-infinite-scroll-component';
import { DepartmentDto } from '../../api/department/model.ts';

interface DepartmentTableProps {
  departments: DepartmentDto[];
  loading: boolean;
  onEdit: (department: DepartmentDto) => void;
  onDelete: (department: DepartmentDto) => void;
  hasMore: boolean;
  onLoadMore: () => void;
  totalCount: number;
}

const DepartmentTable: React.FC<DepartmentTableProps> = ({
  departments,
  loading,
  onEdit,
  onDelete,
  hasMore,
  onLoadMore,
}) => {
  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('ko-KR', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
    }).replace(/\. /g, '.').replace(/\.$/, '');
  };

  if (loading && departments.length === 0) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', my: 4 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (departments.length === 0) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', my: 8 }}>
        <Typography color="text.secondary">
          등록된 부서가 없습니다.
        </Typography>
      </Box>
    );
  }

  return (
    <TableContainer 
      component={Paper} 
      elevation={0}
      id="departmentTableContainer"
      sx={{ 
        flex: 1,
        overflow: 'auto',
        border: '1px solid #eee',
        borderRadius: 1
      }}
    >
      <InfiniteScroll
        dataLength={departments.length}
        next={onLoadMore}
        hasMore={hasMore}
        loader={
          <Box sx={{ display: 'flex', justifyContent: 'center', py: 2 }}>
            <CircularProgress size={24} />
          </Box>
        }
        scrollableTarget="departmentTableContainer"
      >
        <Table stickyHeader>
          <TableHead>
            <TableRow>
              <TableCell>부서명</TableCell>
              <TableCell>설명</TableCell>
              <TableCell align="center">직원수</TableCell>
              <TableCell align="center">설립일</TableCell>
              <TableCell align="center">관리</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {departments.map((department) => (
              <TableRow key={department.id} hover>
                <TableCell>{department.name}</TableCell>
                <TableCell>{department.description}</TableCell>
                <TableCell align="center">{department.employeeCount}명</TableCell>
                <TableCell align="center">{formatDate(department.establishedDate)}</TableCell>
                <TableCell align="center">
                  <Box>
                    <Tooltip title="수정">
                      <IconButton 
                        size="small" 
                        color="primary"
                        onClick={() => onEdit(department)}
                      >
                        <EditIcon fontSize="small" />
                      </IconButton>
                    </Tooltip>
                    <Tooltip title="삭제">
                      <IconButton 
                        size="small" 
                        color="error"
                        onClick={() => onDelete(department)}
                      >
                        <DeleteIcon fontSize="small" />
                      </IconButton>
                    </Tooltip>
                  </Box>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </InfiniteScroll>
    </TableContainer>
  );
};

export default DepartmentTable; 