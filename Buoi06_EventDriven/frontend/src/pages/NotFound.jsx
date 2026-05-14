import React from 'react'
import { Link } from 'react-router-dom'

export default function NotFound() {
  return (
    <section className="empty-page">
      <div>
        <p className="eyebrow">404</p>
        <h2>Route not found</h2>
        <p className="muted">The page you opened is not registered in the frontend router.</p>
      </div>
      <Link className="btn btn-primary" to="/movies">Back to movies</Link>
    </section>
  )
}
