import { useEffect, useState } from 'react';
import Coaches from './pages/Coaches.jsx';
import Community from './pages/Community.jsx';
import Dashboard from './pages/Dashboard.jsx';
import Home from './pages/Home.jsx';
import Login from './pages/Login.jsx';
import Tournaments from './pages/Tournaments.jsx';

const routedPages = new Set(['login', 'dashboard', 'tournaments', 'coaches', 'community']);

function getCurrentPage() {
  const page = window.location.hash.replace('#', '').split('?')[0];
  return routedPages.has(page) ? page : 'home';
}

export default function App() {
  const [page, setPage] = useState(getCurrentPage);

  useEffect(() => {
    function handleHashChange() {
      setPage(getCurrentPage());
    }

    window.addEventListener('hashchange', handleHashChange);
    return () => window.removeEventListener('hashchange', handleHashChange);
  }, []);

  if (page === 'login') return <Login />;
  if (page === 'dashboard') return <Dashboard />;
  if (page === 'tournaments') return <Tournaments />;
  if (page === 'coaches') return <Coaches />;
  if (page === 'community') return <Community />;

  return <Home />;
}