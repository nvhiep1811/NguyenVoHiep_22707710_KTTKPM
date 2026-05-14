import React, { useEffect, useState } from 'react'
import { Link, NavLink, Outlet, useLocation, useNavigate } from 'react-router-dom'
import { useToast } from './components/Toast'

const publicRoutes = [
  { to: '/movies', label: 'Movies' },
  { to: '/register', label: 'Register' },
  { to: '/login', label: 'Login' },
  { to: '/events', label: 'Events' }
]

const signedInRoutes = [
  { to: '/movies', label: 'Movies' },
  { to: '/notifications', label: 'Notifications' },
  { to: '/events', label: 'Events' }
]

export default function App() {
  const [userId, setUserId] = useState(localStorage.getItem('userId') || '')
  const navigate = useNavigate()
  const location = useLocation()
  const toast = useToast()

  useEffect(() => {
    const syncUser = () => setUserId(localStorage.getItem('userId') || '')
    window.addEventListener('storage', syncUser)
    window.addEventListener('current-user-changed', syncUser)
    return () => {
      window.removeEventListener('storage', syncUser)
      window.removeEventListener('current-user-changed', syncUser)
    }
  }, [])

  const routes = userId ? signedInRoutes : publicRoutes

  const handleLogout = () => {
    localStorage.removeItem('userId')
    window.dispatchEvent(new Event('current-user-changed'))
    setUserId('')
    toast.info('Current user has been cleared.', 'Logged out')

    if (location.pathname.startsWith('/notifications')) {
      navigate('/login')
    }
  }

  return (
    <div className="app-shell">
      <header className="topbar">
        <Link className="brand-block" to="/movies" aria-label="Movie Tickets home">
          <div className="brand-mark">MT</div>
          <div>
            <p className="eyebrow">Event-driven cinema</p>
            <h1 className="brand-title">Movie Tickets</h1>
          </div>
        </Link>
        <nav className="topnav">
          {routes.map((route) => (
            <NavLink key={route.to} to={route.to} className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}>
              {route.label}
            </NavLink>
          ))}
          {userId && (
            <button className="nav-link nav-button" type="button" onClick={handleLogout}>
              Logout
            </button>
          )}
        </nav>
        <Link className={`user-chip ${userId ? 'active' : ''}`} to={userId ? '/notifications' : '/login'}>
          <span className="dot" />
          <div>
            <div className="user-chip-label">Current user</div>
            <strong>{userId || 'Not signed in'}</strong>
          </div>
        </Link>
      </header>

      <main className="page-frame">
        <Outlet />
      </main>
    </div>
  )
}
