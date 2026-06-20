export default function HeroVisual() {
  return (
    <div className="hero-visual" aria-label="Cinematic tennis court preview">
      <div className="hero-visual__court">
        <span className="hero-visual__line hero-visual__line--center" />
        <span className="hero-visual__line hero-visual__line--service" />
        <span className="hero-visual__line hero-visual__line--baseline" />
        <span className="hero-visual__ball" />
        <span className="hero-visual__player hero-visual__player--one" />
        <span className="hero-visual__player hero-visual__player--two" />
      </div>
      <div className="hero-visual__badge">
        <span>Live slots</span>
        <strong>42</strong>
      </div>
    </div>
  );
}