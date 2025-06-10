import { createTheme } from '@mui/material/styles';

const theme = createTheme({
  palette: {
    primary: {
      main: '#4285f4',
      light: '#80b1ff',
      dark: '#0057c1',
      contrastText: '#ffffff',
    },
    secondary: {
      main: '#f5f5f5',
      light: '#ffffff',
      dark: '#c2c2c2',
      contrastText: '#333333',
    },
    error: {
      main: '#ea4335',
    },
    background: {
      default: '#f5f5f5',
      paper: '#ffffff',
    },
    text: {
      primary: '#333333',
      secondary: '#666666',
    },
  },
  typography: {
    fontFamily: '"Noto Sans KR", "Roboto", "Helvetica", "Arial", sans-serif',
    h1: {
      fontSize: '2rem',
      fontWeight: 500,
      marginBottom: '1.5rem',
    },
    h2: {
      fontSize: '1.75rem',
      fontWeight: 500,
      marginBottom: '1.25rem',
    },
    h3: {
      fontSize: '1.5rem',
      fontWeight: 500,
      marginBottom: '1rem',
    },
    h4: {
      fontSize: '1.25rem',
      fontWeight: 500,
      marginBottom: '0.75rem',
    },
    h5: {
      fontSize: '1.1rem',
      fontWeight: 500,
      marginBottom: '0.5rem',
    },
    h6: {
      fontSize: '1rem',
      fontWeight: 500,
      marginBottom: '0.5rem',
    },
    body1: {
      fontSize: '1rem',
    },
    body2: {
      fontSize: '0.875rem',
    },
  },
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          textTransform: 'none',
          borderRadius: '4px',
        },
      },
    },
    MuiTableCell: {
      styleOverrides: {
        root: {
          padding: '12px 16px',
        },
        head: {
          fontWeight: 500,
          backgroundColor: '#f5f5f5',
        },
      },
    },
  },
});

export default theme; 