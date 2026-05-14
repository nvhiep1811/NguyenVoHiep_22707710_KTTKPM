import React, { useCallback, useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { formatDateTime, getApiErrorMessage, notificationApi } from '../api'
import { useToast } from '../components/Toast'

export default function Notifications() {
  const [userId, setUserId] = useState(localStorage.getItem('userId') || '')
  const [notes, setNotes] = useState([])
  const [loading, setLoading] = useState(false)
  const toast = useToast()

  useEffect(() => {
    const syncUser = () => setUserId(localStorage.getItem('userId') || '')
    window.addEventListener('current-user-changed', syncUser)
    window.addEventListener('storage', syncUser)
    return () => {
      window.removeEventListener('current-user-changed', syncUser)
      window.removeEventListener('storage', syncUser)
    }
  }, [])

  const loadNotifications = useCallback(() => {
    if (!userId) return Promise.resolve()

    setLoading(true)
    return notificationApi
      .byUser(userId)
      .then(setNotes)
      .catch((err) => {
        setNotes([])
        toast.error(getApiErrorMessage(err, 'Cannot load notifications.'), 'Notifications unavailable')
      })
      .finally(() => setLoading(false))
  }, [toast, userId])

  useEffect(() => {
    loadNotifications()
  }, [loadNotifications])

  const markRead = async (id) => {
    try {
      await notificationApi.markRead(id)
      setNotes((items) => items.map((note) => note.id === id ? { ...note, read: true } : note))
      toast.success('Notification marked as read.')
    } catch (err) {
      toast.error(getApiErrorMessage(err, 'Cannot update notification.'), 'Update failed')
    }
  }

  const markAllRead = async () => {
    try {
      await notificationApi.markAllRead(userId)
      setNotes((items) => items.map((note) => ({ ...note, read: true })))
      toast.success('All notifications marked as read.')
    } catch (err) {
      toast.error(getApiErrorMessage(err, 'Cannot update notifications.'), 'Update failed')
    }
  }

  if (!userId) {
    return (
      <section className="empty-page">
        <div>
          <p className="eyebrow">Inbox</p>
          <h2>No active user</h2>
          <p className="muted">Login or register to view notifications.</p>
        </div>
        <div className="button-row compact">
          <Link className="btn btn-secondary" to="/login">Login</Link>
          <Link className="btn btn-primary" to="/register">Register</Link>
        </div>
      </section>
    )
  }

  const unreadCount = notes.filter((note) => !note.read).length

  return (
    <section className="page-stack">
      <div className="toolbar">
        <div>
          <p className="eyebrow">Inbox</p>
          <h2>Notifications</h2>
          <p className="muted">{unreadCount} unread for {userId}</p>
        </div>
        <div className="button-row compact">
          <button className="btn btn-secondary" type="button" onClick={loadNotifications} disabled={loading}>{loading ? 'Refreshing...' : 'Refresh'}</button>
          <button className="btn btn-primary" type="button" onClick={markAllRead} disabled={notes.length === 0 || unreadCount === 0}>Mark all read</button>
        </div>
      </div>

      <div className="list-surface">
        {loading && <div className="empty-inline">Loading notifications...</div>}
        {!loading && notes.length === 0 && <div className="empty-inline">No notifications yet.</div>}
        {notes.map((n) => (
          <article key={n.id} className={`notification-item ${n.read ? 'read' : 'unread'}`}>
            <div className="notification-main">
              <div className="notification-row">
                <strong>{n.title}</strong>
                <span className="tag">{n.read ? 'read' : 'unread'}</span>
              </div>
              <p>{n.message}</p>
              <small>{formatDateTime(n.createdAt)}</small>
            </div>
            {!n.read && <button className="btn btn-secondary btn-small" type="button" onClick={() => markRead(n.id)}>Mark read</button>}
          </article>
        ))}
      </div>
    </section>
  )
}
