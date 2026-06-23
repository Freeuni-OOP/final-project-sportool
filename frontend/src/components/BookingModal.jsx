import { useCallback, useEffect, useMemo, useState } from 'react';
import { apiClient, getStoredAuth } from '../api/client.js';
import Button from './Button.jsx';

const TIME_SLOTS = [
  '09:00', '10:00', '11:00', '12:00', '13:00', '14:00',
  '15:00', '16:00', '17:00', '18:00', '19:00', '20:00', '21:00',
];

function formatDisplayDate(dateString) {
  if (!dateString) return '';

  return new Date(`${dateString}T12:00:00`).toLocaleDateString('en-GB', {
    weekday: 'long',
    day: 'numeric',
    month: 'long',
    year: 'numeric',
  });
}

function addHour(time) {
  const [hours, minutes] = time.split(':').map(Number);
  const nextHour = hours + 1;
  return `${String(nextHour).padStart(2, '0')}:${String(minutes).padStart(2, '0')}`;
}

function buildMonthDays(year, month) {
  const firstDay = new Date(year, month, 1);
  const lastDay = new Date(year, month + 1, 0);
  const startOffset = (firstDay.getDay() + 6) % 7;
  const days = [];

  for (let index = 0; index < startOffset; index += 1) {
    days.push(null);
  }

  for (let day = 1; day <= lastDay.getDate(); day += 1) {
    days.push(new Date(year, month, day));
  }

  return days;
}

function isSameDay(left, right) {
  return (
    left.getFullYear() === right.getFullYear()
    && left.getMonth() === right.getMonth()
    && left.getDate() === right.getDate()
  );
}

function toDateKey(date) {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
}

function normalizeSlotTime(time) {
  if (!time) return '';

  const [hours, minutes] = time.split(':');
  return `${String(Number(hours)).padStart(2, '0')}:${String(Number(minutes || 0)).padStart(2, '0')}`;
}

function extractSlotTime(startTime) {
  if (!startTime) return '';

  if (Array.isArray(startTime)) {
    const hour = startTime[3] ?? 0;
    const minute = startTime[4] ?? 0;
    return normalizeSlotTime(`${hour}:${minute}`);
  }

  if (typeof startTime === 'object') {
    if (typeof startTime.hour === 'number') {
      return normalizeSlotTime(`${startTime.hour}:${startTime.minute ?? 0}`);
    }
  }

  if (typeof startTime === 'string') {
    const timePart = startTime.includes('T') ? startTime.split('T')[1] : startTime;
    return normalizeSlotTime(timePart.slice(0, 5));
  }

  return '';
}

function mapBookingsToSlots(bookings) {
  return (Array.isArray(bookings) ? bookings : [])
    .map((booking) => extractSlotTime(booking.startTime))
    .filter(Boolean);
}

export default function BookingModal({ venue, onClose }) {
  const today = useMemo(() => {
    const now = new Date();
    now.setHours(0, 0, 0, 0);
    return now;
  }, []);

  const [step, setStep] = useState(1);
  const [viewDate, setViewDate] = useState(() => new Date(today.getFullYear(), today.getMonth(), 1));
  const [selectedDate, setSelectedDate] = useState(null);
  const [selectedSlot, setSelectedSlot] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [isSuccess, setIsSuccess] = useState(false);
  const [bookedSlots, setBookedSlots] = useState([]);
  const [isLoadingSlots, setIsLoadingSlots] = useState(false);
  const [slotsLoaded, setSlotsLoaded] = useState(false);

  const selectedDateKey = selectedDate ? toDateKey(selectedDate) : '';
  const monthDays = buildMonthDays(viewDate.getFullYear(), viewDate.getMonth());
  const monthLabel = viewDate.toLocaleDateString('en-GB', { month: 'long', year: 'numeric' });

  const unavailableSlots = useMemo(() => new Set(bookedSlots), [bookedSlots]);
  const allSlotsAvailable = slotsLoaded && !isLoadingSlots && bookedSlots.length === 0;

  const isSelectedSlotAvailable = Boolean(
    selectedSlot && slotsLoaded && !unavailableSlots.has(selectedSlot),
  );

  const loadBookedSlots = useCallback(async () => {
    if (!venue || !selectedDateKey) {
      setBookedSlots([]);
      setSlotsLoaded(false);
      return;
    }

    setIsLoadingSlots(true);
    setSlotsLoaded(false);

    try {
      const bookings = await apiClient.getCourtBookings(venue.courtId, selectedDateKey);
      setBookedSlots(mapBookingsToSlots(bookings));
    } catch {
      setBookedSlots([]);
      setError('Could not load availability. Please try again.');
    } finally {
      setIsLoadingSlots(false);
      setSlotsLoaded(true);
    }
  }, [venue, selectedDateKey]);

  useEffect(() => {
    const previousOverflow = document.body.style.overflow;
    document.body.style.overflow = 'hidden';

    return () => {
      document.body.style.overflow = previousOverflow;
    };
  }, []);

  useEffect(() => {
    if (!venue || !selectedDateKey || step < 2) {
      return undefined;
    }

    loadBookedSlots();
  }, [venue, selectedDateKey, step, loadBookedSlots]);

  useEffect(() => {
    if (!selectedSlot || !unavailableSlots.has(selectedSlot)) {
      return;
    }

    setSelectedSlot(null);

    if (step === 3) {
      setError('This time slot is no longer available. Please choose another.');
      setStep(2);
    }
  }, [unavailableSlots, selectedSlot, step]);

  function handlePreviousMonth() {
    setViewDate(new Date(viewDate.getFullYear(), viewDate.getMonth() - 1, 1));
  }

  function handleNextMonth() {
    setViewDate(new Date(viewDate.getFullYear(), viewDate.getMonth() + 1, 1));
  }

  function handleDateSelect(day) {
    if (!day || day < today) return;
    setSelectedDate(day);
    setSelectedSlot(null);
    setBookedSlots([]);
    setSlotsLoaded(false);
    setStep(2);
    setError('');
  }

  function handleSlotSelect(slot) {
    if (!slotsLoaded || isLoadingSlots || unavailableSlots.has(slot)) {
      return;
    }

    setSelectedSlot(slot);
    setError('');
    setStep(3);
  }

  async function handleConfirmBooking() {
    if (!selectedDate || !selectedSlot || !venue || !isSelectedSlotAvailable) return;

    const auth = getStoredAuth();
    if (!auth?.userId) {
      setError('Please sign in before booking a court.');
      return;
    }

    setIsSubmitting(true);
    setError('');

    const endSlot = addHour(selectedSlot);
    const payload = {
      userId: auth.userId,
      courtId: venue.courtId,
      startTime: `${selectedDateKey}T${selectedSlot}:00`,
      endTime: `${selectedDateKey}T${endSlot}:00`,
      totalPrice: venue.pricePerHour,
      status: 'CONFIRMED',
    };

    try {
      await apiClient.createBooking(payload);
      await loadBookedSlots();
      setIsSuccess(true);
    } catch (requestError) {
      setError(requestError.message);
      await loadBookedSlots();
    } finally {
      setIsSubmitting(false);
    }
  }

  function handleBookAnother() {
    setIsSuccess(false);
    setSelectedSlot(null);
    setStep(2);
    setError('');
    loadBookedSlots();
  }

  if (!venue) return null;

  return (
    <div className="booking-modal" role="presentation">
      <button
        aria-label="Close booking modal"
        className="booking-modal__backdrop"
        onClick={onClose}
        type="button"
      />

      <div
        aria-labelledby="booking-modal-title"
        aria-modal="true"
        className="booking-modal__panel"
        role="dialog"
      >
        <header className="booking-modal__header">
          <div>
            <p className="eyebrow">Book a court</p>
            <h2 id="booking-modal-title">{venue.name}</h2>
            <p className="booking-modal__location">{venue.location} · {venue.category}</p>
          </div>
          <button className="booking-modal__close" onClick={onClose} type="button">
            ×
          </button>
        </header>

        {!isSuccess ? (
          <>
            <div className="booking-modal__steps" aria-label="Booking progress">
              <span className={step >= 1 ? 'is-active' : ''}>1. Date</span>
              <span className={step >= 2 ? 'is-active' : ''}>2. Time</span>
              <span className={step >= 3 ? 'is-active' : ''}>3. Confirm</span>
            </div>

            {step === 1 ? (
              <section className="booking-step">
                <div className="booking-calendar__toolbar">
                  <button onClick={handlePreviousMonth} type="button">‹</button>
                  <strong>{monthLabel}</strong>
                  <button onClick={handleNextMonth} type="button">›</button>
                </div>

                <div className="booking-calendar__weekdays">
                  {['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'].map((day) => (
                    <span key={day}>{day}</span>
                  ))}
                </div>

                <div className="booking-calendar__grid">
                  {monthDays.map((day, index) => {
                    if (!day) {
                      return <span className="booking-calendar__day is-empty" key={`empty-${index}`} />;
                    }

                    const isDisabled = day < today;
                    const isSelected = selectedDate && isSameDay(day, selectedDate);

                    return (
                      <button
                        className={`booking-calendar__day${isDisabled ? ' is-disabled' : ''}${isSelected ? ' is-selected' : ''}`}
                        disabled={isDisabled}
                        key={toDateKey(day)}
                        onClick={() => handleDateSelect(day)}
                        type="button"
                      >
                        {day.getDate()}
                      </button>
                    );
                  })}
                </div>
              </section>
            ) : null}

            {step === 2 ? (
              <section className="booking-step">
                <p className="booking-step__label">{formatDisplayDate(selectedDateKey)}</p>
                {isLoadingSlots ? (
                  <p className="booking-step__hint">Loading availability...</p>
                ) : null}
                {allSlotsAvailable ? (
                  <p className="booking-step__empty">All time slots are available for this date.</p>
                ) : null}
                <div className="booking-slots" aria-busy={isLoadingSlots}>
                  {TIME_SLOTS.map((slot) => {
                    const isUnavailable = unavailableSlots.has(slot);
                    const isSelected = selectedSlot === slot && !isUnavailable;
                    const endSlot = addHour(slot);

                    if (isUnavailable) {
                      return (
                        <div
                          aria-disabled="true"
                          className="booking-slot is-unavailable"
                          key={slot}
                        >
                          <span className="booking-slot__time">{slot} – {endSlot}</span>
                          <small className="booking-slot__status">(Unavailable)</small>
                        </div>
                      );
                    }

                    return (
                      <button
                        className={`booking-slot is-available${isSelected ? ' is-selected' : ''}`}
                        disabled={!slotsLoaded || isLoadingSlots}
                        key={slot}
                        onClick={() => handleSlotSelect(slot)}
                        type="button"
                      >
                        <span className="booking-slot__time">{slot} – {endSlot}</span>
                        <small className="booking-slot__status">Available</small>
                      </button>
                    );
                  })}
                </div>
                <Button variant="ghost" onClick={() => setStep(1)}>Back to calendar</Button>
              </section>
            ) : null}

            {step === 3 ? (
              <section className="booking-step">
                <div className="booking-summary">
                  <div>
                    <span>Venue</span>
                    <strong>{venue.name}</strong>
                  </div>
                  <div>
                    <span>Location</span>
                    <strong>{venue.location}</strong>
                  </div>
                  <div>
                    <span>Date</span>
                    <strong>{formatDisplayDate(selectedDateKey)}</strong>
                  </div>
                  <div>
                    <span>Time</span>
                    <strong>{selectedSlot} – {addHour(selectedSlot)}</strong>
                  </div>
                  <div>
                    <span>Total</span>
                    <strong>₾{venue.pricePerHour}</strong>
                  </div>
                </div>

                {error ? <div className="notice notice--error">{error}</div> : null}

                <div className="booking-step__actions">
                  <Button variant="ghost" onClick={() => setStep(2)}>Back</Button>
                  <Button
                    disabled={!isSelectedSlotAvailable || isSubmitting || isLoadingSlots}
                    onClick={handleConfirmBooking}
                  >
                    {isSubmitting ? 'Confirming...' : 'Confirm Booking'}
                  </Button>
                </div>
              </section>
            ) : null}
          </>
        ) : (
          <section className="booking-success">
            <div className="booking-success__icon" aria-hidden="true">✓</div>
            <h3>Booking confirmed</h3>
            <p>
              Your slot at <strong>{venue.name}</strong> is locked for{' '}
              <strong>{formatDisplayDate(selectedDateKey)}</strong> at{' '}
              <strong>{selectedSlot} – {addHour(selectedSlot)}</strong>.
            </p>
            <div className="booking-success__actions">
              <Button variant="ghost" onClick={handleBookAnother}>Book another slot</Button>
              <Button onClick={onClose}>Done</Button>
            </div>
          </section>
        )}
      </div>
    </div>
  );
}
