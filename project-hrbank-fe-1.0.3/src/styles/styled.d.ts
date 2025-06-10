import 'styled-components';
import { Theme } from './theme.ts';

// styled-components의 DefaultTheme 인터페이스 확장
declare module 'styled-components' {
  export interface DefaultTheme extends Theme {}
} 