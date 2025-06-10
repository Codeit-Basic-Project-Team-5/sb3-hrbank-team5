import {
  Add as AddIcon,
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
  Stack,
  TextField,
  Tooltip
} from '@mui/material';
import React from 'react';
import { EmployeeStatus } from '../../api/employee/model.ts';

interface SortOption {
  value: string;
  label: string;
}

interface EmployeeToolbarProps {
  searchTerm: string;
  onSearchChange: (value: string) => void;
  onSearch: () => void;
  sortValue: string;
  onSortChange: (e: SelectChangeEvent<string>) => void;
  sortOptions: SortOption[];
  onAddClick: () => void;
  
  // 추가 필터링 옵션
  departmentName: string;
  onDepartmentNameChange: (value: string) => void;
  position: string;
  onPositionChange: (value: string) => void;
  status: EmployeeStatus | '';
  onStatusChange: (value: EmployeeStatus | '') => void;
  showFilters: boolean;
  onToggleFilters: () => void;
  onClearFilters: () => void;
}

const EmployeeToolbar: React.FC<EmployeeToolbarProps> = ({
  searchTerm,
  onSearchChange,
  sortValue,
  onSortChange,
  sortOptions,
  onAddClick,
  departmentName,
  onDepartmentNameChange,
  position,
  onPositionChange,
  status,
  onStatusChange,
  showFilters,
  onToggleFilters,
  onClearFilters
}) => {
  const getStatusLabel = (status: EmployeeStatus) => {
    switch (status) {
      case EmployeeStatus.ACTIVE:
        return '재직중';
      case EmployeeStatus.ON_LEAVE:
        return '휴직중';
      case EmployeeStatus.RESIGNED:
        return '퇴사';
      default:
        return '전체';
    }
  };

  const hasActiveFilters = departmentName || position || status;

  return (
    <Box sx={{ mb: 3 }}>
      <Grid container spacing={2} alignItems="center">
        <Grid item xs>
          <Box sx={{ display: 'flex', gap: 2 }}>
            <TextField
              placeholder="이름 또는 이메일"
              size="small"
              value={searchTerm}
              onChange={(e) => onSearchChange(e.target.value)}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <SearchIcon fontSize="small" color="action" />
                  </InputAdornment>
                ),
              }}
              sx={{ minWidth: 250 }}
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
        <Grid item>
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={onAddClick}
          >
            직원 등록
          </Button>
        </Grid>
      </Grid>

      {showFilters && (
        <Grid container spacing={2} sx={{ mt: 1 }}>
          <Grid item xs={12} sm={4}>
            <TextField
              label="부서명"
              size="small"
              fullWidth
              value={departmentName}
              onChange={(e) => onDepartmentNameChange(e.target.value)}
            />
          </Grid>
          <Grid item xs={12} sm={4}>
            <TextField
              label="직함"
              size="small"
              fullWidth
              value={position}
              onChange={(e) => onPositionChange(e.target.value)}
            />
          </Grid>
          <Grid item xs={12} sm={4}>
            <FormControl fullWidth size="small">
              <InputLabel>상태</InputLabel>
              <Select
                value={status}
                label="상태"
                onChange={(e) => onStatusChange(e.target.value as EmployeeStatus | '')}
              >
                <MenuItem value="">전체</MenuItem>
                <MenuItem value={EmployeeStatus.ACTIVE}>재직중</MenuItem>
                <MenuItem value={EmployeeStatus.ON_LEAVE}>휴직중</MenuItem>
                <MenuItem value={EmployeeStatus.RESIGNED}>퇴사</MenuItem>
              </Select>
            </FormControl>
          </Grid>
        </Grid>
      )}

      {hasActiveFilters && (
        <Stack direction="row" spacing={1} sx={{ mt: 2 }}>
          {departmentName && (
            <Chip 
              label={`부서: ${departmentName}`} 
              onDelete={() => onDepartmentNameChange('')}
              size="small"
            />
          )}
          {position && (
            <Chip 
              label={`직함: ${position}`} 
              onDelete={() => onPositionChange('')}
              size="small"
            />
          )}
          {status && (
            <Chip 
              label={`상태: ${getStatusLabel(status)}`} 
              onDelete={() => onStatusChange('')}
              size="small"
            />
          )}
        </Stack>
      )}
    </Box>
  );
};

export default EmployeeToolbar; 