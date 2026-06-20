export default function FeatureCard({ eyebrow, title, description, meta, variant = 'default' }) {
  return (
    <article className={`feature-card feature-card--${variant}`}>
      <div className="feature-card__visual" aria-hidden="true">
        <span />
      </div>
      <div className="feature-card__content">
        <p className="eyebrow">{eyebrow}</p>
        <h3>{title}</h3>
        <p>{description}</p>
        <span className="feature-card__meta">{meta}</span>
      </div>
    </article>
  );
}