export default function HeroVisual() {
  return (
    <div className="hero-visual" aria-label="Night court preview">
      <div className="hero-visual__sky">
        <span className="hero-visual__moon" />
        <span className="hero-visual__beam hero-visual__beam--left" />
        <span className="hero-visual__beam hero-visual__beam--right" />
      </div>

      <div className="hero-visual__court">
        <span className="hero-visual__line hero-visual__line--center" />
        <span className="hero-visual__line hero-visual__line--service" />
        <span className="hero-visual__line hero-visual__line--baseline" />
        <span className="hero-visual__net" />
        <span className="hero-visual__ball" />
        <span className="hero-visual__racket" />
      </div>

      <div className="hero-visual__glow" />
    </div>
  );
}
