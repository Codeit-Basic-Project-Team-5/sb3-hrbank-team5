import {
  GetApp as DownloadIcon,
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
import { BackupDto, BackupStatus } from '../../api/backup/model.ts';

interface BackupTableProps {
  backups: BackupDto[];
  loading: boolean;
  hasMore: boolean;
  totalCount: number;
  onLoadMore: () => void;
  onDownload: (backup: BackupDto) => void;
}

const BackupTable: React.FC<BackupTableProps> = ({
  backups,
  loading,
  hasMore,
  onLoadMore,
  onDownload
}) => {
  // 날짜 포맷팅 함수
  const formatDateTime = (dateTimeString?: string) => {
    if (!dateTimeString) return '-';
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

  // 상태에 따른 색상 결정
  const getStatusColor = (status: BackupStatus) => {
    switch (status) {
      case BackupStatus.COMPLETED:
        return 'success';
      case BackupStatus.IN_PROGRESS:
        return 'warning';
      case BackupStatus.FAILED:
        return 'error';
      case BackupStatus.SKIPPED:
        return 'default';
      default:
        return 'default';
    }
  };

  // 상태 라벨 표시
  const getStatusLabel = (status: BackupStatus) => {
    switch (status) {
      case BackupStatus.COMPLETED:
        return '완료';
      case BackupStatus.IN_PROGRESS:
        return '진행중';
      case BackupStatus.FAILED:
        return '실패';
      case BackupStatus.SKIPPED:
        return '건너뜀';
      default:
        return '알 수 없음';
    }
  };

  if (loading && backups.length === 0) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', my: 4 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (backups.length === 0) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', my: 8 }}>
        <Typography color="text.secondary">
          백업 내역이 없습니다.
        </Typography>
      </Box>
    );
  }

  return (
    <TableContainer 
      component={Paper} 
      elevation={0}
      id="backupTableContainer"
      sx={{ 
        flex: 1,
        overflow: 'auto',
        border: '1px solid #eee',
        borderRadius: 1
      }}
    >
      <InfiniteScroll
        dataLength={backups.length}
        next={onLoadMore}
        hasMore={hasMore}
        loader={
          <Box sx={{ display: 'flex', justifyContent: 'center', py: 2 }}>
            <CircularProgress size={24} />
          </Box>
        }
        scrollableTarget="backupTableContainer"
      >
        <Table stickyHeader>
          <TableHead>
            <TableRow>
              <TableCell width="10%">ID</TableCell>
              <TableCell width="15%">작업자</TableCell>
              <TableCell width="20%">시작 시간</TableCell>
              <TableCell width="20%">종료 시간</TableCell>
              <TableCell width="15%">상태</TableCell>
              <TableCell width="10%" align="center">다운로드</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {backups.map((backup) => (
              <TableRow key={backup.id} hover>
                <TableCell>{backup.id}</TableCell>
                <TableCell>{backup.worker}</TableCell>
                <TableCell>{formatDateTime(backup.startedAt)}</TableCell>
                <TableCell>{formatDateTime(backup.endedAt)}</TableCell>
                <TableCell>
                  <Chip
                    label={getStatusLabel(backup.status)}
                    color={getStatusColor(backup.status) as any}
                    size="small"
                    variant="outlined"
                  />
                </TableCell>
                <TableCell align="center">
                  {backup.status === BackupStatus.COMPLETED && backup.fileId ? (
                    <Tooltip title="백업 파일 다운로드">
                      <IconButton
                        size="small"
                        onClick={() => onDownload(backup)}
                        color="primary"
                      >
                        <DownloadIcon />
                      </IconButton>
                    </Tooltip>
                  ) : (
                    <Box sx={{ width: 24, height: 24, mx: 'auto' }} />
                  )}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </InfiniteScroll>
    </TableContainer>
  );
};

export default BackupTable; 