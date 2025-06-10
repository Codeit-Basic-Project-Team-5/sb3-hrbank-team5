import React, { createContext, useContext, useState, ReactNode } from 'react';
import { AlertColor } from '@mui/material';
import { ErrorSnackbar } from '../components/common';
import { ErrorResponse } from '../api/common/model.ts';

interface ErrorContextType {
  showError: (error: ErrorResponse) => void;
  showMessage: (message: string, severity?: AlertColor, details?: string) => void;
  clearError: () => void;
}

const ErrorContext = createContext<ErrorContextType | undefined>(undefined);

interface ErrorProviderProps {
  children: ReactNode;
}

export const ErrorProvider: React.FC<ErrorProviderProps> = ({ children }) => {
  const [open, setOpen] = useState(false);
  const [message, setMessage] = useState('');
  const [details, setDetails] = useState<string | undefined>(undefined);
  const [severity, setSeverity] = useState<AlertColor>('error');
  const [timestamp, setTimestamp] = useState<string | undefined>(undefined);
  const [status, setStatus] = useState<number | undefined>(undefined);

  const showError = (error: ErrorResponse) => {
    setMessage(error.message || '오류가 발생했습니다.');
    setDetails(error.details);
    setSeverity('error');
    setTimestamp(error.timestamp);
    setStatus(error.status);
    setOpen(true);
  };

  const showMessage = (message: string, severity: AlertColor = 'info', details?: string) => {
    setMessage(message);
    setDetails(details);
    setSeverity(severity);
    setTimestamp(undefined);
    setStatus(undefined);
    setOpen(true);
  };

  const clearError = () => {
    setOpen(false);
  };

  const handleClose = () => {
    setOpen(false);
  };

  return (
    <ErrorContext.Provider value={{ showError, showMessage, clearError }}>
      {children}
      <ErrorSnackbar
        open={open}
        onClose={handleClose}
        message={message}
        details={details}
        severity={severity}
        timestamp={timestamp}
        status={status}
      />
    </ErrorContext.Provider>
  );
};

export const useError = (): ErrorContextType => {
  const context = useContext(ErrorContext);
  if (context === undefined) {
    throw new Error('useError must be used within an ErrorProvider');
  }
  return context;
}; 