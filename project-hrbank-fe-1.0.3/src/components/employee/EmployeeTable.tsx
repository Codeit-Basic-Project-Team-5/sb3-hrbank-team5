import {
  Delete as DeleteIcon,
  Edit as EditIcon
} from '@mui/icons-material';
import {
  Avatar,
  Box,
  Chip,
  CircularProgress,
  IconButton,
  Paper,
  Stack,
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
import { getProfileImageUrl } from '../../api/employee/api.ts';
import { EmployeeDto, EmployeeStatus } from '../../api/employee/model.ts';

interface EmployeeTableProps {
  employees: EmployeeDto[];
  loading: boolean;
  onEdit: (employee: EmployeeDto) => void;
  onDelete: (employee: EmployeeDto) => void;
  hasMore: boolean;
  onLoadMore: () => void;
  totalCount: number;
}

const EmployeeTable: React.FC<EmployeeTableProps> = ({
  employees,
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

  const getStatusColor = (status: EmployeeStatus) => {
    switch (status) {
      case EmployeeStatus.ACTIVE:
        return 'success';
      case EmployeeStatus.ON_LEAVE:
        return 'warning';
      case EmployeeStatus.RESIGNED:
        return 'error';
      default:
        return 'default';
    }
  };

  const getStatusLabel = (status: EmployeeStatus) => {
    switch (status) {
      case EmployeeStatus.ACTIVE:
        return '재직중';
      case EmployeeStatus.ON_LEAVE:
        return '휴직중';
      case EmployeeStatus.RESIGNED:
        return '퇴사';
      default:
        return '알 수 없음';
    }
  };

  // 이름의 첫 글자를 가져오는 함수
  const getInitials = (name: string) => {
    return name.charAt(0).toUpperCase();
  };

  // 프로필 이미지 배경색을 결정하는 함수
  const getAvatarColor = (name: string) => {
    const colors = [
      '#1976d2', '#388e3c', '#d32f2f', '#7b1fa2', 
      '#c2185b', '#0288d1', '#303f9f', '#689f38', 
      '#fbc02d', '#ef6c00'
    ];
    const index = name.charCodeAt(0) % colors.length;
    return colors[index];
  };

  if (loading && employees.length === 0) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', my: 4 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (employees.length === 0) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', my: 8 }}>
        <Typography color="text.secondary">
          등록된 직원이 없습니다.
        </Typography>
      </Box>
    );
  }

  return (
    <TableContainer 
      component={Paper} 
      elevation={0}
      id="employeeTableContainer"
      sx={{ 
        flex: 1,
        overflow: 'auto',
        border: '1px solid #eee',
        borderRadius: 1
      }}
    >
      <InfiniteScroll
        dataLength={employees.length}
        next={onLoadMore}
        hasMore={hasMore}
        loader={
          <Box sx={{ display: 'flex', justifyContent: 'center', py: 2 }}>
            <CircularProgress size={24} />
          </Box>
        }
        scrollableTarget="employeeTableContainer"
      >
        <Table stickyHeader>
          <TableHead>
            <TableRow>
              <TableCell>직원 정보</TableCell>
              <TableCell>사번</TableCell>
              <TableCell>부서</TableCell>
              <TableCell>직함</TableCell>
              <TableCell align="center">입사일</TableCell>
              <TableCell align="center">상태</TableCell>
              <TableCell align="center">관리</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {employees.map((employee) => (
              <TableRow key={employee.id} hover>
                <TableCell>
                  <Stack direction="row" spacing={2} alignItems="center">
                    <Avatar 
                      src={getProfileImageUrl(employee.profileImageId)}
                      alt={employee.name}
                      sx={{ 
                        width: 40, 
                        height: 40,
                        bgcolor: !employee.profileImageId ? getAvatarColor(employee.name) : undefined
                      }}
                    >
                      {!employee.profileImageId && getInitials(employee.name)}
                    </Avatar>
                    <Box>
                      <Typography variant="body1" fontWeight="medium">
                        {employee.name}
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        {employee.email}
                      </Typography>
                    </Box>
                  </Stack>
                </TableCell>
                <TableCell>{employee.employeeNumber}</TableCell>
                <TableCell>{employee.departmentName}</TableCell>
                <TableCell>{employee.position}</TableCell>
                <TableCell align="center">{formatDate(employee.hireDate)}</TableCell>
                <TableCell align="center">
                  <Chip 
                    label={getStatusLabel(employee.status)} 
                    color={getStatusColor(employee.status) as any}
                    size="small"
                    variant="outlined"
                  />
                </TableCell>
                <TableCell align="center">
                  <Box>
                    <Tooltip title="수정">
                      <IconButton 
                        size="small" 
                        color="primary"
                        onClick={() => onEdit(employee)}
                      >
                        <EditIcon fontSize="small" />
                      </IconButton>
                    </Tooltip>
                    <Tooltip title="삭제">
                      <IconButton 
                        size="small" 
                        color="error"
                        onClick={() => onDelete(employee)}
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

export default EmployeeTable; 