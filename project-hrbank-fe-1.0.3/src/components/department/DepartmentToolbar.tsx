import {
  Add as AddIcon,
  Search as SearchIcon
} from '@mui/icons-material';
import {
  Box,
  Button,
  FormControl,
  Grid,
  InputAdornment,
  MenuItem,
  Select,
  SelectChangeEvent,
  TextField
} from '@mui/material';
import React from 'react';

interface SortOption {
  value: string;
  label: string;
}

interface DepartmentToolbarProps {
  searchTerm: string;
  onSearchChange: (value: string) => void;
  onSearch: (e: React.KeyboardEvent<HTMLInputElement>) => void;
  sortValue: string;
  onSortChange: (e: SelectChangeEvent<string>) => void;
  sortOptions: SortOption[];
  onAddClick: () => void;
}

const DepartmentToolbar: React.FC<DepartmentToolbarProps> = ({
  searchTerm,
  onSearchChange,
  onSearch,
  sortValue,
  onSortChange,
  sortOptions,
  onAddClick
}) => {
  return (
    <Grid container spacing={2} alignItems="center" sx={{ mb: 3 }}>
      <Grid item xs>
        <Box sx={{ display: 'flex', gap: 2 }}>
          <TextField
            placeholder="부서명 또는 설명"
            size="small"
            value={searchTerm}
            onChange={(e) => onSearchChange(e.target.value)}
            onKeyDown={onSearch}
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
        </Box>
      </Grid>
      <Grid item>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={onAddClick}
        >
          부서 등록
        </Button>
      </Grid>
    </Grid>
  );
};

export default DepartmentToolbar; 