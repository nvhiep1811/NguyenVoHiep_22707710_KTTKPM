import React, { useCallback, useEffect, useMemo, useState } from 'react'
import { Link } from 'react-router-dom'
import { formatCurrency, moviesApi } from '../api'
import { useToast } from '../components/Toast'

const initials = (title = 'MT') => title.split(' ').slice(0, 2).map((word) => word[0]).join('').toUpperCase()

export default function Home() {
  const [movies, setMovies] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const toast = useToast()

  const loadMovies = useCallback(() => {
    setLoading(true)
    setError('')
    return moviesApi
      .list()
      .then(setMovies)
      .catch(() => {
        setMovies([])
        setError('Movie service is not responding.')
        toast.error('Movie service is not responding.', 'Catalog unavailable')
      })
      .finally(() => setLoading(false))
  }, [toast])

  useEffect(() => {
    loadMovies()
  }, [loadMovies])

  const stats = useMemo(() => {
    const allMovies = Array.isArray(movies) ? movies : []
    const showCount = allMovies.reduce((sum, movie) => sum + (Array.isArray(movie.showTimes) ? movie.showTimes.length : 0), 0)
    const lowestPrice = allMovies
      .flatMap((movie) => Array.isArray(movie.showTimes) ? movie.showTimes : [])
      .map((show) => Number(show.price))
      .filter((price) => Number.isFinite(price))
      .sort((a, b) => a - b)[0]

    return {
      movieCount: allMovies.length,
      showCount,
      lowestPrice
    }
  }, [movies])

  return (
    <div className="page-stack">
      <section className="page-hero compact-hero">
        <div>
          <p className="eyebrow">Now playing</p>
          <h2>Movies</h2>
          <p className="lead">Choose a title, select a showtime, and create a booking.</p>
        </div>
        <div className="hero-stats" aria-label="Catalog summary">
          <div>
            <span>{stats.movieCount}</span>
            <p>movies</p>
          </div>
          <div>
            <span>{stats.showCount}</span>
            <p>showtimes</p>
          </div>
          <div>
            <span>{stats.lowestPrice == null ? '--' : formatCurrency(stats.lowestPrice)}</span>
            <p>from</p>
          </div>
        </div>
      </section>

      <section className="toolbar">
        <div>
          <h3>Catalog</h3>
          <p className="muted">{loading ? 'Loading...' : `${stats.movieCount} titles available`}</p>
        </div>
        <button className="btn btn-secondary" type="button" onClick={loadMovies} disabled={loading}>
          {loading ? 'Refreshing...' : 'Refresh'}
        </button>
      </section>

      <div className="movie-grid">
        {loading && (
          Array.from({ length: 6 }, (_, index) => <div key={index} className="movie-card skeleton-card" />)
        )}
        {!loading && movies.length === 0 && (
          <div className="empty-page full-span">
            <div>
              <p className="eyebrow">Empty catalog</p>
              <h3>{error || 'No movies found'}</h3>
              <p className="muted">Check movie-service and gateway logs if the list stays empty.</p>
            </div>
            <button className="btn btn-primary" type="button" onClick={loadMovies}>Try again</button>
          </div>
        )}
        {(Array.isArray(movies) ? movies : []).map((m) => (
          <article key={m.id} className="movie-card">
            <div className="poster-wrap">
              {m.posterUrl && (
                <img className="poster" src={m.posterUrl} alt={m.title} onError={(event) => { event.currentTarget.style.display = 'none' }} />
              )}
              <div className="poster-fallback">{initials(m.title)}</div>
            </div>
            <div className="movie-card-body">
              <div className="movie-topline">
                <span className="tag">{m.genre}</span>
                <span className="muted">{m.duration} min</span>
              </div>
              <h4>{m.title}</h4>
              <p className="muted clamp-3">{m.description || 'Multiple showtimes are available for this movie.'}</p>
              <Link className="btn btn-primary btn-full" to={`/movies/${m.id}`}>Book tickets</Link>
            </div>
          </article>
        ))}
      </div>
    </div>
  )
}
