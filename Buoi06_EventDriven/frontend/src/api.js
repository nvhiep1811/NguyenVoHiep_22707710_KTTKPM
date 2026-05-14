import axios from 'axios'

const API_BASE = import.meta.env.VITE_API_BASE || '/api'

const api = axios.create({
  baseURL: API_BASE,
  headers: { 'Content-Type': 'application/json' }
})

export const getApiErrorMessage = (err, fallback) => {
  const data = err.response?.data

  if (typeof data === 'string') {
    return data || fallback
  }

  return data?.message || data?.detail || data?.error || fallback
}

export const moviesApi = {
  list: () => api.get('/movies').then(r => {
    const d = r.data
    if (Array.isArray(d)) return d
    if (d == null) return []
    if (Array.isArray(d.content)) return d.content
    // fallback: try to extract array from object
    const vals = Object.values(d).filter(v => Array.isArray(v))
    if (vals.length) return vals[0]
    return []
  }),
  get: (id) => api.get(`/movies/${id}`).then(r => r.data)
}

export const bookingApi = {
  create: (payload) => api.post('/bookings', payload).then(r => r.data),
  byUser: (userId) => api.get(`/bookings/users/${userId}`).then(r => r.data)
}

export const userApi = {
  register: (payload) => api.post('/users/register', payload).then(r => r.data),
  login: (payload) => api.post('/users/login', payload).then(r => r.data)
}

export const notificationApi = {
  byUser: (userId) => api.get(`/notifications/users/${userId}`).then(r => r.data),
  unread: (userId) => api.get(`/notifications/users/${userId}/unread`).then(r => r.data),
  markRead: (id) => api.patch(`/notifications/${id}/read`).then(r => r.data),
  markAllRead: (userId) => api.patch(`/notifications/users/${userId}/read-all`).then(r => r.data)
}

export const eventsApi = {
  list: () => api.get('/events').then(r => r.data),
  byType: (type) => api.get(`/events/types/${type}`).then(r => r.data),
  streamUrl: () => `${API_BASE}/events/stream`
}

export const formatDateTime = (value) => {
  if (!value) return 'Not scheduled'

  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return String(value)

  return new Intl.DateTimeFormat('vi-VN', {
    dateStyle: 'medium',
    timeStyle: 'short'
  }).format(date)
}

export const formatCurrency = (value) => {
  const amount = Number(value || 0)

  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD'
  }).format(amount)
}
