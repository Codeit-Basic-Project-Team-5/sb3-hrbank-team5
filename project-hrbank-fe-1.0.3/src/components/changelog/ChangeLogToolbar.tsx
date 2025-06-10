import {
  CalendarMonth as CalendarIcon,
  Clear as ClearIcon,
  FilterList as FilterIcon,
  Search as SearchIcon
} from '@mui/icons-material';
import {
  Box,
  Chip,
  FormControl,
  Grid,
  IconButton,
  InputAdornment,
  InputLabel,
  MenuItem,
  Select,
  SelectChangeEvent,
  Stack,
  TextField,
  Tooltip
} from '@mui/material';
import React from 'react';
import { ChangeLogType } from '../../api/changelog/model.ts';

interface SortOption {
  value: string;
  label: string;
}

interface ChangeLogToolbarProps {
  employeeNumber: string;
  onEmployeeNumberChange: (value: string) => void;
  memo: string;
  onMemoChange: (value: string) => void;
  ipAddress: string;
  onIpAddressChange: (value: string) => void;
  type: ChangeLogType | '';
  onTypeChange: (value: ChangeLogType | '') => void;
  atFrom: Date | null;
  onAtFromChange: (date: Date | null) => void;
  atTo: Date | null;
  onAtToChange: (date: Date | null) => void;
  sortValue: string;
  onSortChange: (e: SelectChangeEvent<string>) => void;
  sortOptions: SortOption[];
  onSearch: () => void;
  showFilters: boolean;
  onToggleFilters: () => void;
  onClearFilters: () => void;
}

const ChangeLogToolbar: React.FC<ChangeLogToolbarProps> = ({
  employeeNumber,
  onEmployeeNumberChange,
  memo,
  onMemoChange,
  ipAddress,
  onIpAddressChange,
  type,
  onTypeChange,
  atFrom,
  onAtFromChange,
  atTo,
  onAtToChange,
  sortValue,
  onSortChange,
  sortOptions,
  showFilters,
  onToggleFilters,
  onClearFilters
}) => {
  const getTypeLabel = (type: ChangeLogType) => {
    switch (type) {
      case ChangeLogType.CREATED:
        return '생성';
      case ChangeLogType.UPDATED:
        return '수정';
      case ChangeLogType.DELETED:
        return '삭제';
      default:
        return '전체';
    }
  };

  // 날짜를 'YYYY-MM-DD' 형식의 문자열로 변환 (화면 표시용)
  const formatDateToString = (date: Date | null): string => {
    if (!date) return '';
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  };

  // 문자열을 Date 객체로 변환
  const parseStringToDate = (dateString: string): Date | null => {
    if (!dateString) return null;
    
    try {
      const date = new Date(dateString);
      if (isNaN(date.getTime())) return null;
      return date;
    } catch (error) {
      return null;
    }
  };

  const handleFromDateChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const date = parseStringToDate(e.target.value);
    onAtFromChange(date);
  };

  const handleToDateChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const date = parseStringToDate(e.target.value);
    onAtToChange(date);
  };

  const hasActiveFilters = employeeNumber || memo || ipAddress || type || atFrom || atTo;

  return (
    <Box sx={{ mb: 3 }}>
      <Grid container spacing={2} alignItems="center">
        <Grid item xs>
          <Box sx={{ display: 'flex', gap: 2 }}>
            <TextField
              placeholder="사번"
              size="small"
              value={employeeNumber}
              onChange={(e) => onEmployeeNumberChange(e.target.value)}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <SearchIcon fontSize="small" color="action" />
                  </InputAdornment>
                ),
              }}
              sx={{ minWidth: 150 }}
            />
            <FormControl size="small">
              <Select
                value={sortValue}
                onChange={onSortChange}
                sx={{ minWidth: 180 }}
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
                onClick={onToggleFilters}
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
      </Grid>

      {showFilters && (
        <Grid container spacing={2} sx={{ mt: 1 }}>
          <Grid item xs={12} sm={2}>
            <TextField
              label="내용"
              size="small"
              fullWidth
              value={memo}
              onChange={(e) => onMemoChange(e.target.value)}
            />
          </Grid>
          <Grid item xs={12} sm={2}>
            <TextField
              label="IP 주소"
              size="small"
              fullWidth
              value={ipAddress}
              onChange={(e) => onIpAddressChange(e.target.value)}
            />
          </Grid>
          <Grid item xs={12} sm={2}>
            <FormControl fullWidth size="small">
              <InputLabel>유형</InputLabel>
              <Select
                value={type}
                label="유형"
                onChange={(e) => onTypeChange(e.target.value as ChangeLogType | '')}
              >
                <MenuItem value="">전체</MenuItem>
                <MenuItem value={ChangeLogType.CREATED}>생성</MenuItem>
                <MenuItem value={ChangeLogType.UPDATED}>수정</MenuItem>
                <MenuItem value={ChangeLogType.DELETED}>삭제</MenuItem>
              </Select>
            </FormControl>
          </Grid>
          <Grid item xs={12} sm={3}>
            <TextField
              label="시작일"
              type="date"
              size="small"
              fullWidth
              value={formatDateToString(atFrom)}
              onChange={handleFromDateChange}
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
          <Grid item xs={12} sm={3}>
            <TextField
              label="종료일"
              type="date"
              size="small"
              fullWidth
              value={formatDateToString(atTo)}
              onChange={handleToDateChange}
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

      {hasActiveFilters && (
        <Stack direction="row" spacing={1} sx={{ mt: 2 }}>
          {employeeNumber && (
            <Chip 
              label={`사번: ${employeeNumber}`} 
              onDelete={() => onEmployeeNumberChange('')}
              size="small"
            />
          )}
          {memo && (
            <Chip 
              label={`내용: ${memo}`} 
              onDelete={() => onMemoChange('')}
              size="small"
            />
          )}
          {ipAddress && (
            <Chip 
              label={`IP: ${ipAddress}`} 
              onDelete={() => onIpAddressChange('')}
              size="small"
            />
          )}
          {type && (
            <Chip 
              label={`유형: ${getTypeLabel(type)}`} 
              onDelete={() => onTypeChange('')}
              size="small"
            />
          )}
          {atFrom && (
            <Chip 
              label={`시작일: ${formatDateToString(atFrom)}`} 
              onDelete={() => onAtFromChange(null)}
              size="small"
            />
          )}
          {atTo && (
            <Chip 
              label={`종료일: ${formatDateToString(atTo)}`} 
              onDelete={() => onAtToChange(null)}
              size="small"
            />
          )}
        </Stack>
      )}
    </Box>
  );
};

export default ChangeLogToolbar; 