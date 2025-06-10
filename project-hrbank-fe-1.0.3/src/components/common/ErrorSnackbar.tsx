import CloseIcon from '@mui/icons-material/Close';
import { Alert, AlertColor, Box, IconButton, Snackbar, Typography } from '@mui/material';
import { format } from 'date-fns';
import { ko } from 'date-fns/locale';
import React from 'react';

interface ErrorSnackbarProps {
  open: boolean;
  onClose: () => void;
  message: string;
  details?: string;
  severity: AlertColor;
  timestamp?: string;
  status?: number;
}

const ErrorSnackbar: React.FC<ErrorSnackbarProps> = ({
  open,
  onClose,
  message,
  details,
  severity,
  timestamp,
  status
}) => {
  // 타임스탬프 포맷팅
  const formatTimestamp = (timestamp?: string) => {
    if (!timestamp) return '';
    try {
      const date = new Date(timestamp);
      return format(date, 'yyyy-MM-dd HH:mm:ss', { locale: ko });
    } catch (error) {
      return timestamp;
    }
  };

  return (
    <Snackbar
      open={open}
      autoHideDuration={6000}
      onClose={onClose}
      anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
    >
      <Alert 
        severity={severity}
        variant="filled"
        sx={{ 
          width: '100%',
          minWidth: '300px',
          boxShadow: 3
        }}
        action={
          <IconButton
            size="small"
            aria-label="close"
            color="inherit"
            onClick={onClose}
          >
            <CloseIcon fontSize="small" />
          </IconButton>
        }
      >
        <Box sx={{ mb: details ? 1 : 0 }}>
          <Typography variant="body1" fontWeight="bold">
            {message}
          </Typography>
          {status && (
            <Typography variant="caption" display="block" sx={{ mt: 0.5 }}>
              상태 코드: {status}
            </Typography>
          )}
          {timestamp && (
            <Typography variant="caption" display="block">
              발생 시간: {formatTimestamp(timestamp)}
            </Typography>
          )}
        </Box>
        {details && (
          <Typography variant="body2" sx={{ mt: 1, fontSize: '0.875rem' }}>
            {details}
          </Typography>
        )}
      </Alert>
    </Snackbar>
  );
};

export default ErrorSnackbar; 