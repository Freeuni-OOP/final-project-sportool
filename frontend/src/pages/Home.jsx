import HeroVisual from '../components/HeroVisual.jsx';
import Navbar from '../components/Navbar.jsx';
import Ticker from '../components/Ticker.jsx';

export default function Home() {
  return (
    <main className="app-shell home-page" id="home">
      <Navbar />

      <section className="hero section home-hero">
        <div className="container hero__grid">
          <div className="hero__content">
            <p className="eyebrow">Player matchmaking platform</p>
            <h1 className="hero__title">
              <span className="hero__title-line">Own</span>
              <span className="hero__title-line">The</span>
              <span className="hero__title-line">Court</span>
              <span className="hero__title-line hero__title-line--outline">Play</span>
              <span className="hero__title-line hero__title-line--outline">After</span>
              <span className="hero__title-line hero__title-line--outline">Dark</span>
            </h1>
            <p className="hero__description">
              SporTool gives players seamless matchmaking — find rivals at your
              level, lock court time, and get from queue to first serve without
              the friction.
            </p>
          </div>

          <div className="hero__visual-wrap">
            <HeroVisual />
          </div>
        </div>
      </section>

      <Ticker />
    </main>
  );
}
