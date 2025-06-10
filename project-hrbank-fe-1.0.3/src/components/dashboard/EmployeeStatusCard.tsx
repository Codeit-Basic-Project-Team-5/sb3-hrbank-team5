import {
  ExitToApp as ExitIcon,
  PauseCircle as PauseIcon,
  People as PeopleIcon,
  Work as WorkIcon
} from '@mui/icons-material';
import {
  Box,
  CircularProgress,
  Grid,
  Paper,
  Typography,
  useTheme
} from '@mui/material';
import React from 'react';
import { EmployeeStatus } from '../../api/employee/model.ts';

interface StatusCount {
  status: EmployeeStatus | 'TOTAL';
  count: number;
}

interface EmployeeStatusCardProps {
  data: StatusCount[];
  loading: boolean;
}

const EmployeeStatusCard: React.FC<EmployeeStatusCardProps> = ({
  data,
  loading
}) => {
  const theme = useTheme();

  // 상태별 아이콘 및 색상 정의
  const getStatusInfo = (status: EmployeeStatus | 'TOTAL') => {
    switch (status) {
      case 'ACTIVE':
        return { 
          icon: <WorkIcon />, 
          color: theme.palette.success.main,
          label: '재직중'
        };
      case 'ON_LEAVE':
        return { 
          icon: <PauseIcon />, 
          color: theme.palette.warning.main,
          label: '휴직중'
        };
      case 'RESIGNED':
        return { 
          icon: <ExitIcon />, 
          color: theme.palette.error.main,
          label: '퇴사'
        };
      case 'TOTAL':
      default:
        return { 
          icon: <PeopleIcon />, 
          color: theme.palette.primary.main,
          label: '전체'
        };
    }
  };

  return (
    <Paper sx={{ p: 3, height: '100%' }}>
      <Typography variant="h6" sx={{ mb: 2 }}>직원 현황</Typography>
      
      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: 150 }}>
          <CircularProgress />
        </Box>
      ) : data.length === 0 ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: 150 }}>
          <Typography color="text.secondary">데이터가 없습니다.</Typography>
        </Box>
      ) : (
        <Grid container spacing={2}>
          {data.map((item) => {
            const { icon, color, label } = getStatusInfo(item.status);
            return (
              <Grid item xs={6} key={item.status}>
                <Paper 
                  variant="outlined" 
                  sx={{ 
                    p: 2, 
                    display: 'flex', 
                    flexDirection: 'column',
                    alignItems: 'center',
                    borderColor: color,
                    borderWidth: item.status === 'TOTAL' ? 2 : 1
                  }}
                >
                  <Box sx={{ 
                    display: 'flex', 
                    alignItems: 'center', 
                    color,
                    mb: 1
                  }}>
                    {icon}
                    <Typography 
                      variant="subtitle2" 
                      sx={{ ml: 0.5 }}
                    >
                      {label}
                    </Typography>
                  </Box>
                  <Typography 
                    variant="h4" 
                    sx={{ 
                      fontWeight: 'bold',
                      color: item.status === 'TOTAL' ? color : 'text.primary'
                    }}
                  >
                    {item.count}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    명
                  </Typography>
                </Paper>
              </Grid>
            );
          })}
        </Grid>
      )}
    </Paper>
  );
};

export default EmployeeStatusCard; 