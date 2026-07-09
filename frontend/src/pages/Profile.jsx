import { useEffect, useState } from 'react';
import { apiClient, getStoredAuth } from '../api/client.js';
import Button from '../components/Button.jsx';
import Navbar from '../components/Navbar.jsx';
import PostCard from '../components/PostCard.jsx';

function formatBookingDate(value) {
  if (!value) return '';

  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return value;
  }

  return date.toLocaleDateString('en-GB', {
    weekday: 'short',
    day: 'numeric',
    month: 'short',
    year: 'numeric',
  });
}

function formatBookingTime(value) {
  if (!value) return '';

  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return value;
  }

  return date.toLocaleTimeString('en-GB', {
    hour: '2-digit',
    minute: '2-digit',
  });
}

function formatPrice(value) {
  const amount = Number(value);
  if (Number.isNaN(amount)) {
    return value;
  }

  return `₾${amount % 1 === 0 ? amount.toFixed(0) : amount.toFixed(2)}`;
}

export default function Profile() {
  const session = getStoredAuth();
  const [bookings, setBookings] = useState([]);
  const [trainerBookings, setTrainerBookings] = useState([]);
  const [posts, setPosts] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');
  const [trainerBookingsLoading, setTrainerBookingsLoading] = useState(true);
  const [trainerBookingsError, setTrainerBookingsError] = useState('');
  const [postsLoading, setPostsLoading] = useState(true);
  const [postsError, setPostsError] = useState('');

  useEffect(() => {
    if (!session?.userId) {
      window.location.hash = 'login';
      return undefined;
    }

    let ignoreResult = false;

    async function loadBookings() {
      setIsLoading(true);
      setError('');

      try {
        const response = await apiClient.getMyBookings();
        if (!ignoreResult) {
          setBookings(Array.isArray(response) ? response : []);
        }
      } catch (requestError) {
        if (!ignoreResult) {
          setError(requestError.message);
          setBookings([]);
        }
      } finally {
        if (!ignoreResult) {
          setIsLoading(false);
        }
      }
    }

    loadBookings();

    return () => {
      ignoreResult = true;
    };
  }, [session?.userId]);

  useEffect(() => {
    if (!session?.userId) {
      return undefined;
    }

    let ignoreResult = false;

    async function loadTrainerBookings() {
      setTrainerBookingsLoading(true);
      setTrainerBookingsError('');

      try {
        const response = session?.role === 'COACH'
          ? await apiClient.getTrainerIncomingBookings()
          : await apiClient.getMyTrainerBookings();
        if (!ignoreResult) {
          setTrainerBookings(Array.isArray(response) ? response : []);
        }
      } catch (requestError) {
        if (!ignoreResult) {
          setTrainerBookingsError(requestError.message);
          setTrainerBookings([]);
        }
      } finally {
        if (!ignoreResult) {
          setTrainerBookingsLoading(false);
        }
      }
    }

    loadTrainerBookings();

    return () => {
      ignoreResult = true;
    };
  }, [session?.userId, session?.role]);

  useEffect(() => {
    if (!session?.userId) {
      return undefined;
    }

    let ignoreResult = false;

    async function loadPosts() {
      setPostsLoading(true);
      setPostsError('');

      try {
        const data = await apiClient.getPosts();
        const mine = (Array.isArray(data) ? data : []).filter((post) => post.userId === session.userId);
        if (!ignoreResult) {
          setPosts(mine);
        }
      } catch (requestError) {
        if (!ignoreResult) {
          setPostsError(requestError.message || 'Failed to load your posts.');
          setPosts([]);
        }
      } finally {
        if (!ignoreResult) {
          setPostsLoading(false);
        }
      }
    }

    loadPosts();

    return () => {
      ignoreResult = true;
    };
  }, [session?.userId]);

  if (!session?.userId) {
    return null;
  }

  const displayName = session.fullName || 'Player';
  const roleLabel = session.role || 'PLAYER';
  const removePostFromHistory = (postId) => {
    setPosts((currentPosts) => currentPosts.filter((post) => post.id !== postId));
  };

  return (
    <main className="app-shell profile-page">
      <Navbar />

      <section className="profile section">
        <div className="container">
          <div className="profile__hero">
            <div className="profile__avatar" aria-hidden="true">
              {displayName.charAt(0).toUpperCase()}
            </div>
            <div>
              <p className="eyebrow">Player profile</p>
              <h1>{displayName}</h1>
              <p className="profile__role">{roleLabel}</p>
            </div>
          </div>

          <div className="profile__grid">
            <section className="profile-panel">
              <div className="profile-panel__header">
                <h2>Booking history</h2>
                <span>{bookings.length} total</span>
              </div>

              {isLoading ? <p className="profile-panel__hint">Loading your bookings...</p> : null}
              {error ? <div className="notice notice--error">{error}</div> : null}

              {!isLoading && !error && bookings.length === 0 ? (
                <div className="profile-empty">
                  <p>No bookings yet.</p>
                  <Button href="#dashboard">Book a court</Button>
                </div>
              ) : null}

              {!isLoading && bookings.length > 0 ? (
                <div className="profile-bookings">
                  {bookings.map((booking) => (
                    <article className="profile-booking-card" key={booking.id}>
                      <div className="profile-booking-card__main">
                        <div className="profile-booking-card__top">
                          <p className="profile-booking-card__eyebrow">{booking.courtType}</p>
                          <span className="profile-booking-card__status">{booking.status || 'CONFIRMED'}</span>
                        </div>
                        <h3>{booking.courtName}</h3>
                        <p className="profile-booking-card__location">{booking.location}</p>
                        <p className="profile-booking-card__schedule">
                          {formatBookingDate(booking.startTime)}
                          {' · '}
                          {formatBookingTime(booking.startTime)} – {formatBookingTime(booking.endTime)}
                        </p>
                      </div>
                      <div className="profile-booking-card__meta">
                        <strong>{formatPrice(booking.totalPrice)}</strong>
                        <small>{booking.paymentStatus || 'PAID'}</small>
                      </div>
                    </article>
                  ))}
                </div>
              ) : null}
            </section>

            <section className="profile-panel">
              <div className="profile-panel__header">
                <h2>{session?.role === 'COACH' ? 'Incoming coach requests' : 'Coach session requests'}</h2>
                <span>{trainerBookings.length} total</span>
              </div>

              {trainerBookingsLoading ? (
                <p className="profile-panel__hint">Loading coach session requests...</p>
              ) : null}
              {trainerBookingsError ? <div className="notice notice--error">{trainerBookingsError}</div> : null}

              {!trainerBookingsLoading && !trainerBookingsError && trainerBookings.length === 0 ? (
                <div className="profile-empty">
                  <p>No coach session requests yet.</p>
                  <Button href="#coaches">Browse coaches</Button>
                </div>
              ) : null}

              {!trainerBookingsLoading && trainerBookings.length > 0 ? (
                <div className="profile-bookings">
                  {trainerBookings.map((booking) => (
                    <article className="profile-booking-card" key={`trainer-${booking.id}`}>
                      <div className="profile-booking-card__main">
                        <div className="profile-booking-card__top">
                          <p className="profile-booking-card__eyebrow">{booking.sportType}</p>
                          <span className="profile-booking-card__status">{booking.status || 'PENDING'}</span>
                        </div>
                        <h3>{booking.trainerName}</h3>
                        <p className="profile-booking-card__location">{booking.venueName}</p>
                        <p className="profile-booking-card__schedule">
                          {formatBookingDate(booking.requestedDate)}
                          {booking.requestedTimeSlot ? ` · ${booking.requestedTimeSlot}` : ''}
                        </p>
                      </div>
                      <div className="profile-booking-card__meta">
                        <strong>{formatPrice(booking.sessionPrice)}</strong>
                        <small>Coach session</small>
                      </div>
                    </article>
                  ))}
                </div>
              ) : null}
            </section>

            <section className="profile-panel profile-panel--muted">
              <div className="profile-panel__header">
                <h2>My posts</h2>
                <span>{posts.length} total</span>
              </div>

              {postsLoading ? <p className="profile-panel__hint">Loading your posts...</p> : null}
              {postsError ? <div className="notice notice--error">{postsError}</div> : null}

              {!postsLoading && !postsError && posts.length === 0 ? (
                <div className="profile-empty">
                  <p>You haven&apos;t posted anything yet.</p>
                  <Button href="#home">Create a post</Button>
                </div>
              ) : null}

              {!postsLoading && posts.length > 0 ? (
                <div className="profile-history__list">
                  {posts.map((post) => (
                    <PostCard
                      key={post.id}
                      post={post}
                      showComments={false}
                      expandableComments
                      onPostDeleted={removePostFromHistory}
                    />
                  ))}
                </div>
              ) : null}
            </section>
          </div>
        </div>
      </section>
    </main>
  );
}
