import { createGlobalStyle } from 'styled-components';

const GlobalStyle = createGlobalStyle`
  :root {
    font-family: system-ui, Avenir, Helvetica, Arial, sans-serif;
    line-height: 1.5;
    font-weight: 400;

    color-scheme: light dark;
    color: ${({ theme }) => theme.colors.text};
    background-color: ${({ theme }) => theme.colors.background};

    font-synthesis: none;
    text-rendering: optimizeLegibility;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
  }

  * {
    box-sizing: border-box;
    margin: 0;
    padding: 0;
  }

  a {
    font-weight: 500;
    color: ${({ theme }) => theme.colors.primary};
    text-decoration: inherit;
  }
  
  a:hover {
    color: ${({ theme }) => theme.colors.secondary};
  }

  body {
    margin: 0;
    min-width: 320px;
    min-height: 100vh;
  }

  h1 {
    font-size: ${({ theme }) => theme.fontSizes.xxl};
    line-height: 1.1;
  }

  button {
    border-radius: ${({ theme }) => theme.borderRadius.md};
    border: 1px solid transparent;
    padding: 0.6em 1.2em;
    font-size: ${({ theme }) => theme.fontSizes.md};
    font-weight: 500;
    font-family: inherit;
    background-color: #1a1a1a;
    cursor: pointer;
    transition: border-color 0.25s;
  }
  
  button:hover {
    border-color: ${({ theme }) => theme.colors.primary};
  }
  
  button:focus,
  button:focus-visible {
    outline: 4px auto -webkit-focus-ring-color;
  }

  @media (prefers-color-scheme: light) {
    :root {
      color: ${({ theme }) => theme.colors.text};
      background-color: ${({ theme }) => theme.colors.background};
    }
    
    a:hover {
      color: ${({ theme }) => theme.colors.secondary};
    }
    
    button {
      background-color: #f9f9f9;
    }
  }
`;

export default GlobalStyle; 