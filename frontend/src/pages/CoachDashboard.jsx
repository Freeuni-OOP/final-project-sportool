import { useEffect, useState } from 'react';
import { apiClient, getStoredAuth } from '../api/client.js';
import Button from '../components/Button.jsx';
import Navbar from '../components/Navbar.jsx';

const DAYS = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'];

const emptyVenueForm = {
  venueName: '',
  address: '',
  priceOverride: '',
  dayOfWeek: 'MONDAY',
  startTime: '18:00',
  endTime: '20:00',
};

function formatDate(value) {
  if (!value) return '';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  return date.toLocaleDateString('en-GB', {
    weekday: 'short',
    day: 'numeric',
    month: 'short',
    year: 'numeric',
  });
}

export default function CoachDashboard() {
  const auth = getStoredAuth();
  const [trainer, setTrainer] = useState(null);
  const [venues, setVenues] = useState([]);
  const [bookings, setBookings] = useState([]);
  const [description, setDescription] = useState('');
  const [profileForm, setProfileForm] = useState({
    firstName: '',
    lastName: '',
    phone: '',
    sportType: 'Tennis',
    pricePerSession: '50',
  });
  const [venueForm, setVenueForm] = useState(emptyVenueForm);
  const [editingVenueId, setEditingVenueId] = useState(null);
  const [editingSlots, setEditingSlots] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isSavingProfile, setIsSavingProfile] = useState(false);
  const [isSavingVenue, setIsSavingVenue] = useState(false);
  const [isSavingDescription, setIsSavingDescription] = useState(false);
  const [updatingBookingId, setUpdatingBookingId] = useState(null);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  async function loadDashboard() {
    if (!auth?.userId || auth.role !== 'COACH') {
      setIsLoading(false);
      return;
    }

    setIsLoading(true);
    setError('');

    try {
      const profileResponse = await apiClient.getMyTrainerProfile();
      const trainerData = profileResponse?.trainer;
      setTrainer(trainerData);
      setVenues(Array.isArray(trainerData?.venues) ? trainerData.venues : []);
      setDescription(trainerData?.description || '');
      setProfileForm({
        firstName: trainerData?.firstName || '',
        lastName: trainerData?.lastName || '',
        phone: trainerData?.phone || '',
        sportType: trainerData?.sportType || 'Tennis',
        pricePerSession: String(trainerData?.pricePerSession || 50),
      });

      const bookingData = await apiClient.getTrainerIncomingBookings();
      setBookings(Array.isArray(bookingData) ? bookingData : []);
    } catch (requestError) {
      setError(requestError.message || 'Failed to load coach dashboard.');
    } finally {
      setIsLoading(false);
    }
  }

  useEffect(() => {
    if (!auth?.userId) {
      window.location.hash = 'login';
      return undefined;
    }

    if (auth.role !== 'COACH') {
      window.location.hash = 'home';
      return undefined;
    }

    loadDashboard();
    return undefined;
  }, [auth?.userId, auth?.role]);

  function resetVenueForm() {
    setVenueForm(emptyVenueForm);
    setEditingVenueId(null);
    setEditingSlots([]);
  }

  function addSlotToForm() {
    setEditingSlots((current) => [
      ...current,
      {
        dayOfWeek: venueForm.dayOfWeek,
        startTime: venueForm.startTime,
        endTime: venueForm.endTime,
      },
    ]);
  }

  function removeSlot(index) {
    setEditingSlots((current) => current.filter((_, slotIndex) => slotIndex !== index));
  }

  function startEditVenue(venue) {
    setEditingVenueId(venue.id);
    setVenueForm({
      venueName: venue.venueName || '',
      address: venue.address || '',
      priceOverride: venue.priceOverride != null ? String(venue.priceOverride) : '',
      dayOfWeek: 'MONDAY',
      startTime: '18:00',
      endTime: '20:00',
    });
    setEditingSlots(Array.isArray(venue.availability) ? [...venue.availability] : []);
  }

  async function handleSaveProfile(event) {
    event.preventDefault();
    setIsSavingProfile(true);
    setError('');
    setSuccess('');

    try {
      const response = await apiClient.updateMyTrainerProfile({
        firstName: profileForm.firstName,
        lastName: profileForm.lastName,
        phone: profileForm.phone,
        sportType: profileForm.sportType,
        pricePerSession: Number(profileForm.pricePerSession),
      });

      if (response?.success) {
        setSuccess('Trainer profile updated.');
        await loadDashboard();
      } else {
        setError(response?.message || 'Could not update trainer profile.');
      }
    } catch (requestError) {
      setError(requestError.message || 'Could not update trainer profile.');
    } finally {
      setIsSavingProfile(false);
    }
  }

  async function handleSaveDescription(event) {
    event.preventDefault();
    setIsSavingDescription(true);
    setError('');
    setSuccess('');

    try {
      const response = await apiClient.updateMyTrainerDescription(description);
      if (response?.success) {
        setSuccess('Description updated.');
        await loadDashboard();
      } else {
        setError(response?.message || 'Could not update description.');
      }
    } catch (requestError) {
      setError(requestError.message || 'Could not update description.');
    } finally {
      setIsSavingDescription(false);
    }
  }

  async function handleSaveVenue(event) {
    event.preventDefault();
    setIsSavingVenue(true);
    setError('');
    setSuccess('');

    const availability = editingSlots.length > 0
      ? editingSlots
      : [{
        dayOfWeek: venueForm.dayOfWeek,
        startTime: venueForm.startTime,
        endTime: venueForm.endTime,
      }];

    const payload = {
      venueName: venueForm.venueName,
      address: venueForm.address,
      priceOverride: venueForm.priceOverride ? Number(venueForm.priceOverride) : null,
      availability,
    };

    if (editingVenueId) {
      payload.id = editingVenueId;
    }

    try {
      const response = editingVenueId
        ? await apiClient.updateTrainerVenue(payload)
        : await apiClient.createTrainerVenue(payload);

      if (response?.success) {
        setSuccess(editingVenueId ? 'Venue updated.' : 'Venue created.');
        resetVenueForm();
        await loadDashboard();
      } else {
        setError(response?.message || 'Could not save venue.');
      }
    } catch (requestError) {
      setError(requestError.message || 'Could not save venue.');
    } finally {
      setIsSavingVenue(false);
    }
  }

  async function handleDeleteVenue(venueId) {
    setError('');
    setSuccess('');

    try {
      const response = await apiClient.deleteTrainerVenue(venueId);
      if (response?.success) {
        setSuccess('Venue deleted.');
        if (editingVenueId === venueId) {
          resetVenueForm();
        }
        await loadDashboard();
      } else {
        setError(response?.message || 'Could not delete venue.');
      }
    } catch (requestError) {
      setError(requestError.message || 'Could not delete venue.');
    }
  }

  async function handleBookingStatus(bookingId, status) {
    setUpdatingBookingId(bookingId);
    setError('');
    setSuccess('');

    try {
      const response = await apiClient.updateTrainerBookingStatus(bookingId, status);
      if (response?.success) {
        setSuccess(`Booking ${status.toLowerCase()}.`);
        await loadDashboard();
      } else {
        setError(response?.message || 'Could not update booking.');
      }
    } catch (requestError) {
      setError(requestError.message || 'Could not update booking.');
    } finally {
      setUpdatingBookingId(null);
    }
  }

  if (!auth?.userId || auth.role !== 'COACH') {
    return null;
  }

  return (
    <main className="app-shell coach-dashboard-page">
      <Navbar />

      <section className="coach-dashboard section">
        <div className="container">
          <div className="coach-dashboard__hero">
            <p className="eyebrow">Coach workspace</p>
            <h1>Dashboard</h1>
            <p>Manage your trainer profile, venues, availability, and incoming booking requests.</p>
          </div>

          {isLoading ? <p className="coach-dashboard__hint">Loading dashboard...</p> : null}
          {error ? <div className="notice notice--error">{error}</div> : null}
          {success ? <div className="notice notice--success">{success}</div> : null}

          {!isLoading ? (
            <div className="coach-dashboard__layout">
              <div className="coach-dashboard__columns">
                <section className="coach-panel coach-panel--profile">
                <div className="coach-panel__header">
                  <h2>Trainer profile</h2>
                  <span>{trainer?.sportType}</span>
                </div>

                <form className="coach-form coach-form--compact" onSubmit={handleSaveProfile}>
                  <div className="coach-profile__row">
                    <label className="field">
                      <span>First name</span>
                      <input
                        value={profileForm.firstName}
                        onChange={(event) => setProfileForm((current) => ({
                          ...current,
                          firstName: event.target.value,
                        }))}
                      />
                    </label>
                    <label className="field">
                      <span>Last name</span>
                      <input
                        value={profileForm.lastName}
                        onChange={(event) => setProfileForm((current) => ({
                          ...current,
                          lastName: event.target.value,
                        }))}
                      />
                    </label>
                  </div>

                  <div className="coach-form__grid coach-form__grid--tight">
                    <label className="field">
                      <span>Phone</span>
                      <input
                        value={profileForm.phone}
                        onChange={(event) => setProfileForm((current) => ({
                          ...current,
                          phone: event.target.value,
                        }))}
                      />
                    </label>
                    <label className="field">
                      <span>Sport type</span>
                      <input
                        value={profileForm.sportType}
                        onChange={(event) => setProfileForm((current) => ({
                          ...current,
                          sportType: event.target.value,
                        }))}
                      />
                    </label>
                    <label className="field coach-form__span-2">
                      <span>Base price per session</span>
                      <input
                        type="number"
                        min="1"
                        step="0.01"
                        value={profileForm.pricePerSession}
                        onChange={(event) => setProfileForm((current) => ({
                          ...current,
                          pricePerSession: event.target.value,
                        }))}
                      />
                    </label>
                  </div>

                  <Button type="submit" disabled={isSavingProfile}>
                    {isSavingProfile ? 'Saving...' : 'Save profile'}
                  </Button>
                </form>
                </section>

                <div className="coach-dashboard__right">
                  <section className="coach-panel coach-panel--bio">
                    <div className="coach-panel__header">
                      <h2>Bio</h2>
                      <span>Public</span>
                    </div>

                    <form className="coach-form coach-form--compact" onSubmit={handleSaveDescription}>
                      <label className="field">
                        <span>About me</span>
                        <textarea
                          rows={4}
                          value={description}
                          onChange={(event) => setDescription(event.target.value)}
                          placeholder="Short coach intro shown to players."
                        />
                      </label>
                      <Button type="submit" disabled={isSavingDescription}>
                        {isSavingDescription ? 'Saving...' : 'Save bio'}
                      </Button>
                    </form>
                  </section>

                  <section className="coach-panel coach-panel--slot">
                    <div className="coach-panel__header">
                      <h2>{editingVenueId ? 'Edit venue' : 'Add slot'}</h2>
                      <span>{venues.length} venues</span>
                    </div>

                    <form className="coach-form coach-form--compact" onSubmit={handleSaveVenue}>
                      <div className="coach-venue-editor">
                        <div className="coach-venue-editor__left">
                          <label className="field">
                            <span>Venue</span>
                            <input
                              required
                              value={venueForm.venueName}
                              onChange={(event) => setVenueForm((current) => ({
                                ...current,
                                venueName: event.target.value,
                              }))}
                            />
                          </label>
                          <label className="field">
                            <span>Address</span>
                            <input
                              value={venueForm.address}
                              onChange={(event) => setVenueForm((current) => ({
                                ...current,
                                address: event.target.value,
                              }))}
                            />
                          </label>

                          <div className="coach-venue-editor__summary">
                            <p className="coach-form__label">Slots to publish</p>
                            {editingSlots.length === 0 ? (
                              <p className="coach-venue-editor__hint">
                                Add at least one day/time slot. You can stack multiple slots before publishing.
                              </p>
                            ) : (
                              <div className="coach-slot-list coach-slot-list--compact">
                                {editingSlots.map((slot, index) => (
                                  <div className="coach-slot-list__item" key={`${slot.dayOfWeek}-${slot.startTime}-${index}`}>
                                    <span>{slot.dayOfWeek} · {slot.startTime} – {slot.endTime}</span>
                                    <button type="button" onClick={() => removeSlot(index)}>Remove</button>
                                  </div>
                                ))}
                              </div>
                            )}
                          </div>

                          <div className="coach-form__actions coach-form__actions--sticky">
                            <Button type="submit" disabled={isSavingVenue}>
                              {isSavingVenue ? 'Saving...' : editingVenueId ? 'Update venue' : 'Publish slot'}
                            </Button>
                            {editingVenueId ? (
                              <Button type="button" variant="outline" onClick={resetVenueForm}>
                                Cancel
                              </Button>
                            ) : null}
                          </div>
                        </div>

                        <div className="coach-venue-editor__right">
                          <label className="field">
                            <span>Price</span>
                            <input
                              type="number"
                              min="1"
                              step="0.01"
                              placeholder="Same as base"
                              value={venueForm.priceOverride}
                              onChange={(event) => setVenueForm((current) => ({
                                ...current,
                                priceOverride: event.target.value,
                              }))}
                            />
                          </label>

                          <div className="coach-form__slots">
                            <p className="coach-form__label">Day + time</p>
                            <div className="coach-form__grid coach-form__grid--tight">
                              <label className="field">
                                <span>Day</span>
                                <select
                                  value={venueForm.dayOfWeek}
                                  onChange={(event) => setVenueForm((current) => ({
                                    ...current,
                                    dayOfWeek: event.target.value,
                                  }))}
                                >
                                  {DAYS.map((day) => (
                                    <option key={day} value={day}>{day}</option>
                                  ))}
                                </select>
                              </label>
                              <label className="field">
                                <span>Start</span>
                                <input
                                  type="time"
                                  value={venueForm.startTime}
                                  onChange={(event) => setVenueForm((current) => ({
                                    ...current,
                                    startTime: event.target.value,
                                  }))}
                                />
                              </label>
                              <label className="field">
                                <span>End</span>
                                <input
                                  type="time"
                                  value={venueForm.endTime}
                                  onChange={(event) => setVenueForm((current) => ({
                                    ...current,
                                    endTime: event.target.value,
                                  }))}
                                />
                              </label>
                            </div>
                            <Button type="button" variant="outline" onClick={addSlotToForm}>
                              Add slot
                            </Button>
                          </div>
                        </div>
                      </div>

                      {venues.length > 0 ? (
                        <div className="coach-venue-mini">
                          <p className="coach-form__label">Your venues</p>
                          <div className="coach-venue-mini__list">
                            {venues.map((venue) => (
                              <div className="coach-venue-mini__item" key={venue.id}>
                                <div>
                                  <strong>{venue.venueName}</strong>
                                  <p className="coach-venue-mini__meta">{venue.address || 'No address'}</p>
                                </div>
                                <div className="coach-venue-mini__actions">
                                  <Button variant="outline" onClick={() => startEditVenue(venue)}>Edit</Button>
                                  <Button variant="ghost" onClick={() => handleDeleteVenue(venue.id)}>Delete</Button>
                                </div>
                              </div>
                            ))}
                          </div>
                        </div>
                      ) : null}
                    </form>
                  </section>
                </div>
              </div>

              <section className="coach-panel coach-panel--wide coach-panel--requests">
                <div className="coach-panel__header">
                  <h2>Incoming booking requests</h2>
                  <span>{bookings.length} total</span>
                </div>

                {bookings.length === 0 ? (
                  <div className="coach-empty">
                    <p>No booking requests yet.</p>
                  </div>
                ) : (
                  <div className="coach-requests">
                    {bookings.map((booking) => (
                      <article className="coach-request-card" key={booking.id}>
                        <div className="coach-request-card__main">
                          <div className="coach-request-card__top">
                            <p className={`coach-request-card__status is-${String(booking.status || 'PENDING').toLowerCase()}`}>
                              {booking.status || 'PENDING'}
                            </p>
                          <p className="coach-request-card__price">
                            ₾{Number.isFinite(Number(booking.sessionPrice)) ? Number(booking.sessionPrice) : 0}
                          </p>
                          </div>

                          <h3 className="coach-request-card__player">{booking.playerName}</h3>
                          <p className="coach-request-card__meta">
                            {booking.venueName}{booking.venueAddress ? ` · ${booking.venueAddress}` : ''}
                          </p>
                          <p className="coach-request-card__meta">
                            {formatDate(booking.requestedDate)}
                            {booking.requestedTimeSlot ? ` · ${booking.requestedTimeSlot}` : ''}
                          </p>
                        </div>

                        <div className="coach-request-card__actions">
                          {booking.status === 'PENDING' ? (
                            <>
                              <Button
                                disabled={updatingBookingId === booking.id}
                                onClick={() => handleBookingStatus(booking.id, 'CONFIRMED')}
                              >
                                Confirm
                              </Button>
                              <Button
                                variant="outline"
                                disabled={updatingBookingId === booking.id}
                                onClick={() => handleBookingStatus(booking.id, 'DECLINED')}
                              >
                                Decline
                              </Button>
                            </>
                          ) : (
                            <span className="coach-request-card__hint">
                              No actions
                            </span>
                          )}
                        </div>
                      </article>
                    ))}
                  </div>
                )}
              </section>
            </div>
          ) : null}
        </div>
      </section>
    </main>
  );
}
