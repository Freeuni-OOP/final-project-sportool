import Button from './Button.jsx';

const sportLabels = {
  Padel: 'Padel',
  Football: 'Football',
  Tennis: 'Tennis',
  Basketball: 'Basketball',
};

export default function VenueCard({ venue, onBook }) {
  const category = sportLabels[venue.category] || venue.category;
  const mediaClassName = venue.imageUrl
    ? 'venue-card__media venue-card__media--photo'
    : `venue-card__media venue-card__media--${venue.imageVariant}`;

  return (
    <article className="venue-card">
      <div className={mediaClassName}>
        {venue.imageUrl ? (
          <img alt={`${venue.name} court`} className="venue-card__image" src={venue.imageUrl} />
        ) : null}
        <span className="venue-card__badge">{category}</span>
      </div>

      <div className="venue-card__body">
        <div>
          <p className="eyebrow">Tbilisi</p>
          <h3>{venue.name}</h3>
          <p className="venue-card__location">{venue.location}</p>
        </div>

        <div className="venue-card__footer">
          <strong>₾{venue.pricePerHour} / hour</strong>
          <Button className="venue-card__book" onClick={() => onBook?.(venue)}>
            Book Now
          </Button>
        </div>
      </div>
    </article>
  );
}
