import { Box, Paper } from '@mui/material';
import React, { ReactNode } from 'react';

interface PageContainerProps {
  children: ReactNode;
}

const PageContainer: React.FC<PageContainerProps> = ({ children }) => {
  return (
    <Paper
      elevation={1}
      sx={{
        padding: 3,
        borderRadius: 1,
        backgroundColor: 'background.paper',
      }}
    >
      <Box>{children}</Box>
    </Paper>
  );
};

export default PageContainer; 