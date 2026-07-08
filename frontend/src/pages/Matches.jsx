import { useEffect, useState } from 'react';
import { apiClient, getStoredAuth } from '../api/client.js';
import Button from '../components/Button.jsx';
import Navbar from '../components/Navbar.jsx';

const initialForm = {
  venue: '',
  matchTime: '',
  sportType: 'Football',
  playersNeeded: '1',
  skillLevel: '',
  contactInfo: '',
  notes: '',
};

function formatMatchDate(value) {
  if (!value) return '';

  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return value;
  }

  return date.toLocaleString('en-GB', {
    weekday: 'short',
    day: 'numeric',
    month: 'short',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
}

export default function Matches() {
  const auth = getStoredAuth();
  const isPlayer = auth?.role === 'PLAYER';
  const [matches, setMatches] = useState([]);
  const [form, setForm] = useState(initialForm);
  const [isLoading, setIsLoading] = useState(Boolean(auth));
  const [isSaving, setIsSaving] = useState(false);
  const [joiningId, setJoiningId] = useState(null);
  const [deletingId, setDeletingId] = useState(null);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  async function loadMatches() {
    if (!auth) {
      setMatches([]);
      setIsLoading(false);
      return;
    }

    setIsLoading(true);
    setError('');

    try {
      const data = await apiClient.getMatches();
      setMatches(Array.isArray(data) ? data : []);
    } catch (requestError) {
      setMatches([]);
      setError(requestError.message || 'Failed to load matches.');
    } finally {
      setIsLoading(false);
    }
  }

  useEffect(() => {
    loadMatches();
  }, [auth?.userId]);

  function updateForm(field, value) {
    setForm((currentForm) => ({
      ...currentForm,
      [field]: value,
    }));
  }

  async function handleCreateMatch(event) {
    event.preventDefault();
    if (!isPlayer || isSaving) return;

    setIsSaving(true);
    setError('');
    setSuccess('');

    try {
      const response = await apiClient.createMatch({
        venue: form.venue,
        matchTime: form.matchTime,
        sportType: form.sportType,
        playersNeeded: Number(form.playersNeeded),
        skillLevel: form.skillLevel,
        contactInfo: form.contactInfo,
        notes: form.notes,
      });

      if (response?.success) {
        setForm(initialForm);
        setSuccess('Match announcement created.');
        await loadMatches();
      } else {
        setError(response?.message || 'Could not create match announcement.');
      }
    } catch (requestError) {
      setError(requestError.message || 'Could not create match announcement.');
    } finally {
      setIsSaving(false);
    }
  }

  async function handleJoin(matchId) {
    if (!isPlayer || joiningId) return;

    setJoiningId(matchId);
    setError('');
    setSuccess('');

    try {
      const response = await apiClient.joinMatch(matchId);
      if (response?.success) {
        setSuccess('You joined the match.');
        await loadMatches();
      } else {
        setError(response?.message || 'Could not join this match.');
      }
    } catch (requestError) {
      setError(requestError.message || 'Could not join this match.');
    } finally {
      setJoiningId(null);
    }
  }

  async function handleDelete(matchId) {
    if (deletingId || !window.confirm('Delete this match announcement?')) return;

    setDeletingId(matchId);
    setError('');
    setSuccess('');

    try {
      const response = await apiClient.deleteMatch(matchId);
      if (response?.success) {
        setSuccess('Match announcement deleted.');
        setMatches((currentMatches) => currentMatches.filter((match) => match.id !== matchId));
      } else {
        setError(response?.message || 'Could not delete this match announcement.');
      }
    } catch (requestError) {
      setError(requestError.message || 'Could not delete this match announcement.');
    } finally {
      setDeletingId(null);
    }
  }

  return (
    <main className="app-shell matches-page">
      <Navbar />

      <section className="section matches">
        <div className="container matches__layout">
          <div className="matches__header">
            <div>
              <p className="eyebrow">Open games</p>
              <h1>Matches</h1>
              <p>Find local games that need players, or post your own match announcement.</p>
            </div>
            {!auth ? <Button href="#login">Sign in to view matches</Button> : null}
          </div>

          {auth ? (
            <div className="matches__grid">
              <section className="match-form-panel">
                <div className="match-form-panel__header">
                  <h2>Create announcement</h2>
                  <span>PLAYER only</span>
                </div>

                {!isPlayer ? (
                  <div className="notice notice--error">Only players can create match announcements.</div>
                ) : (
                  <form className="match-form" onSubmit={handleCreateMatch}>
                    <label className="field">
                      <span>Stadium or venue</span>
                      <input
                        type="text"
                        value={form.venue}
                        onChange={(event) => updateForm('venue', event.target.value)}
                        placeholder="Saburtalo Football Arena"
                        required
                      />
                    </label>

                    <div className="match-form__row">
                      <label className="field">
                        <span>Date and time</span>
                        <input
                          type="datetime-local"
                          value={form.matchTime}
                          onChange={(event) => updateForm('matchTime', event.target.value)}
                          required
                        />
                      </label>

                      <label className="field">
                        <span>Sport type</span>
                        <select
                          value={form.sportType}
                          onChange={(event) => updateForm('sportType', event.target.value)}
                          required
                        >
                          <option>Football</option>
                          <option>Basketball</option>
                          <option>Tennis</option>
                          <option>Padel</option>
                          <option>Volleyball</option>
                          <option>Other</option>
                        </select>
                      </label>
                    </div>

                    <div className="match-form__row">
                      <label className="field">
                        <span>Players needed</span>
                        <input
                          type="number"
                          min="1"
                          value={form.playersNeeded}
                          onChange={(event) => updateForm('playersNeeded', event.target.value)}
                          required
                        />
                      </label>

                      <label className="field">
                        <span>Skill level</span>
                        <input
                          type="text"
                          value={form.skillLevel}
                          onChange={(event) => updateForm('skillLevel', event.target.value)}
                          placeholder="Beginner, mixed, advanced"
                        />
                      </label>
                    </div>

                    <label className="field">
                      <span>Contact info</span>
                      <input
                        type="text"
                        value={form.contactInfo}
                        onChange={(event) => updateForm('contactInfo', event.target.value)}
                        placeholder="Phone, email, or social handle"
                      />
                    </label>

                    <label className="field">
                      <span>Notes</span>
                      <textarea
                        value={form.notes}
                        onChange={(event) => updateForm('notes', event.target.value)}
                        placeholder="Bring dark shirts, indoor shoes, split court cost..."
                        rows="4"
                      />
                    </label>

                    <Button type="submit" disabled={isSaving}>
                      {isSaving ? 'Creating...' : 'Create match'}
                    </Button>
                  </form>
                )}
              </section>

              <section className="matches-list">
                <div className="matches-list__header">
                  <h2>Open announcements</h2>
                  <span>{matches.length} total</span>
                </div>

                {error ? <div className="notice notice--error">{error}</div> : null}
                {success ? <div className="notice notice--success">{success}</div> : null}
                {isLoading ? <p className="matches__hint">Loading matches...</p> : null}

                {!isLoading && matches.length === 0 ? (
                  <div className="matches-empty">
                    <p>No match announcements yet.</p>
                  </div>
                ) : null}

                {!isLoading && matches.length > 0 ? (
                  <div className="match-card-list">
                    {matches.map((match) => {
                      const isOwnMatch = auth?.userId === match.userId;
                      const isFull = Number(match.playersNeeded) <= 0;
                      const cannotJoin = !isPlayer || isOwnMatch || isFull || match.joinedByCurrentUser;

                      return (
                        <article className="match-card" key={match.id}>
                          <div className="match-card__meta-row">
                            <div className="match-card__tags">
                              <span className="match-card__sport">{match.sportType}</span>
                              <span className={`match-card__spots${isFull ? ' is-full' : ''}`}>
                                {isFull ? 'Full' : `${match.playersNeeded} needed`}
                              </span>
                            </div>
                            <p className="match-card__time">{formatMatchDate(match.matchTime)}</p>
                          </div>

                          <h3>{match.venue}</h3>

                          <dl className="match-card__details">
                            {match.skillLevel ? (
                              <>
                                <dt>Level</dt>
                                <dd>{match.skillLevel}</dd>
                              </>
                            ) : null}
                            {match.contactInfo ? (
                              <>
                                <dt>Contact</dt>
                                <dd>{match.contactInfo}</dd>
                              </>
                            ) : null}
                          </dl>

                          {match.notes ? <p className="match-card__notes">{match.notes}</p> : null}

                          <div className="match-card__actions">
                            {isOwnMatch ? <span>Your announcement</span> : null}
                            {match.joinedByCurrentUser ? <span>Joined</span> : null}
                            {isOwnMatch ? (
                              <Button
                                type="button"
                                variant="outline"
                                disabled={deletingId === match.id}
                                onClick={() => handleDelete(match.id)}
                              >
                                {deletingId === match.id ? 'Deleting...' : 'Delete'}
                              </Button>
                            ) : (
                              <Button
                                type="button"
                                variant="outline"
                                disabled={cannotJoin || joiningId === match.id}
                                onClick={() => handleJoin(match.id)}
                              >
                                {joiningId === match.id ? 'Joining...' : 'Join match'}
                              </Button>
                            )}
                          </div>
                        </article>
                      );
                    })}
                  </div>
                ) : null}
              </section>
            </div>
          ) : (
            <div className="matches-empty">
              <p>Sign in as a player to view and create match announcements.</p>
            </div>
          )}
        </div>
      </section>
    </main>
  );
}
