import Navbar from '../components/Navbar.jsx';

export default function Tournaments() {
  return (
    <main className="app-shell">
      <Navbar />
      <section className="section container">
        <p className="eyebrow">Competitive calendar</p>
        <h1>Tournaments</h1>
        <p>Upcoming SporTool tournaments will appear here.</p>
      </section>
    </main>
  );
}