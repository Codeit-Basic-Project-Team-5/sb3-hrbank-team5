import {
  Table as MuiTable,
  Paper,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
} from '@mui/material';
import React from 'react';

interface Column<T> {
  header: string;
  accessor: keyof T | ((data: T) => React.ReactNode);
  width?: string;
}

interface TableProps<T> {
  columns: Column<T>[];
  data: T[];
  onRowClick?: (item: T) => void;
}

function Table<T>({ columns, data, onRowClick }: TableProps<T>) {
  return (
    <TableContainer component={Paper} elevation={0} sx={{ marginTop: 2 }}>
      <MuiTable>
        <TableHead>
          <TableRow>
            {columns.map((column, index) => (
              <TableCell
                key={index}
                sx={{
                  width: column.width,
                  fontWeight: 500,
                  backgroundColor: 'secondary.main',
                }}
              >
                {column.header}
              </TableCell>
            ))}
          </TableRow>
        </TableHead>
        <TableBody>
          {data.map((item, rowIndex) => (
            <TableRow
              key={rowIndex}
              onClick={() => onRowClick && onRowClick(item)}
              hover
              sx={{ cursor: onRowClick ? 'pointer' : 'default' }}
            >
              {columns.map((column, colIndex) => (
                <TableCell key={colIndex}>
                  {typeof column.accessor === 'function'
                    ? column.accessor(item)
                    : item[column.accessor] as React.ReactNode}
                </TableCell>
              ))}
            </TableRow>
          ))}
        </TableBody>
      </MuiTable>
    </TableContainer>
  );
}

export default Table; 