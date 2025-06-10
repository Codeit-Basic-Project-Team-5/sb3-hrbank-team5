import { Typography } from '@mui/material';
import React, { ReactNode } from 'react';

interface PageTitleProps {
  children: ReactNode;
}

const PageTitle: React.FC<PageTitleProps> = ({ children }) => {
  return (
    <Typography 
      variant="h4" 
      component="h1" 
      sx={{ 
        fontWeight: 500, 
        marginBottom: 3,
        color: 'text.primary'
      }}
    >
      {children}
    </Typography>
  );
};

export default PageTitle; 