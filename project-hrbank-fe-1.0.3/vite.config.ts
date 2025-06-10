import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // 백엔드 서버 주소
        changeOrigin: true,  // 백엔드 서버의 CORS 정책을 우회
        secure: false,       // HTTPS가 아닌 경우 필요
        // rewrite: (path) => path.replace(/^\/api/, '') // URL 재작성 가능
      }
    }
  }
})
