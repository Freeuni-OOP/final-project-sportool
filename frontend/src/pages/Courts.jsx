import { useMemo, useState } from 'react';
import BookingModal from '../components/BookingModal.jsx';
import Navbar from '../components/Navbar.jsx';
import VenueCard from '../components/VenueCard.jsx';
import { tbilisiVenues, venueCategories } from '../data/venues.js';

export default function Courts() {
  const [activeCategory, setActiveCategory] = useState('All');
  const [selectedVenue, setSelectedVenue] = useState(null);

  const filteredVenues = useMemo(() => {
    if (activeCategory === 'All') {
      return tbilisiVenues;
    }

    return tbilisiVenues.filter((venue) => venue.category === activeCategory);
  }, [activeCategory]);

  return (
    <main className="app-shell courts-page">
      <Navbar />

      <section className="courts section">
        <div className="container">
          <div className="courts__header">
            <p className="eyebrow">Tbilisi venues</p>
            <h1>Find your court. Book your match.</h1>
            <p>
              Browse premium sports facilities across Tbilisi and lock your next
              session in seconds.
            </p>
          </div>

          <div className="courts__filters" role="tablist" aria-label="Sport categories">
            {venueCategories.map((category) => (
              <button
                aria-selected={activeCategory === category}
                className={`courts__filter${activeCategory === category ? ' is-active' : ''}`}
                key={category}
                onClick={() => setActiveCategory(category)}
                role="tab"
                type="button"
              >
                {category}
              </button>
            ))}
          </div>

          <div className="courts__grid">
            {filteredVenues.map((venue) => (
              <VenueCard
                key={venue.id}
                onBook={setSelectedVenue}
                venue={venue}
              />
            ))}
          </div>
        </div>
      </section>

      {selectedVenue ? (
        <BookingModal onClose={() => setSelectedVenue(null)} venue={selectedVenue} />
      ) : null}
    </main>
  );
}
