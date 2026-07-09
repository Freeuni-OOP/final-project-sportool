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
            <div className="coach-dashboard__grid">
              <section className="coach-panel">
                <div className="coach-panel__header">
                  <h2>Trainer profile</h2>
                  <span>{trainer?.sportType}</span>
                </div>

                <form className="coach-form" onSubmit={handleSaveProfile}>
                  <div className="coach-form__grid">
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
                    <label className="field">
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

              <section className="coach-panel">
                <div className="coach-panel__header">
                  <h2>Coach description</h2>
                  <span>Public</span>
                </div>

                <form className="coach-form" onSubmit={handleSaveDescription}>
                  <label className="field">
                    <span>Bio</span>
                    <textarea
                      rows={6}
                      value={description}
                      onChange={(event) => setDescription(event.target.value)}
                      placeholder="Tell players what you focus on, your experience, certifications, and what sessions look like."
                    />
                  </label>
                  <Button type="submit" disabled={isSavingDescription}>
                    {isSavingDescription ? 'Saving...' : 'Save description'}
                  </Button>
                </form>
              </section>

              <section className="coach-panel coach-panel--wide">
                <div className="coach-panel__header">
                  <h2>{editingVenueId ? 'Edit venue' : 'Add venue'}</h2>
                  <span>{venues.length} venues</span>
                </div>

                <form className="coach-form" onSubmit={handleSaveVenue}>
                  <label className="field">
                    <span>Venue name</span>
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
                  <label className="field">
                    <span>Price override (optional)</span>
                    <input
                      type="number"
                      min="1"
                      step="0.01"
                      placeholder="Same as base price"
                      value={venueForm.priceOverride}
                      onChange={(event) => setVenueForm((current) => ({
                        ...current,
                        priceOverride: event.target.value,
                      }))}
                    />
                  </label>

                  <div className="coach-form__slots">
                    <p className="coach-form__label">Availability slots</p>
                    <div className="coach-form__grid">
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

                  {editingSlots.length > 0 ? (
                    <div className="coach-slot-list">
                      {editingSlots.map((slot, index) => (
                        <div className="coach-slot-list__item" key={`${slot.dayOfWeek}-${slot.startTime}-${index}`}>
                          <span>{slot.dayOfWeek} · {slot.startTime} – {slot.endTime}</span>
                          <button type="button" onClick={() => removeSlot(index)}>Remove</button>
                        </div>
                      ))}
                    </div>
                  ) : null}

                  <div className="coach-form__actions">
                    <Button type="submit" disabled={isSavingVenue}>
                      {isSavingVenue ? 'Saving...' : editingVenueId ? 'Update venue' : 'Create venue'}
                    </Button>
                    {editingVenueId ? (
                      <Button type="button" variant="outline" onClick={resetVenueForm}>
                        Cancel edit
                      </Button>
                    ) : null}
                  </div>
                </form>
              </section>

              <section className="coach-panel coach-panel--wide">
                <div className="coach-panel__header">
                  <h2>Your venues</h2>
                </div>

                {venues.length === 0 ? (
                  <div className="coach-empty">
                    <p>No venues published yet.</p>
                  </div>
                ) : (
                  <div className="coach-venue-list">
                    {venues.map((venue) => (
                      <article className="coach-venue-card" key={venue.id}>
                        <div>
                          <h3>{venue.venueName}</h3>
                          <p>{venue.address || 'No address provided'}</p>
                          <p className="coach-venue-card__price">
                            {venue.priceOverride != null
                              ? `₾${venue.priceOverride}`
                              : `Same as base price (₾${trainer?.pricePerSession || 0})`}
                          </p>
                          <div className="coach-slot-list">
                            {(venue.availability || []).map((slot, index) => (
                              <span className="coach-slot-pill" key={`${venue.id}-${index}`}>
                                {slot.dayOfWeek} {slot.startTime}-{slot.endTime}
                              </span>
                            ))}
                          </div>
                        </div>
                        <div className="coach-venue-card__actions">
                          <Button variant="outline" onClick={() => startEditVenue(venue)}>
                            Edit
                          </Button>
                          <Button variant="ghost" onClick={() => handleDeleteVenue(venue.id)}>
                            Delete
                          </Button>
                        </div>
                      </article>
                    ))}
                  </div>
                )}
              </section>

              <section className="coach-panel coach-panel--wide">
                <div className="coach-panel__header">
                  <h2>Incoming booking requests</h2>
                  <span>{bookings.length} total</span>
                </div>

                {bookings.length === 0 ? (
                  <div className="coach-empty">
                    <p>No booking requests yet.</p>
                  </div>
                ) : (
                  <div className="coach-booking-list">
                    {bookings.map((booking) => (
                      <article className="coach-booking-card" key={booking.id}>
                        <div>
                          <p className="coach-booking-card__eyebrow">{booking.status}</p>
                          <h3>{booking.playerName}</h3>
                          <p>{booking.venueName} · {booking.venueAddress}</p>
                          <p>
                            {formatDate(booking.requestedDate)}
                            {booking.requestedTimeSlot ? ` · ${booking.requestedTimeSlot}` : ''}
                          </p>
                        </div>
                        {booking.status === 'PENDING' ? (
                          <div className="coach-booking-card__actions">
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
                          </div>
                        ) : null}
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
