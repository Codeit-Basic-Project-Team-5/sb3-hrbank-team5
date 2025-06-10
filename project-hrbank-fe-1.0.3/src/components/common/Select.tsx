import { FormControl, MenuItem, Select as MuiSelect, SelectChangeEvent } from '@mui/material';
import React from 'react';

interface Option {
  value: string;
  label: string;
}

interface SelectProps {
  options: Option[];
  value: string;
  onChange: (event: SelectChangeEvent<string>) => void;
  size?: 'small' | 'medium';
}

const Select: React.FC<SelectProps> = ({ options, value, onChange, size = 'small' }) => {
  return (
    <FormControl size={size}>
      <MuiSelect
        value={value}
        onChange={onChange}
        displayEmpty
        sx={{ minWidth: 180 }}
      >
        {options.map((option) => (
          <MenuItem key={option.value} value={option.value}>
            {option.label}
          </MenuItem>
        ))}
      </MuiSelect>
    </FormControl>
  );
};

export default Select; 