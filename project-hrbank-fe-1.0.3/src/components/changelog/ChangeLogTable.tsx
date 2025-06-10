import {
  Info as InfoIcon
} from '@mui/icons-material';
import {
  Box,
  Chip,
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
import { ChangeLogDto, ChangeLogType } from '../../api/changelog/model.ts';

interface ChangeLogTableProps {
  changeLogs: ChangeLogDto[];
  loading: boolean;
  hasMore: boolean;
  onLoadMore: () => void;
  totalCount: number;
  onViewDetails: (changeLog: ChangeLogDto) => void;
}

const ChangeLogTable: React.FC<ChangeLogTableProps> = ({
  changeLogs,
  loading,
  hasMore,
  onLoadMore,
  onViewDetails
}) => {
  const formatDateTime = (dateTimeString: string) => {
    const date = new Date(dateTimeString);
    return date.toLocaleString('ko-KR', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
      hour12: false
    }).replace(/\. /g, '.').replace(/\.$/, '');
  };

  const getTypeColor = (type: ChangeLogType) => {
    switch (type) {
      case ChangeLogType.CREATED:
        return 'success';
      case ChangeLogType.UPDATED:
        return 'primary';
      case ChangeLogType.DELETED:
        return 'error';
      default:
        return 'default';
    }
  };

  const getTypeLabel = (type: ChangeLogType) => {
    switch (type) {
      case ChangeLogType.CREATED:
        return '생성';
      case ChangeLogType.UPDATED:
        return '수정';
      case ChangeLogType.DELETED:
        return '삭제';
      default:
        return '알 수 없음';
    }
  };

  if (loading && changeLogs.length === 0) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', my: 4 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (changeLogs.length === 0) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', my: 8 }}>
        <Typography color="text.secondary">
          수정 이력이 없습니다.
        </Typography>
      </Box>
    );
  }

  return (
    <TableContainer 
      component={Paper} 
      elevation={0}
      id="changeLogTableContainer"
      sx={{ 
        flex: 1,
        overflow: 'auto',
        border: '1px solid #eee',
        borderRadius: 1
      }}
    >
      <InfiniteScroll
        dataLength={changeLogs.length}
        next={onLoadMore}
        hasMore={hasMore}
        loader={
          <Box sx={{ display: 'flex', justifyContent: 'center', py: 2 }}>
            <CircularProgress size={24} />
          </Box>
        }
        scrollableTarget="changeLogTableContainer"
      >
        <Table stickyHeader>
          <TableHead>
            <TableRow>
              <TableCell>유형</TableCell>
              <TableCell>사번</TableCell>
              <TableCell>내용</TableCell>
              <TableCell>IP 주소</TableCell>
              <TableCell>일시</TableCell>
              <TableCell align="center">상세</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {changeLogs.map((changeLog) => (
              <TableRow key={changeLog.id} hover>
                <TableCell>
                  <Chip 
                    label={getTypeLabel(changeLog.type)} 
                    color={getTypeColor(changeLog.type) as any}
                    size="small"
                    variant="outlined"
                  />
                </TableCell>
                <TableCell>{changeLog.employeeNumber}</TableCell>
                <TableCell>{changeLog.memo}</TableCell>
                <TableCell>{changeLog.ipAddress}</TableCell>
                <TableCell>{formatDateTime(changeLog.at)}</TableCell>
                <TableCell align="center">
                  <Tooltip title="상세 보기">
                    <IconButton 
                      size="small" 
                      color="primary"
                      onClick={() => onViewDetails(changeLog)}
                    >
                      <InfoIcon fontSize="small" />
                    </IconButton>
                  </Tooltip>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </InfiniteScroll>
    </TableContainer>
  );
};

export default ChangeLogTable; 