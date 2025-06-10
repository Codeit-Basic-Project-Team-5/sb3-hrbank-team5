import {
  Box,
  Chip,
  CircularProgress,
  Paper,
  Typography,
  useTheme
} from '@mui/material';
import React from 'react';
import {
  Area,
  CartesianGrid,
  Legend,
  Line,
  LineChart,
  ResponsiveContainer,
  Tooltip,
  TooltipProps,
  XAxis,
  YAxis
} from 'recharts';
import { EmployeeTrendDto } from '../../api/employee/model.ts';

interface EmployeeTrendChartProps {
  data: EmployeeTrendDto[];
  loading: boolean;
  title: string;
  timeUnit: string;
}

const EmployeeTrendChart: React.FC<EmployeeTrendChartProps> = ({
  data,
  loading,
  title,
  timeUnit
}) => {
  const theme = useTheme();

  // 날짜 형식 변환 함수
  const formatDate = (dateStr: string) => {
    const date = new Date(dateStr);
    
    switch (timeUnit) {
      case 'day':
        return `${date.getMonth() + 1}/${date.getDate()}`;
      case 'week':
        return `${date.getMonth() + 1}/${date.getDate()}`;
      case 'month':
        return `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, '0')}`;
      case 'quarter':
        const quarter = Math.floor(date.getMonth() / 3) + 1;
        return `${date.getFullYear()} Q${quarter}`;
      case 'year':
        return `${date.getFullYear()}`;
      default:
        return dateStr;
    }
  };

  // 툴크 커스터마이징
  const CustomTooltip = ({ active, payload, }: TooltipProps<number, string>) => {
    if (active && payload && payload.length) {
      const data = payload[0].payload as EmployeeTrendDto;
      return (
        <Paper sx={{ 
          p: 1.5, 
          boxShadow: '0 4px 8px rgba(0,0,0,0.15)',
          border: '1px solid rgba(0,0,0,0.05)',
          borderRadius: 1
        }}>
          <Typography variant="subtitle2" sx={{ mb: 1, fontWeight: 600, color: theme.palette.primary.dark }}>
            {formatDate(data.date)}
          </Typography>
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 0.5 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: 2 }}>
              <Typography variant="body2" color="text.secondary">직원 수:</Typography>
              <Typography variant="body2" fontWeight={600} color={theme.palette.primary.main}>{data.count}명</Typography>
            </Box>
            <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: 2 }}>
              <Typography variant="body2" color="text.secondary">증감:</Typography>
              <Box sx={{ display: 'flex', alignItems: 'center' }}>
                <Typography 
                  variant="body2" 
                  fontWeight={600}
                  color={data.change > 0 ? theme.palette.success.main : data.change < 0 ? theme.palette.error.main : 'text.primary'}
                >
                  {data.change > 0 ? '+' : ''}{data.change}명
                </Typography>
              </Box>
            </Box>
            <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: 2 }}>
              <Typography variant="body2" color="text.secondary">증감률:</Typography>
              <Box sx={{ display: 'flex', alignItems: 'center' }}>
                <Typography 
                  variant="body2" 
                  fontWeight={600}
                  color={data.changeRate > 0 ? theme.palette.success.main : data.changeRate < 0 ? theme.palette.error.main : 'text.primary'}
                >
                  {data.changeRate > 0 ? '+' : ''}{data.changeRate.toFixed(1)}%
                </Typography>
              </Box>
            </Box>
          </Box>
        </Paper>
      );
    }
    return null;
  };

  return (
    <Paper sx={{ p: 3, height: '100%' }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
        <Typography variant="h6">{title}</Typography>
        <Chip 
          label={`단위: ${timeUnit === 'day' ? '일' : 
                          timeUnit === 'week' ? '주' : 
                          timeUnit === 'month' ? '월' : 
                          timeUnit === 'quarter' ? '분기' : '년'}`} 
          size="small" 
          variant="outlined" 
        />
      </Box>
      
      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: 300 }}>
          <CircularProgress />
        </Box>
      ) : data.length === 0 ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: 300 }}>
          <Typography color="text.secondary">데이터가 없습니다.</Typography>
        </Box>
      ) : (
        <ResponsiveContainer width="100%" height={300}>
          <LineChart
            data={data}
            margin={{
              top: 5,
              right: 30,
              left: 20,
              bottom: 5,
            }}
          >
            <CartesianGrid strokeDasharray="3 3" stroke="rgba(0,0,0,0.08)" />
            <XAxis 
              dataKey="date" 
              tickFormatter={formatDate} 
              stroke="#666"
              tick={{ fill: '#666', fontSize: 12 }}
            />
            <YAxis 
              stroke="#666"
              tick={{ fill: '#666', fontSize: 12 }}
              tickFormatter={(value) => `${value}명`}
            />
            <Tooltip content={<CustomTooltip />} />
            <Legend />
            <defs>
              <linearGradient id="colorCount" x1="0" y1="0" x2="0" y2="1">
                <stop offset="5%" stopColor={theme.palette.primary.main} stopOpacity={0.2}/>
                <stop offset="95%" stopColor={theme.palette.primary.main} stopOpacity={0}/>
              </linearGradient>
            </defs>
            <Area 
              type="monotone" 
              dataKey="count" 
              fill="url(#colorCount)" 
              fillOpacity={1}
              stroke="none"
            />
            <Line
              type="monotone"
              dataKey="count"
              name="직원 수"
              stroke={theme.palette.primary.main}
              strokeWidth={3}
              dot={{ 
                fill: theme.palette.primary.main, 
                r: 4,
                strokeWidth: 2,
                stroke: theme.palette.grey[100]
              }}
              activeDot={{ 
                r: 6, 
                stroke: theme.palette.primary.main,
                strokeWidth: 2,
                fill: theme.palette.grey[100]
              }}
            />
          </LineChart>
        </ResponsiveContainer>
      )}
    </Paper>
  );
};

export default EmployeeTrendChart; 