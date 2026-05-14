import React, { useEffect, useMemo, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { bookingApi, formatCurrency, formatDateTime, getApiErrorMessage, moviesApi } from '../api'
import { useToast } from '../components/Toast'

const initials = (title = 'MT') => title.split(' ').slice(0, 2).map((word) => word[0]).join('').toUpperCase()

export default function Movie() {
  const { id } = useParams()
  const [movie, setMovie] = useState(null)
  const [seats, setSeats] = useState(1)
  const [selectedShow, setSelectedShow] = useState(null)
  const [userId, setUserId] = useState(localStorage.getItem('userId') || '')
  const [status, setStatus] = useState({ type: 'idle', message: '' })
  const [loading, setLoading] = useState(true)
  const toast = useToast()

  useEffect(() => {
    setLoading(true)
    setStatus({ type: 'idle', message: '' })
    moviesApi
      .get(id)
      .then((data) => {
        setMovie(data)
        const shows = Array.isArray(data?.showTimes) ? data.showTimes : []
        setSelectedShow(shows.find((show) => Number(show.availableSeats) > 0) || shows[0] || null)
      })
      .catch((err) => {
        setMovie(null)
        toast.error(getApiErrorMessage(err, 'Movie detail is unavailable.'), 'Movie unavailable')
      })
      .finally(() => setLoading(false))
  }, [id, toast])

  useEffect(() => {
    const syncUser = () => setUserId(localStorage.getItem('userId') || '')
    window.addEventListener('current-user-changed', syncUser)
    window.addEventListener('storage', syncUser)
    return () => {
      window.removeEventListener('current-user-changed', syncUser)
      window.removeEventListener('storage', syncUser)
    }
  }, [])

  useEffect(() => {
    const availableSeats = Number(selectedShow?.availableSeats || 0)
    if (availableSeats > 0 && seats > availableSeats) {
      setSeats(availableSeats)
    }
  }, [seats, selectedShow])

  const showTimes = Array.isArray(movie?.showTimes) ? movie.showTimes : []
  const availableSeats = Number(selectedShow?.availableSeats || 0)
  const safeSeats = Math.min(Math.max(Number(seats) || 1, 1), Math.max(availableSeats, 1))
  const total = useMemo(() => Number(((Number(selectedShow?.price) || 0) * safeSeats).toFixed(2)), [safeSeats, selectedShow])
  const canBook = Boolean(userId && selectedShow && availableSeats > 0 && status.type !== 'loading')

  if (loading) return <div className="empty-page"><h2>Loading movie...</h2></div>
  if (!movie) {
    return (
      <section className="empty-page">
        <div>
          <p className="eyebrow">Movie unavailable</p>
          <h2>Cannot load this movie</h2>
          <p className="muted">The movie id may be invalid or movie-service is offline.</p>
        </div>
        <Link className="btn btn-primary" to="/movies">Back to movies</Link>
      </section>
    )
  }

  const handleBook = async () => {
    if (!userId) {
      toast.error('Sign in or register before creating a booking.', 'User required')
      return
    }
    if (!selectedShow) {
      toast.error('Choose a showtime first.', 'Showtime required')
      return
    }
    if (availableSeats <= 0) {
      toast.error('This showtime is sold out.', 'No seats available')
      return
    }

    setStatus({ type: 'loading', message: 'Creating booking...' })
    const payload = {
      userId,
      movieId: movie.id,
      showTimeId: selectedShow.id,
      movieTitle: movie.title,
      seats: safeSeats,
      totalPrice: total
    }

    try {
      const booking = await bookingApi.create(payload)
      localStorage.setItem('userId', booking.userId)
      window.dispatchEvent(new Event('current-user-changed'))
      setStatus({ type: 'success', message: `Booking ${booking.id} created.` })
      toast.success('Payment will be processed asynchronously.', `Booking ${booking.id}`)
    } catch (e) {
      console.error(e)
      const message = getApiErrorMessage(e, 'Failed to create booking.')
      setStatus({ type: 'error', message })
      toast.error(message, 'Booking failed')
    }
  }

  return (
    <section className="page-stack">
      <div className="movie-detail">
        <div>
          <p className="eyebrow">Movie details</p>
          <h2>{movie.title}</h2>
          <p className="lead">{movie.description}</p>
          <div className="movie-topline">
            <span className="tag">{movie.genre}</span>
            <span className="muted">{movie.duration} min</span>
          </div>
        </div>
        <div className="poster-frame">
          {movie.posterUrl && <img className="poster" src={movie.posterUrl} alt={movie.title} onError={(event) => { event.currentTarget.style.display = 'none' }} />}
          <div className="poster-fallback">{initials(movie.title)}</div>
        </div>
      </div>

      <div className="booking-layout">
        <div>
          <div className="toolbar flat">
            <div>
              <p className="eyebrow">Showtimes</p>
              <h3>Select a session</h3>
            </div>
            <Link className="btn btn-secondary" to="/movies">All movies</Link>
          </div>
          <div className="showtime-list">
            {showTimes.length === 0 && <div className="empty-inline">No showtimes are available.</div>}
            {showTimes.map((s) => {
              const soldOut = Number(s.availableSeats || 0) <= 0

              return (
                <label key={s.id} className={`showtime-option ${selectedShow?.id === s.id ? 'selected' : ''} ${soldOut ? 'disabled' : ''}`}>
                  <input type="radio" name="show" checked={selectedShow?.id === s.id} disabled={soldOut} onChange={() => { setSelectedShow(s); setSeats(1); setStatus({ type: 'idle', message: '' }) }} />
                  <div>
                    <strong>{formatDateTime(s.datetime)}</strong>
                    <p className="muted">{formatCurrency(s.price)} per seat · {s.availableSeats} seats available</p>
                  </div>
                </label>
              )
            })}
          </div>
        </div>

        <aside className="booking-card">
          <p className="eyebrow">Book ticket</p>
          {!userId && (
            <div className="inline-warning">
              <strong>No active user</strong>
              <p>Login or register before booking.</p>
              <div className="button-row compact">
                <Link className="btn btn-secondary" to="/login">Login</Link>
                <Link className="btn btn-primary" to="/register">Register</Link>
              </div>
            </div>
          )}
          <label className="field">
            <span>Seats</span>
            <div className="stepper">
              <button type="button" onClick={() => setSeats((value) => Math.max(1, value - 1))} disabled={safeSeats <= 1}>-</button>
              <input
                className="field-input"
                type="number"
                value={safeSeats}
                min={1}
                max={Math.max(availableSeats, 1)}
                onChange={e => setSeats(parseInt(e.target.value || 1, 10))}
              />
              <button type="button" onClick={() => setSeats((value) => Math.min(Math.max(availableSeats, 1), value + 1))} disabled={availableSeats <= 0 || safeSeats >= availableSeats}>+</button>
            </div>
          </label>
          <label className="field">
            <span>Current user</span>
            <input className="field-input" value={userId || 'Not signed in'} readOnly />
          </label>
          <div className="summary-box">
            <span>Total</span>
            <strong>{selectedShow ? formatCurrency(total) : formatCurrency(0)}</strong>
          </div>
          {status.type !== 'idle' && (
            <div className={`status-banner ${status.type}`}>{status.message}</div>
          )}
          <button className="btn btn-primary btn-full" onClick={handleBook} disabled={!canBook}>
            {status.type === 'loading' ? 'Creating...' : 'Create booking'}
          </button>
        </aside>
      </div>
    </section>
  )
}
