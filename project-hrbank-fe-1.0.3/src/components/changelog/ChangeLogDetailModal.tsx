import { Close as CloseIcon } from '@mui/icons-material';
import {
  Box,
  Button,
  Chip,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Divider,
  IconButton,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography
} from '@mui/material';
import React, { useEffect, useState } from 'react';
import { getChangeLogDiffs } from '../../api/changelog/api.ts';
import { ChangeLogDto, ChangeLogType, DiffDto } from '../../api/changelog/model.ts';

interface ChangeLogDetailModalProps {
  open: boolean;
  onClose: () => void;
  changeLog?: ChangeLogDto;
}

const ChangeLogDetailModal: React.FC<ChangeLogDetailModalProps> = ({
  open,
  onClose,
  changeLog
}) => {
  const [diffs, setDiffs] = useState<DiffDto[]>([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (open && changeLog) {
      fetchDiffs();
    } else {
      setDiffs([]);
    }
  }, [open, changeLog]);

  const fetchDiffs = async () => {
    if (!changeLog) return;
    
    setLoading(true);
    try {
      const data = await getChangeLogDiffs(changeLog.id);
      setDiffs(data);
    } catch (error) {
      console.error('변경 내용을 불러오는 중 오류가 발생했습니다:', error);
    } finally {
      setLoading(false);
    }
  };

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

  const getPropertyLabel = (propertyName: string) => {
    switch (propertyName) {
      case 'name':
        return '이름';
      case 'email':
        return '이메일';
      case 'departmentId':
        return '부서 ID';
      case 'department':
        return '부서명';
      case 'position':
        return '직함';
      case 'employeeNumber':
        return '사번';
      case 'hireDate':
        return '입사일';
      case 'status':
        return '상태';
      case 'profileImageId':
        return '프로필 이미지';
      default:
        return propertyName;
    }
  };

  if (!changeLog) {
    return null;
  }

  return (
    <Dialog 
      open={open} 
      onClose={onClose}
      maxWidth="sm"
      fullWidth
    >
      <DialogTitle sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Typography variant="h6">수정 이력 상세</Typography>
        <IconButton onClick={onClose} size="small">
          <CloseIcon />
        </IconButton>
      </DialogTitle>
      <DialogContent dividers>
        <Box sx={{ mb: 3 }}>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
            <Box>
              <Typography variant="subtitle2" color="text.secondary">유형</Typography>
              <Chip 
                label={getTypeLabel(changeLog.type)} 
                color={getTypeColor(changeLog.type) as any}
                size="small"
                variant="outlined"
                sx={{ mt: 0.5 }}
              />
            </Box>
            <Box>
              <Typography variant="subtitle2" color="text.secondary">사번</Typography>
              <Typography variant="body1">{changeLog.employeeNumber}</Typography>
            </Box>
            <Box>
              <Typography variant="subtitle2" color="text.secondary">IP 주소</Typography>
              <Typography variant="body1">{changeLog.ipAddress}</Typography>
            </Box>
            <Box>
              <Typography variant="subtitle2" color="text.secondary">일시</Typography>
              <Typography variant="body1">{formatDateTime(changeLog.at)}</Typography>
            </Box>
          </Box>
          
          <Typography variant="subtitle2" color="text.secondary">내용</Typography>
          <Typography variant="body1" sx={{ mt: 0.5 }}>{changeLog.memo}</Typography>
        </Box>
        
        <Divider sx={{ my: 2 }} />
        
        <Typography variant="h6" sx={{ mb: 2 }}>변경 상세 내용</Typography>
        
        {loading ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', my: 4 }}>
            <CircularProgress />
          </Box>
        ) : diffs.length === 0 ? (
          <Typography color="text.secondary" sx={{ textAlign: 'center', my: 4 }}>
            변경 상세 내용이 없습니다.
          </Typography>
        ) : (
          <TableContainer component={Paper} variant="outlined">
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell width="30%">항목</TableCell>
                  <TableCell width="35%">변경 전</TableCell>
                  <TableCell width="35%">변경 후</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {diffs.map((diff, index) => (
                  <TableRow key={index} hover>
                    <TableCell>{getPropertyLabel(diff.propertyName)}</TableCell>
                    <TableCell>{diff.before || '-'}</TableCell>
                    <TableCell>{diff.after || '-'}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        )}
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>닫기</Button>
      </DialogActions>
    </Dialog>
  );
};

export default ChangeLogDetailModal; 