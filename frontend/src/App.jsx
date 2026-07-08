import { useEffect, useState } from 'react';
import Coaches from './pages/Coaches.jsx';
import Community from './pages/Community.jsx';
import Courts from './pages/Courts.jsx';
import Home from './pages/Home.jsx';
import Login from './pages/Login.jsx';
import Matches from './pages/Matches.jsx';
import Profile from './pages/Profile.jsx';
import SignUp from './pages/SignUp.jsx';
import Tournaments from './pages/Tournaments.jsx';

const routedPages = new Set(['login', 'signup', 'dashboard', 'matches', 'tournaments', 'coaches', 'community', 'profile']);

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
  if (page === 'signup') return <SignUp />;
  if (page === 'dashboard') return <Courts />;
  if (page === 'matches') return <Matches />;
  if (page === 'tournaments') return <Tournaments />;
  if (page === 'coaches') return <Coaches />;
  if (page === 'community') return <Community />;
  if (page === 'profile') return <Profile />;

  return <Home />;
}
