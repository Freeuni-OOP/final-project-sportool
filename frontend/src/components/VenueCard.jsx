import Button from './Button.jsx';

const sportLabels = {
  Padel: 'Padel',
  Football: 'Football',
  Tennis: 'Tennis',
  Basketball: 'Basketball',
};

export default function VenueCard({ venue, onBook }) {
  const category = sportLabels[venue.category] || venue.category;

  return (
    <article className="venue-card">
      <div className={`venue-card__media venue-card__media--${venue.imageVariant}`}>
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
