import AccountBalanceIcon from '@mui/icons-material/AccountBalance';
import BusinessIcon from '@mui/icons-material/Business';
import DashboardIcon from '@mui/icons-material/Dashboard';
import HistoryIcon from '@mui/icons-material/History';
import PeopleIcon from '@mui/icons-material/People';
import StorageIcon from '@mui/icons-material/Storage';
import {
  Box,
  Drawer,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Typography,
  useTheme
} from '@mui/material';
import React from 'react';
import { NavLink, useLocation } from 'react-router-dom';

const drawerWidth = 240;

const menuItems = [
  { path: '/dashboard', label: '대시보드', icon: <DashboardIcon /> },
  { path: '/departments', label: '부서 관리', icon: <BusinessIcon /> },
  { path: '/employees', label: '직원 관리', icon: <PeopleIcon /> },
  { path: '/change-logs', label: '수정 이력', icon: <HistoryIcon /> },
  { path: '/backups', label: '데이터 백업', icon: <StorageIcon /> },
];

// HR Bank 로고 컴포넌트
const HRBankLogo: React.FC = () => {
  const theme = useTheme();
  
  return (
    <Box
      sx={{
        display: 'flex',
        alignItems: 'center',
        padding: '16px 20px',
        backgroundColor: 'background.paper',
        color: theme.palette.primary.main,
        height: 64,
        borderBottom: `1px solid ${theme.palette.divider}`,
      }}
    >
      <Box
        sx={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          backgroundColor: theme.palette.primary.main,
          borderRadius: '8px',
          padding: '8px',
          marginRight: 1.5,
          boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
          color: 'white',
        }}
      >
        <AccountBalanceIcon sx={{ fontSize: 22 }} />
      </Box>
      <Box>
        <Typography
          variant="h6"
          component="div"
          sx={{
            fontWeight: 700,
            letterSpacing: 0.5,
            fontSize: '1.2rem',
            lineHeight: 1,

          }}
        >
          HR Bank
        </Typography>
        <Typography
          variant="caption"
          sx={{
            opacity: 0.7,
            display: 'block',
            marginTop: 0,
            letterSpacing: 0.5,
            fontSize: '0.7rem',
            lineHeight: 1,
          }}
        >
          인사관리 시스템
        </Typography>
      </Box>
    </Box>
  );
};

const Sidebar: React.FC = () => {
  const location = useLocation();
  const theme = useTheme();

  return (
    <Drawer
      variant="permanent"
      sx={{
        width: drawerWidth,
        flexShrink: 0,
        '& .MuiDrawer-paper': {
          width: drawerWidth,
          boxSizing: 'border-box',
          boxShadow: '0 0 10px rgba(0,0,0,0.05)',
          border: 'none',
        },
      }}
    >
      <HRBankLogo />
      <List sx={{ py: 1.5, px: 1 }}>
        {menuItems.map((item) => (
          <ListItem key={item.path} disablePadding sx={{ mb: 0.5 }}>
            <ListItemButton
              component={NavLink}
              to={item.path}
              selected={location.pathname === item.path}
              sx={{
                borderRadius: 1,
                px: 1.5,
                '&.active': {
                  backgroundColor: theme.palette.primary.light + '20',
                  color: theme.palette.primary.main,
                  '& .MuiListItemIcon-root': {
                    color: theme.palette.primary.main,
                  },
                },
                '&:hover': {
                  backgroundColor: theme.palette.action.hover,
                },
              }}
            >
              <ListItemIcon sx={{ minWidth: 40 }}>{item.icon}</ListItemIcon>
              <ListItemText 
                primary={item.label} 
                primaryTypographyProps={{ 
                  fontSize: '0.9rem',
                  fontWeight: location.pathname === item.path ? 600 : 400
                }}
              />
            </ListItemButton>
          </ListItem>
        ))}
      </List>
    </Drawer>
  );
};

export default Sidebar; 