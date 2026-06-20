import { useEffect, useMemo, useState } from 'react';
import { API_BASE_URL, apiClient, getStoredAuth } from '../api/client.js';
import CourtCard from '../components/CourtCard.jsx';
import Navbar from '../components/Navbar.jsx';

const surfaceOptions = [
  { label: 'All surfaces', value: 'all' },
  { label: 'Clay', value: 'Clay' },
  { label: 'Grass', value: 'Grass' },
  { label: 'Hard', value: 'Hard' },
];

export default function Dashboard() {
  const [surface, setSurface] = useState('all');
  const [courts, setCourts] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');
  const authSession = getStoredAuth();

  useEffect(() => {
    let ignoreResult = false;

    async function loadCourts() {
      setIsLoading(true);
      setError('');

      try {
        const courtResponse = await apiClient.getCourts({ type: surface });
        const nextCourts = Array.isArray(courtResponse)
          ? courtResponse
          : courtResponse?.courts || [];

        if (!ignoreResult) {
          setCourts(nextCourts);
        }
      } catch (requestError) {
        if (!ignoreResult) {
          setError(requestError.message);
          setCourts([]);
        }
      } finally {
        if (!ignoreResult) {
          setIsLoading(false);
        }
      }
    }

    loadCourts();

    return () => {
      ignoreResult = true;
    };
  }, [surface]);

  return (
    <main className="app-shell">
      <Navbar />

      <section className="dashboard section">
        <div className="container">
          <div className="dashboard__header">
            <div>
              <p className="eyebrow">Live court discovery</p>
              <h1>Choose your surface. Lock your slot.</h1>
              <p>
                Courts are fetched from the Java Servlet REST endpoint and
                filtered in the React UI without a page refresh.
              </p>
            </div>

            <div className="dashboard__session">
              <span>Signed in as</span>
              <strong>{authSession?.fullName || 'Guest Player'}</strong>
              <small>{authSession?.role || 'Member'}</small>
            </div>
          </div>

          <div className="filter-bar">
            <label>
              <span>Surface</span>
              <select value={surface} onChange={(event) => setSurface(event.target.value)}>
                {surfaceOptions.map((option) => (
                  <option key={option.value} value={option.value}>
                    {option.label}
                  </option>
                ))}
              </select>
            </label>
          </div>

          {error ? (
            <div className="empty-state" role="alert">
              <p className="eyebrow">Server unavailable</p>
              <h2>We could not load courts.</h2>
              <p>{error}</p>
              <span>GET {API_BASE_URL}/courts</span>
            </div>
          ) : null}

          {isLoading ? (
            <div className="loading-grid">
              {Array.from({ length: 6 }).map((_, index) => (
                <div className="court-skeleton" key={index} />
              ))}
            </div>
          ) : null}

          {!isLoading && !error ? (
            <div className="court-grid">
              {courts.map((court, index) => (
                <CourtCard
                  court={court}
                  key={court.id || court.name || court.courtName || index}
                />
              ))}
            </div>
          ) : null}
        </div>
      </section>
    </main>
  );
}