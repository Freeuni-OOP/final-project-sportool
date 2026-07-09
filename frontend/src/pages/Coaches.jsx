import { useEffect, useMemo, useState } from 'react';
import { apiClient, getStoredAuth } from '../api/client.js';
import Button from '../components/Button.jsx';
import Navbar from '../components/Navbar.jsx';

const SPORT_FILTERS = ['All', 'Tennis', 'Football', 'Padel', 'Basketball'];

function formatPrice(value, fallback) {
  const amount = value != null ? Number(value) : Number(fallback);
  if (Number.isNaN(amount)) return 'Price on request';
  return `₾${amount % 1 === 0 ? amount.toFixed(0) : amount.toFixed(2)}`;
}

export default function Coaches() {
  const auth = getStoredAuth();
  const [trainers, setTrainers] = useState([]);
  const [selectedTrainer, setSelectedTrainer] = useState(null);
  const [reviews, setReviews] = useState([]);
  const [activeSport, setActiveSport] = useState('All');
  const [bookingForm, setBookingForm] = useState({
    trainerVenueId: '',
    requestedDate: '',
    requestedTimeSlot: '',
  });
  const [reviewForm, setReviewForm] = useState({
    rating: '5',
    comment: '',
  });
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isSavingReview, setIsSavingReview] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  useEffect(() => {
    let ignoreResult = false;

    async function loadTrainers() {
      setIsLoading(true);
      setError('');

      try {
        const data = await apiClient.getTrainers();
        if (!ignoreResult) {
          setTrainers(Array.isArray(data) ? data : []);
        }
      } catch (requestError) {
        if (!ignoreResult) {
          setError(requestError.message || 'Failed to load coaches.');
          setTrainers([]);
        }
      } finally {
        if (!ignoreResult) {
          setIsLoading(false);
        }
      }
    }

    loadTrainers();
    return () => {
      ignoreResult = true;
    };
  }, []);

  const filteredTrainers = useMemo(() => {
    if (activeSport === 'All') {
      return trainers;
    }

    return trainers.filter((trainer) =>
      trainer.sportType?.toLowerCase() === activeSport.toLowerCase());
  }, [trainers, activeSport]);

  async function openTrainer(trainerId) {
    setError('');
    setSuccess('');

    try {
      const trainer = await apiClient.getTrainer(trainerId);
      setSelectedTrainer(trainer);
      const reviewData = await apiClient.getTrainerReviews(trainerId);
      setReviews(Array.isArray(reviewData) ? reviewData : []);
      setBookingForm({
        trainerVenueId: trainer?.venues?.[0]?.id ? String(trainer.venues[0].id) : '',
        requestedDate: '',
        requestedTimeSlot: '',
      });
      setReviewForm({
        rating: '5',
        comment: '',
      });
    } catch (requestError) {
      setError(requestError.message || 'Failed to load coach details.');
    }
  }

  async function handleBookingSubmit(event) {
    event.preventDefault();

    if (!auth?.userId) {
      window.location.hash = 'login';
      return;
    }

    setIsSubmitting(true);
    setError('');
    setSuccess('');

    try {
      const response = await apiClient.createTrainerBooking({
        trainerVenueId: Number(bookingForm.trainerVenueId),
        requestedDate: bookingForm.requestedDate,
        requestedTimeSlot: bookingForm.requestedTimeSlot || null,
      });

      if (response?.success) {
        setSuccess('Booking request submitted. The coach will confirm or decline it.');
        setBookingForm((current) => ({
          ...current,
          requestedDate: '',
          requestedTimeSlot: '',
        }));
      } else {
        setError(response?.message || 'Could not submit booking request.');
      }
    } catch (requestError) {
      setError(requestError.message || 'Could not submit booking request.');
    } finally {
      setIsSubmitting(false);
    }
  }

  async function handleReviewSubmit(event) {
    event.preventDefault();

    if (!auth?.userId) {
      window.location.hash = 'login';
      return;
    }

    if (!selectedTrainer?.id) {
      return;
    }

    setIsSavingReview(true);
    setError('');
    setSuccess('');

    try {
      const response = await apiClient.createTrainerReview({
        trainerId: selectedTrainer.id,
        rating: Number(reviewForm.rating),
        comment: reviewForm.comment || null,
      });

      if (response?.success) {
        setSuccess('Review submitted. Thank you!');
        const updated = await apiClient.getTrainer(selectedTrainer.id);
        setSelectedTrainer(updated);
        const reviewData = await apiClient.getTrainerReviews(selectedTrainer.id);
        setReviews(Array.isArray(reviewData) ? reviewData : []);
        setReviewForm({ rating: '5', comment: '' });
      } else {
        setError(response?.message || 'Could not submit review.');
      }
    } catch (requestError) {
      setError(requestError.message || 'Could not submit review.');
    } finally {
      setIsSavingReview(false);
    }
  }

  return (
    <main className="app-shell">
      <Navbar />

      <section className="info-page section">
        <div className="container info-page__grid">
          <div>
            <p className="eyebrow">Verified performance staff</p>
            <h1 className="coaches__title">Coaches</h1>
            <p>Browse trainers by sport, review venues and availability, then request a coaching session.</p>
          </div>

          <div className="info-panel">
            <span>How it works</span>
            <strong>Request · Confirm · Train</strong>
            <p>
              Send a request to a coach. They confirm or decline it from their dashboard.
              Track the status in your profile.
            </p>
          </div>
        </div>
      </section>

      <section className="section section--tight">
        <div className="container">
          <div className="coaches__filters" role="tablist" aria-label="Sport categories">
            {SPORT_FILTERS.map((sport) => (
              <button
                key={sport}
                type="button"
                role="tab"
                aria-selected={activeSport === sport}
                className={`coaches__filter${activeSport === sport ? ' is-active' : ''}`}
                onClick={() => setActiveSport(sport)}
              >
                {sport}
              </button>
            ))}
          </div>

          {isLoading ? <p className="profile-panel__hint">Loading coaches...</p> : null}
          {error ? <div className="notice notice--error">{error}</div> : null}
          {success ? <div className="notice notice--success">{success}</div> : null}

          {!isLoading && filteredTrainers.length === 0 ? (
            <div className="profile-empty">
              <p>No coaches found for this sport yet.</p>
            </div>
          ) : null}

          <div className="coaches__grid">
            {filteredTrainers.map((trainer) => (
              <article className="profile-panel coach-card" key={trainer.id}>
                <div className="profile-panel__header">
                  <h2>{trainer.firstName} {trainer.lastName}</h2>
                  <span>{trainer.sportType}</span>
                </div>
                <p className="profile__role">
                  {trainer.rating?.toFixed?.(1) || '0.0'} ({trainer.reviewCount || 0}) · {trainer.phone}
                </p>
                <p className="profile-panel__hint">
                  From {formatPrice(null, trainer.pricePerSession)} per session · {(trainer.venues || []).length} venue(s)
                </p>
                <Button onClick={() => openTrainer(trainer.id)}>View profile</Button>
              </article>
            ))}
          </div>

          {selectedTrainer ? (
            <section className="profile-panel coach-detail">
              <div className="profile-panel__header">
                <h2>{selectedTrainer.firstName} {selectedTrainer.lastName}</h2>
                <Button variant="outline" onClick={() => setSelectedTrainer(null)}>Close</Button>
              </div>

              <div className="coach-detail__meta">
                <p className="coach-detail__subtitle">
                  {selectedTrainer.sportType} · {selectedTrainer.phone}
                </p>
              </div>

              {selectedTrainer.description ? (
                <div className="coach-detail__bio">
                  <p className="coach-detail__bio-label">Coach bio</p>
                  <p className="coach-detail__bio-text">{selectedTrainer.description}</p>
                </div>
              ) : null}

              <div className="coach-detail__grid">
                <div>
                  <h3>Venues & availability</h3>
                  {(selectedTrainer.venues || []).length === 0 ? (
                    <p className="profile-panel__hint">This coach has not published venues yet.</p>
                  ) : (
                    (selectedTrainer.venues || []).map((venue) => (
                      <article className="info-panel coach-venue-card coach-venue-card--spaced" key={venue.id}>
                        <strong>{venue.venueName}</strong>
                        <p className="profile-panel__hint">{venue.address || 'No address provided'}</p>
                        <p className="profile-panel__hint">
                          {formatPrice(venue.priceOverride, selectedTrainer.pricePerSession)}
                        </p>
                        <div className="coach-slot-list">
                          {(venue.availability || []).map((slot, index) => (
                            <span className="coach-slot-pill" key={`${venue.id}-${index}`}>
                              {slot.dayOfWeek} {slot.startTime}-{slot.endTime}
                            </span>
                          ))}
                        </div>
                      </article>
                    ))
                  )}
                </div>

                <form onSubmit={handleBookingSubmit}>
                  <h3>Request a session</h3>
                  {!auth?.userId ? (
                    <p className="profile-panel__hint">Sign in to request a coaching session.</p>
                  ) : null}

                  <label className="field">
                    <span>Venue</span>
                    <select
                      required
                      value={bookingForm.trainerVenueId}
                      onChange={(event) => setBookingForm((current) => ({
                        ...current,
                        trainerVenueId: event.target.value,
                      }))}
                      disabled={!auth?.userId || !(selectedTrainer.venues || []).length}
                    >
                      <option value="">Select a venue</option>
                      {(selectedTrainer.venues || []).map((venue) => (
                        <option key={venue.id} value={venue.id}>{venue.venueName}</option>
                      ))}
                    </select>
                  </label>

                  <label className="field">
                    <span>Requested date</span>
                    <input
                      type="date"
                      required
                      value={bookingForm.requestedDate}
                      onChange={(event) => setBookingForm((current) => ({
                        ...current,
                        requestedDate: event.target.value,
                      }))}
                      disabled={!auth?.userId}
                    />
                  </label>

                  <label className="field">
                    <span>Preferred time slot (optional)</span>
                    <input
                      placeholder="e.g. 18:00-20:00"
                      value={bookingForm.requestedTimeSlot}
                      onChange={(event) => setBookingForm((current) => ({
                        ...current,
                        requestedTimeSlot: event.target.value,
                      }))}
                      disabled={!auth?.userId}
                    />
                  </label>

                  <Button type="submit" disabled={!auth?.userId || isSubmitting}>
                    {isSubmitting ? 'Submitting...' : 'Submit booking request'}
                  </Button>
                </form>
              </div>

              <div className="coach-detail__grid coach-detail__grid--reviews">
                <div>
                  <h3>Reviews</h3>
                  {reviews.length === 0 ? (
                    <p className="profile-panel__hint">No reviews yet.</p>
                  ) : (
                    <div className="coach-reviews">
                      {reviews.map((review) => (
                        <article className="info-panel coach-review-card" key={review.id}>
                          <div className="coach-review-card__top">
                            <strong>{review.playerName}</strong>
                            <span className="coach-review-card__rating">{review.rating}/5</span>
                          </div>
                          <p className="coach-review-card__comment">{review.comment || 'No comment provided.'}</p>
                        </article>
                      ))}
                    </div>
                  )}
                </div>

                <form onSubmit={handleReviewSubmit}>
                  <h3>Write a review</h3>
                  {!auth?.userId ? (
                    <p className="profile-panel__hint">Sign in to leave a review.</p>
                  ) : null}

                  <label className="field">
                    <span>Rating</span>
                    <select
                      value={reviewForm.rating}
                      onChange={(event) => setReviewForm((current) => ({
                        ...current,
                        rating: event.target.value,
                      }))}
                      disabled={!auth?.userId}
                    >
                      {[5, 4, 3, 2, 1].map((value) => (
                        <option key={value} value={String(value)}>{value}</option>
                      ))}
                    </select>
                  </label>

                  <label className="field">
                    <span>Comment (optional)</span>
                    <textarea
                      rows={4}
                      value={reviewForm.comment}
                      onChange={(event) => setReviewForm((current) => ({
                        ...current,
                        comment: event.target.value,
                      }))}
                      disabled={!auth?.userId}
                    />
                  </label>

                  <Button type="submit" disabled={!auth?.userId || isSavingReview}>
                    {isSavingReview ? 'Submitting...' : 'Submit review'}
                  </Button>
                </form>
              </div>
            </section>
          ) : null}
        </div>
      </section>
    </main>
  );
}