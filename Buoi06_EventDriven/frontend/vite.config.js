import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig(({ mode }) => {
  const apiBase = process.env.VITE_API_BASE || 'http://localhost:8080'

  return {
    plugins: [react()],
    server: {
      // Proxy /api to backend gateway during development (port 5173 -> 8080)
      proxy: {
        '/api': {
          target: apiBase,
          changeOrigin: true,
          secure: false,
          rewrite: (path) => path.replace(/^\/api/, '/api')
        }
      }
    }
  }
})
