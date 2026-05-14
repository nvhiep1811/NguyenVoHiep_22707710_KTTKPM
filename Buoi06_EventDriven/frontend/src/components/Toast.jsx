import React, { createContext, useCallback, useContext, useMemo, useState } from 'react'

const ToastContext = createContext(null)

const titles = {
  success: 'Success',
  error: 'Error',
  info: 'Notice',
  loading: 'Working'
}

export function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([])

  const removeToast = useCallback((id) => {
    setToasts((items) => items.filter((toast) => toast.id !== id))
  }, [])

  const showToast = useCallback((toast) => {
    const id = globalThis.crypto?.randomUUID ? globalThis.crypto.randomUUID() : `${Date.now()}-${Math.random()}`
    const nextToast = {
      id,
      type: toast.type || 'info',
      title: toast.title || titles[toast.type || 'info'],
      message: toast.message,
      timeout: toast.timeout ?? 4200
    }

    setToasts((items) => [nextToast, ...items].slice(0, 4))

    if (nextToast.timeout > 0) {
      window.setTimeout(() => removeToast(id), nextToast.timeout)
    }

    return id
  }, [removeToast])

  const api = useMemo(() => ({
    show: showToast,
    success: (message, title) => showToast({ type: 'success', title, message }),
    error: (message, title) => showToast({ type: 'error', title, message }),
    info: (message, title) => showToast({ type: 'info', title, message }),
    remove: removeToast
  }), [removeToast, showToast])

  return (
    <ToastContext.Provider value={api}>
      {children}
      <div className="toast-region" aria-live="polite" aria-atomic="true">
        {toasts.map((toast) => (
          <div key={toast.id} className={`toast toast-${toast.type}`}>
            <div className="toast-content">
              <strong>{toast.title}</strong>
              {toast.message && <p>{toast.message}</p>}
            </div>
            <button className="toast-close" type="button" onClick={() => removeToast(toast.id)} aria-label="Dismiss notification">
              x
            </button>
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  )
}

export function useToast() {
  const context = useContext(ToastContext)

  if (!context) {
    throw new Error('useToast must be used inside ToastProvider')
  }

  return context
}
