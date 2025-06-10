import {
  Backup as BackupIcon,
  CalendarToday as CalendarIcon,
  Clear as ClearIcon,
  FilterList as FilterIcon,
  Search as SearchIcon
} from '@mui/icons-material';
import {
  Box,
  Button,
  Chip,
  FormControl,
  Grid,
  IconButton,
  InputAdornment,
  InputLabel,
  MenuItem,
  Select,
  SelectChangeEvent,
  TextField,
  Tooltip
} from '@mui/material';
import React, { useState } from 'react';
import { BackupStatus } from '../../api/backup/model.ts';

interface SortOption {
  value: string;
  label: string;
}

interface BackupToolbarProps {
  worker: string;
  onWorkerChange: (value: string) => void;
  status: BackupStatus | '';
  onStatusChange: (value: BackupStatus | '') => void;
  startedAtFrom: string;
  onStartedAtFromChange: (date: string) => void;
  startedAtTo: string;
  onStartedAtToChange: (date: string) => void;
  sortOption: string;
  onSortChange: (value: string) => void;
  onCreateBackup: () => void;
  onClearFilters: () => void;
  isCreatingBackup: boolean;
}

const BackupToolbar: React.FC<BackupToolbarProps> = ({
  worker,
  onWorkerChange,
  status,
  onStatusChange,
  startedAtFrom,
  onStartedAtFromChange,
  startedAtTo,
  onStartedAtToChange,
  sortOption,
  onSortChange,
  onCreateBackup,
  onClearFilters,
  isCreatingBackup
}) => {
  const [showFilters, setShowFilters] = useState(false);

  const sortOptions: SortOption[] = [
    { value: 'startedAt:DESC', label: '시작 시간 내림차순' },
    { value: 'startedAt:ASC', label: '시작 시간 오름차순' },
    { value: 'endedAt:DESC', label: '종료 시간 내림차순' },
    { value: 'endedAt:ASC', label: '종료 시간 오름차순' }
  ];

  const handleSortChange = (e: SelectChangeEvent<string>) => {
    onSortChange(e.target.value);
  };

  const handleToggleFilters = () => {
    setShowFilters(!showFilters);
  };

  const getStatusLabel = (status: BackupStatus | '') => {
    switch (status) {
      case BackupStatus.COMPLETED:
        return '완료';
      case BackupStatus.IN_PROGRESS:
        return '진행중';
      case BackupStatus.FAILED:
        return '실패';
      case BackupStatus.SKIPPED:
        return '건너뜀';
      case '':
        return '전체';
      default:
        return '알 수 없음';
    }
  };


  // 활성화된 필터 개수 계산
  const hasActiveFilters = worker || status || startedAtFrom || startedAtTo;

  return (
    <Box sx={{ mb: 3 }}>
      <Grid container spacing={2} alignItems="center">
        <Grid item xs>
          <Box sx={{ display: 'flex', gap: 2 }}>
            <TextField
              placeholder="작업자 검색"
              size="small"
              value={worker}
              onChange={(e) => onWorkerChange(e.target.value)}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <SearchIcon fontSize="small" color="action" />
                  </InputAdornment>
                ),
              }}
              sx={{ minWidth: 200 }}
            />
            <FormControl size="small">
              <Select
                value={sortOption}
                onChange={handleSortChange}
                sx={{ minWidth: 180 }}
                displayEmpty
              >
                {sortOptions.map((option) => (
                  <MenuItem key={option.value} value={option.value}>
                    {option.label}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
            <Tooltip title="필터 표시/숨기기">
              <IconButton 
                color={showFilters || hasActiveFilters ? "primary" : "default"} 
                onClick={handleToggleFilters}
              >
                <FilterIcon />
              </IconButton>
            </Tooltip>
            {hasActiveFilters && (
              <Tooltip title="필터 초기화">
                <IconButton color="default" onClick={onClearFilters}>
                  <ClearIcon />
                </IconButton>
              </Tooltip>
            )}
          </Box>
        </Grid>
        <Grid item>
          <Button
            variant="contained"
            startIcon={<BackupIcon />}
            onClick={onCreateBackup}
            disabled={isCreatingBackup}
          >
            {isCreatingBackup ? '백업 생성 중...' : '새 백업 생성'}
          </Button>
        </Grid>
      </Grid>
      
      {showFilters && (
        <Grid container spacing={2} sx={{ mt: 1 }}>
          <Grid item xs={12} sm={4}>
            <FormControl fullWidth size="small">
              <InputLabel>상태</InputLabel>
              <Select
                value={status}
                onChange={(e) => onStatusChange(e.target.value as BackupStatus | '')}
                label="상태"
              >
                <MenuItem value="">전체</MenuItem>
                <MenuItem value={BackupStatus.COMPLETED}>완료</MenuItem>
                <MenuItem value={BackupStatus.IN_PROGRESS}>진행중</MenuItem>
                <MenuItem value={BackupStatus.FAILED}>실패</MenuItem>
                <MenuItem value={BackupStatus.SKIPPED}>건너뜀</MenuItem>
              </Select>
            </FormControl>
          </Grid>
          
          <Grid item xs={12} sm={4}>
            <TextField
              label="시작 시간(부터)"
              type="date"
              value={startedAtFrom ? startedAtFrom.split('T')[0] : ''}
              onChange={(e) => onStartedAtFromChange(e.target.value)}
              fullWidth
              size="small"
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <CalendarIcon fontSize="small" color="action" />
                  </InputAdornment>
                ),
              }}
              InputLabelProps={{
                shrink: true,
              }}
              sx={{
                '& input[type="date"]::-webkit-calendar-picker-indicator': {
                  opacity: 0,
                  position: 'absolute',
                  width: '100%',
                  height: '100%',
                  cursor: 'pointer',
                  zIndex: 1
                }
              }}
            />
          </Grid>
          
          <Grid item xs={12} sm={4}>
            <TextField
              label="시작 시간(까지)"
              type="date"
              value={startedAtTo ? startedAtTo.split('T')[0] : ''}
              onChange={(e) => onStartedAtToChange(e.target.value)}
              fullWidth
              size="small"
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <CalendarIcon fontSize="small" color="action" />
                  </InputAdornment>
                ),
              }}
              InputLabelProps={{
                shrink: true,
              }}
              sx={{
                '& input[type="date"]::-webkit-calendar-picker-indicator': {
                  opacity: 0,
                  position: 'absolute',
                  width: '100%',
                  height: '100%',
                  cursor: 'pointer',
                  zIndex: 1
                }
              }}
            />
          </Grid>
        </Grid>
      )}
      
      {hasActiveFilters && !showFilters && (
        <Box sx={{ mt: 1, display: 'flex', flexWrap: 'wrap', gap: 1 }}>
          {status && (
            <Chip 
              label={`상태: ${getStatusLabel(status)}`} 
              size="small" 
              onDelete={() => onStatusChange('')}
              color="primary"
              variant="outlined"
            />
          )}
          {startedAtFrom && (
            <Chip 
              label={`시작일(부터): ${startedAtFrom.split('T')[0]}`} 
              size="small" 
              onDelete={() => onStartedAtFromChange('')}
              color="primary"
              variant="outlined"
            />
          )}
          {startedAtTo && (
            <Chip 
              label={`시작일(까지): ${startedAtTo.split('T')[0]}`} 
              size="small" 
              onDelete={() => onStartedAtToChange('')}
              color="primary"
              variant="outlined"
            />
          )}
          {worker && (
            <Chip 
              label={`작업자: ${worker}`} 
              size="small" 
              onDelete={() => onWorkerChange('')}
              color="primary"
              variant="outlined"
            />
          )}
        </Box>
      )}
    </Box>
  );
};

export default BackupToolbar; 