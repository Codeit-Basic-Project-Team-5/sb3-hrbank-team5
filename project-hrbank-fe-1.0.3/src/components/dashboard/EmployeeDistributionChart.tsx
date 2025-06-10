import {
  Box,
  CircularProgress,
  LinearProgress,
  Typography,
  useTheme
} from '@mui/material';
import React from 'react';
import { EmployeeDistributionDto } from '../../api/employee/model.ts';

interface EmployeeDistributionChartProps {
  data: EmployeeDistributionDto[];
  loading: boolean;
  title: string;
  groupBy: 'department' | 'position';
}

const EmployeeDistributionChart: React.FC<EmployeeDistributionChartProps> = ({
  data,
  loading,
}) => {
  const theme = useTheme();
  
  // 테마 색상 배열
  const COLORS = [
    theme.palette.primary.main,
    theme.palette.success.main,
    theme.palette.info.main,
    theme.palette.warning.main,
    theme.palette.error.main,
    '#5c6bc0', // indigo
    '#26a69a', // teal
    '#ec407a', // pink
    '#7e57c2', // deep purple
  ];

  // 데이터 정렬 (내림차순)
  const sortedData = [...data].sort((a, b) => b.count - a.count);

  // 진행 막대 항목 렌더링
  const renderProgressItem = (item: EmployeeDistributionDto, index: number) => {
    const color = COLORS[index % COLORS.length];
    const backgroundColor = theme.palette.grey[100];
    
    return (
      <Box key={item.groupKey} sx={{ mb: 2 }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 0.5 }}>
          <Typography 
            variant="body2" 
            color="text.primary" 
            noWrap 
            sx={{ 
              maxWidth: '60%', 
              fontWeight: 500,
              color: theme.palette.mode === 'light' ? 'rgba(0, 0, 0, 0.7)' : 'rgba(255, 255, 255, 0.7)'
            }} 
            title={item.groupKey}
          >
            {item.groupKey}
          </Typography>
          <Typography 
            variant="body2" 
            sx={{ 
              color: color,
              fontWeight: 500
            }}
          >
            {item.count}명 ({item.percentage.toFixed(1)}%)
          </Typography>
        </Box>
        <LinearProgress
          variant="determinate"
          value={item.percentage}
          sx={{
            height: 12,
            borderRadius: 6,
            backgroundColor: backgroundColor,
            border: '1px solid rgba(0,0,0,0.05)',
            boxShadow: 'inset 0 1px 2px rgba(0,0,0,0.05)',
            '& .MuiLinearProgress-bar': {
              borderRadius: 6,
              backgroundColor: color,
              backgroundImage: `linear-gradient(90deg, ${color}80 0%, ${color} 100%)`,
              boxShadow: `0 0 6px ${color}80`,
            }
          }}
        />
      </Box>
    );
  };

  return (
    <Box 
      sx={{ 
        width: '100%', 
        height: 380, 
        overflow: 'auto',
        '&::-webkit-scrollbar': {
          width: '8px',
        },
        '&::-webkit-scrollbar-track': {
          backgroundColor: theme.palette.grey[100],
          borderRadius: '4px',
        },
        '&::-webkit-scrollbar-thumb': {
          backgroundColor: theme.palette.grey[400],
          borderRadius: '4px',
          '&:hover': {
            backgroundColor: theme.palette.grey[500],
          },
        },
        p: 1,
        backgroundColor: theme.palette.grey[50],
        borderRadius: 1,
      }}
    >
      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100%' }}>
          <CircularProgress />
        </Box>
      ) : data.length === 0 ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100%' }}>
          <Typography variant="body1" color="text.secondary">
            데이터가 없습니다
          </Typography>
        </Box>
      ) : (
        <Box sx={{ pt: 1, pr: 1 }}>
          {sortedData.map((item, index) => renderProgressItem(item, index))}
        </Box>
      )}
    </Box>
  );
};

export default EmployeeDistributionChart; 