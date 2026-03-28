import { useEffect, useState } from 'react';

const GATEWAY_BASE = import.meta.env.VITE_GATEWAY_URL || 'http://localhost:8080';

const API_BASE = {
  orders: `${GATEWAY_BASE}/api/orders`,
  payments: `${GATEWAY_BASE}/api/payments`,
  shipments: `${GATEWAY_BASE}/api/shipments`
};

function ServiceCard({ title, url }) {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [data, setData] = useState([]);

  useEffect(() => {
    let active = true;

    fetch(url)
      .then((res) => {
        if (!res.ok) {
          throw new Error(`HTTP ${res.status}`);
        }
        return res.json();
      })
      .then((json) => {
        if (active) {
          setData(json);
          setError('');
        }
      })
      .catch((err) => {
        if (active) {
          setError(err.message || 'Failed to fetch');
          setData([]);
        }
      })
      .finally(() => {
        if (active) {
          setLoading(false);
        }
      });

    return () => {
      active = false;
    };
  }, [url]);

  return (
    <section className="card">
      <h2>{title}</h2>
      <p className="endpoint">{url}</p>
      {loading && <p>Loading...</p>}
      {error && <p className="error">Error: {error}</p>}
      {!loading && !error && (
        <pre>{JSON.stringify(data, null, 2)}</pre>
      )}
    </section>
  );
}

export default function App() {
  return (
    <main className="container">
      <h1>Service-Based System Demo</h1>
      <p className="subtitle">
        React frontend calling API Gateway, and gateway routes to 3 independent Spring Boot services.
      </p>

      <div className="grid">
        <ServiceCard title="Order Service" url={API_BASE.orders} />
        <ServiceCard title="Payment Service" url={API_BASE.payments} />
        <ServiceCard title="Shipping Service" url={API_BASE.shipments} />
      </div>
    </main>
  );
}
