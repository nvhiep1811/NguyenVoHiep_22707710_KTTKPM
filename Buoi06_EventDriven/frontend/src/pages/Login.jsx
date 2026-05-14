import React, { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { getApiErrorMessage, userApi } from '../api'
import { useToast } from '../components/Toast'

export default function Login() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [saved, setSaved] = useState(localStorage.getItem('userId') || '')
  const [status, setStatus] = useState({ type: 'idle', message: '' })
  const toast = useToast()
  const navigate = useNavigate()

  const handle = async (e) => {
    e.preventDefault()
    if (!username.trim()) return setStatus({ type: 'error', message: 'Enter username or email.' })
    if (!password) return setStatus({ type: 'error', message: 'Enter password.' })

    setStatus({ type: 'loading', message: 'Signing in...' })
    try {
      const user = await userApi.login({ username: username.trim(), password })

      if (user?.id) {
        localStorage.setItem('userId', user.id)
        window.dispatchEvent(new Event('current-user-changed'))
        setSaved(user.id)
        setStatus({ type: 'success', message: `Logged in as ${user.username}.` })
        toast.success(`Logged in as ${user.username}.`, 'Welcome back')
        navigate('/movies')
      } else {
        setStatus({ type: 'error', message: 'Login did not return a user id.' })
      }
    } catch (err) {
      console.error(err)
      const message = getApiErrorMessage(err, 'Login failed. Check your username/email and password.')
      setStatus({ type: 'error', message })
      toast.error(message, 'Login failed')
    }
  }

  return (
    <section className="auth-page">
      <div className="auth-copy">
        <p className="eyebrow">Login</p>
        <h2>Use your active demo account</h2>
        <p className="lead">The returned user id is kept locally for bookings and notifications.</p>
        <div className="inline-note">
          <strong>Stored user</strong>
          <p>{saved || 'No user selected yet.'}</p>
        </div>
      </div>

      <form className="form-card" onSubmit={handle}>
        <label className="field">
          <span>Username or email</span>
          <input className="field-input" value={username} onChange={e => setUsername(e.target.value)} placeholder="jane.doe or jane@example.com" autoComplete="username" />
        </label>
        <label className="field">
          <span>Password</span>
          <input className="field-input" type="password" value={password} onChange={e => setPassword(e.target.value)} placeholder="Your password" autoComplete="current-password" />
        </label>
        {status.type !== 'idle' && (
          <div className={`status-banner ${status.type}`}>
            {status.message}
          </div>
        )}
        <button className="btn btn-primary btn-full" type="submit" disabled={status.type === 'loading'}>
          {status.type === 'loading' ? 'Signing in...' : 'Login'}
        </button>
        <p className="form-footnote">No account yet? <Link to="/register">Create one</Link></p>
      </form>
    </section>
  )
}
