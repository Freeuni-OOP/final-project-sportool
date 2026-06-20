import Button from '../components/Button.jsx';
import Navbar from '../components/Navbar.jsx';

export default function Community() {
  return (
    <main className="app-shell">
      <Navbar />

      <section className="info-page section">
        <div className="container info-page__grid">
          <div>
            <p className="eyebrow">Club energy</p>
            <h1>Community</h1>
            <p>
              Join local players, find hitting partners, enter open matches,
              and stay connected with SporTool club events.
            </p>

            <Button href="#login">Join SporTool</Button>
          </div>

          <div className="info-panel">
            <span>Members online</span>
            <strong>300+</strong>
            <p>
              Connect with players near you and build your next tennis session
              around real availability.
            </p>
          </div>
        </div>
      </section>
    </main>
  );
}