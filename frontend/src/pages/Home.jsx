import Button from '../components/Button.jsx';
import FeatureCard from '../components/FeatureCard.jsx';
import HeroVisual from '../components/HeroVisual.jsx';
import Navbar from '../components/Navbar.jsx';
import StatCard from '../components/StatCard.jsx';
import Ticker from '../components/Ticker.jsx';

const stats = [
  { value: '300+', label: 'Active Members' },
  { value: '10+', label: 'Premium Courts' },
  { value: '25+', label: 'Weekly Events' },
  { value: '96%', label: 'Match Satisfaction' },
];

const features = [
  {
    eyebrow: 'Court discovery',
    title: 'Find the perfect surface',
    description:
      'Browse clay, grass, and hard courts with real-time availability, lighting details, and elite venue profiles.',
    meta: 'From $18 / hour',
    variant: 'lime',
  },
  {
    eyebrow: 'Tournament hub',
    title: 'Enter high-voltage ladders',
    description:
      'Join curated weekend tournaments, club ladders, and social match nights built for every competitive level.',
    meta: 'Next draw opens Friday',
    variant: 'blue',
  },
  {
    eyebrow: 'Performance coaching',
    title: 'Train with verified pros',
    description:
      'Book focused sessions with expert coaches for serve mechanics, footwork, match IQ, and recovery planning.',
    meta: 'Private and group sessions',
    variant: 'violet',
  },
];

export default function Home() {
  return (
    <main className="app-shell" id="home">
      <Navbar />

      <section className="hero section">
        <div className="container hero__grid">
          <div className="hero__content">
            <p className="eyebrow">Premium sports booking platform</p>
            <h1>
              Own the court.
              <span> Play after dark.</span>
            </h1>
            <p className="hero__description">
              SporTool brings cinematic court discovery, elite tennis events,
              and fast booking flows into one high-energy club experience.
            </p>
            <div className="hero__actions">
              <Button href="#courts">Join Now</Button>
              <Button href="#tournaments" variant="ghost">
                Explore Events
              </Button>
            </div>
          </div>

          <HeroVisual />
        </div>
      </section>

      <Ticker />

      <section className="section section--tight">
        <div className="container stats-grid">
          {stats.map((stat) => (
            <StatCard key={stat.label} {...stat} />
          ))}
        </div>
      </section>

      <section className="section" id="courts">
        <div className="container">
          <div className="section-heading">
            <p className="eyebrow">What is inside</p>
            <h2>Book, compete, and level up from one electric dashboard.</h2>
            <p>
              A modular frontend foundation for court booking, tournament
              discovery, and future authentication flows.
            </p>
          </div>

          <div className="feature-grid">
            {features.map((feature) => (
              <FeatureCard key={feature.title} {...feature} />
            ))}
          </div>
        </div>
      </section>
    </main>
  );
}