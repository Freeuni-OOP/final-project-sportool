import { useEffect, useState } from 'react';
import { clearAuthSession, getStoredAuth } from '../api/client.js';
import Button from './Button.jsx';

const navLinks = [
  { label: 'Home', href: '#home' },
  { label: 'Courts', href: '#dashboard' },
  { label: 'Tournaments', href: '#tournaments' },
  { label: 'Coaches', href: '#coaches' },
  { label: 'Community', href: '#community' },
];

export default function Navbar() {
  const [authSession, setAuthSession] = useState(() => getStoredAuth());

  useEffect(() => {
    function syncAuthSession() {
      setAuthSession(getStoredAuth());
    }

    window.addEventListener('storage', syncAuthSession);
    window.addEventListener('sportool:auth-changed', syncAuthSession);

    return () => {
      window.removeEventListener('storage', syncAuthSession);
      window.removeEventListener('sportool:auth-changed', syncAuthSession);
    };
  }, []);

  function handleSignOut() {
    clearAuthSession();
    setAuthSession(null);
    window.dispatchEvent(new Event('sportool:auth-changed'));
    window.location.hash = 'home';
  }

  return (
    <header className="site-header">
      <nav className="navbar container" aria-label="Main navigation">
        <a className="brand" href="#home" aria-label="SporTool home">
          <span className="brand__mark">S</span>
          <span>SporTool</span>
        </a>

        <div className="navbar__links">
          {navLinks.map((link) => (
            <a key={link.label} href={link.href}>
              {link.label}
            </a>
          ))}
        </div>

        {authSession ? (
          <Button variant="outline" className="navbar__cta" onClick={handleSignOut}>
            Sign Out
          </Button>
        ) : (
          <Button href="#login" variant="outline" className="navbar__cta">
            Sign In
          </Button>
        )}
      </nav>
    </header>
  );
}