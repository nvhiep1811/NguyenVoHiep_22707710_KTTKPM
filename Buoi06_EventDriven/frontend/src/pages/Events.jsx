import React, { useCallback, useEffect, useMemo, useState } from 'react'
import { eventsApi, formatDateTime, getApiErrorMessage } from '../api'
import { useToast } from '../components/Toast'

const filters = ['ALL', 'USER_REGISTERED', 'BOOKING_CREATED', 'PAYMENT_COMPLETED', 'BOOKING_FAILED', 'DEAD_LETTER']

export default function Events() {
  const [events, setEvents] = useState([])
  const [loading, setLoading] = useState(true)
  const [activeType, setActiveType] = useState('ALL')
  const [live, setLive] = useState('connecting')
  const toast = useToast()

  const loadEvents = useCallback(() => {
    setLoading(true)

    return eventsApi
      .list()
      .then((data) => setEvents(Array.isArray(data) ? data : []))
      .catch((err) => {
        setEvents([])
        toast.error(getApiErrorMessage(err, 'Cannot load event log.'), 'Event log unavailable')
      })
      .finally(() => setLoading(false))
  }, [toast])

  useEffect(() => {
    loadEvents()
  }, [loadEvents])

  useEffect(() => {
    if (!window.EventSource) {
      setLive('unsupported')
      return undefined
    }

    const source = new EventSource(eventsApi.streamUrl())
    const upsertEvent = (event) => {
      setEvents((items) => {
        if (!event?.id || items.some((item) => item.id === event.id)) return items
        return [event, ...items].slice(0, 100)
      })
    }

    source.addEventListener('open', () => setLive('connected'))
    source.addEventListener('ready', () => setLive('connected'))
    source.addEventListener('snapshot', (message) => upsertEvent(JSON.parse(message.data)))
    source.addEventListener('event', (message) => upsertEvent(JSON.parse(message.data)))
    source.addEventListener('error', () => setLive('reconnecting'))

    return () => {
      source.close()
    }
  }, [])

  const counts = useMemo(() => {
    return events.reduce((acc, event) => {
      acc[event.eventType] = (acc[event.eventType] || 0) + 1
      acc[event.status] = (acc[event.status] || 0) + 1
      return acc
    }, {})
  }, [events])

  const displayedEvents = useMemo(() => {
    if (activeType === 'ALL') return events
    if (activeType === 'DEAD_LETTER') return events.filter((event) => event.status === 'DEAD_LETTER')
    return events.filter((event) => event.eventType === activeType)
  }, [activeType, events])

  const renderPayload = (payload) => {
    if (payload == null) return 'No payload'
    if (typeof payload === 'string') return payload
    return JSON.stringify(payload, null, 2)
  }

  return (
    <section className="page-stack">
      <div className="toolbar">
        <div>
          <p className="eyebrow">Audit trail</p>
          <h2>Event log</h2>
          <p className="muted">{displayedEvents.length} events shown</p>
        </div>
        <div className="button-row compact">
          <span className={`live-pill ${live}`}>{live}</span>
          <button className="btn btn-secondary" type="button" onClick={loadEvents} disabled={loading}>{loading ? 'Refreshing...' : 'Refresh'}</button>
        </div>
      </div>

      <div className="segmented" role="tablist" aria-label="Event type filter">
        {filters.map((type) => (
          <button key={type} type="button" className={activeType === type ? 'active' : ''} onClick={() => setActiveType(type)}>
            {type === 'ALL' ? `ALL (${events.length})` : `${type} (${counts[type] || 0})`}
          </button>
        ))}
      </div>

      <div className="list-surface event-list">
        {loading && <div className="empty-inline">Loading event log...</div>}
        {!loading && displayedEvents.length === 0 && <div className="empty-inline">No events found.</div>}
        {displayedEvents.map((e) => (
          <article key={e.id} className="event-item">
            <div className="event-meta">
              <span className={`event-badge event-${e.status === 'DEAD_LETTER' ? 'DEAD_LETTER' : e.eventType}`}>{e.status === 'DEAD_LETTER' ? `${e.eventType}.DLQ` : e.eventType}</span>
              <small>{formatDateTime(e.timestamp)}</small>
            </div>
            <pre>{renderPayload(e.payload)}</pre>
          </article>
        ))}
      </div>
    </section>
  )
}
