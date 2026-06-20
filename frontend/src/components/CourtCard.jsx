import Button from './Button.jsx';

function formatPrice(value) {
  if (value === undefined || value === null || value === '') {
    return 'Price on request';
  }

  const numericPrice = Number(value);

  if (Number.isNaN(numericPrice)) {
    return value;
  }

  return `$${numericPrice.toFixed(0)} / hour`;
}

export default function CourtCard({ court, onBook }) {
  const name = court.name || court.courtName || court.title || `Court #${court.id || ''}`;
  const surface = court.surface || court.type || court.courtType || court.sportType || 'Tennis';
  const price = court.pricePerHour || court.hourlyRate || court.price || court.cost;
  const location = court.location || court.address || court.clubName || 'SporTool Arena';
  const isAvailable = court.available ?? court.isAvailable ?? court.status !== 'BOOKED';
  const imageUrl = court.imageUrl || court.image || court.photoUrl;

  return (
    <article className="court-card">
      <div className="court-card__media">
        {imageUrl ? <img src={imageUrl} alt={`${name} court`} /> : <span aria-hidden="true" />}

        <div className={`court-card__status ${isAvailable ? 'is-open' : 'is-closed'}`}>
          {isAvailable ? 'Available' : 'Booked'}
        </div>
      </div>

      <div className="court-card__body">
        <div>
          <p className="eyebrow">{surface}</p>
          <h3>{name}</h3>
          <p>{location}</p>
        </div>

        <div className="court-card__footer">
          <strong>{formatPrice(price)}</strong>

          <Button
            className="court-card__book"
            disabled={!isAvailable}
            onClick={() => onBook?.(court)}
          >
            Book Now
          </Button>
        </div>
      </div>
    </article>
  );
}