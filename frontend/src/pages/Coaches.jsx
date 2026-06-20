import Button from '../components/Button.jsx';
import Navbar from '../components/Navbar.jsx';

export default function Coaches() {
  return (
    <main className="app-shell">
      <Navbar />

      <section className="info-page section">
        <div className="container info-page__grid">
          <div>
            <p className="eyebrow">Verified performance staff</p>
            <h1>Coaches</h1>
            <p>
              Find verified tennis coaches for private lessons, group sessions,
              footwork drills, serve mechanics, and match strategy.
            </p>

            <Button href="#dashboard">Book Training Court</Button>
          </div>

          <div className="info-panel">
            <span>Featured program</span>
            <strong>Serve mechanics</strong>
            <p>
              Improve your serve power, placement, rhythm, and consistency with
              elite SporTool coaching sessions.
            </p>
          </div>
        </div>
      </section>
    </main>
  );
}