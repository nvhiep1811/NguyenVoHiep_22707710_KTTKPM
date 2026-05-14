import React from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter, Navigate, Routes, Route } from 'react-router-dom'
import App from './App'
import './styles.css'
import Home from './pages/Home'
import Movie from './pages/Movie'
import Register from './pages/Register'
import Login from './pages/Login'
import Notifications from './pages/Notifications'
import Events from './pages/Events'
import NotFound from './pages/NotFound'
import { ToastProvider } from './components/Toast'

createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <ToastProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<App />}>
            <Route index element={<Navigate to="/movies" replace />} />
            <Route path="movies" element={<Home />} />
            <Route path="movies/:id" element={<Movie />} />
            <Route path="register" element={<Register />} />
            <Route path="login" element={<Login />} />
            <Route path="notifications" element={<Notifications />} />
            <Route path="events" element={<Events />} />
            <Route path="*" element={<NotFound />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </ToastProvider>
  </React.StrictMode>
)
