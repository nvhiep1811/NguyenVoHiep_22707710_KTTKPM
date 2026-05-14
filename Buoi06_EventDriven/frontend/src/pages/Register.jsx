import React, { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { getApiErrorMessage, userApi } from '../api'
import { useToast } from '../components/Toast'

export default function Register() {
  const [form, setForm] = useState({ username: '', email: '', password: '' })
  const [status, setStatus] = useState({ type: 'idle', message: '' })
  const toast = useToast()
  const navigate = useNavigate()

  const updateField = (field, value) => {
    setForm((current) => ({ ...current, [field]: value }))
  }

  const handle = async e => {
    e.preventDefault()
    const payload = {
      ...form,
      username: form.username.trim(),
      email: form.email.trim()
    }

    if (!payload.username || !payload.email || !payload.password) {
      setStatus({ type: 'error', message: 'Fill in username, email, and password.' })
      return
    }

    setStatus({ type: 'loading', message: 'Creating account...' })
    try {
      const user = await userApi.register(payload)
      localStorage.setItem('userId', user.id)
      window.dispatchEvent(new Event('current-user-changed'))
      setStatus({ type: 'success', message: `Registered ${user.username}.` })
      setForm({ username: '', email: '', password: '' })
      toast.success(`Registered ${user.username}.`, 'Account ready')
      navigate('/movies')
    } catch (err) {
      console.error(err)
      const message = getApiErrorMessage(err, 'Register failed. Check the backend logs and API base.')
      setStatus({ type: 'error', message })
      toast.error(message, 'Register failed')
    }
  }

  return (
    <section className="auth-page">
      <div className="auth-copy">
        <p className="eyebrow">Register</p>
        <h2>Create a demo user</h2>
        <p className="lead">Registration publishes a user event and stores the new user id for the booking flow.</p>
        <div className="inline-note">
          <strong>Next step</strong>
          <p>Choose a movie after the account is created.</p>
        </div>
      </div>

      <form className="form-card" onSubmit={handle}>
        <label className="field">
          <span>Username</span>
          <input className="field-input" value={form.username} onChange={e => updateField('username', e.target.value)} placeholder="jane.doe" autoComplete="username" />
        </label>
        <label className="field">
          <span>Email</span>
          <input className="field-input" type="email" value={form.email} onChange={e => updateField('email', e.target.value)} placeholder="jane@example.com" autoComplete="email" />
        </label>
        <label className="field">
          <span>Password</span>
          <input className="field-input" type="password" value={form.password} onChange={e => updateField('password', e.target.value)} placeholder="At least 6 characters" autoComplete="new-password" />
        </label>

        {status.type !== 'idle' && (
          <div className={`status-banner ${status.type}`}>
            {status.message}
          </div>
        )}

        <button className="btn btn-primary btn-full" type="submit" disabled={status.type === 'loading'}>
          {status.type === 'loading' ? 'Registering...' : 'Create account'}
        </button>
        <p className="form-footnote">Already registered? <Link to="/login">Login</Link></p>
      </form>
    </section>
  )
}
