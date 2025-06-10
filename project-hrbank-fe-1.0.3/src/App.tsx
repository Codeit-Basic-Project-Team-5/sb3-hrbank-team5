import React, { useEffect } from 'react';
import {Routes, Route, Navigate, HashRouter} from 'react-router-dom';
import { ThemeProvider } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import theme from './theme.ts';
import Layout from './components/layout/Layout.tsx';
import DashboardPage from './pages/DashboardPage.tsx';
import EmployeePage from './pages/EmployeePage.tsx';
import DepartmentPage from './pages/DepartmentPage.tsx';
import ChangeLogPage from './pages/ChangeLogPage.tsx';
import BackupPage from './pages/BackupPage.tsx';
import { ErrorProvider, useError } from './contexts/ErrorContext.tsx';
import { setGlobalErrorHandler } from './api/client.ts';

// 에러 핸들러 설정을 위한 컴포넌트
const ErrorHandler: React.FC = () => {
  const { showError } = useError();
  
  useEffect(() => {
    // API 클라이언트에 전역 에러 핸들러 설정
    setGlobalErrorHandler(showError);
  }, [showError]);
  
  return null;
};

const App: React.FC = () => {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <ErrorProvider>
        <ErrorHandler />
        <HashRouter>
          <Routes>
            <Route path="/" element={<Navigate to="/dashboard" replace />} />
            <Route
              path="/dashboard"
              element={
                <Layout>
                  <DashboardPage />
                </Layout>
              }
            />
            <Route
              path="/employees"
              element={
                <Layout>
                  <EmployeePage />
                </Layout>
              }
            />
            <Route
              path="/departments"
              element={
                <Layout>
                  <DepartmentPage />
                </Layout>
              }
            />
            <Route
              path="/change-logs"
              element={
                <Layout>
                  <ChangeLogPage />
                </Layout>
              }
            />
            <Route
              path="/backups"
              element={
                <Layout>
                  <BackupPage />
                </Layout>
              }
            />
          </Routes>
        </HashRouter>
      </ErrorProvider>
    </ThemeProvider>
  );
};

export default App;
