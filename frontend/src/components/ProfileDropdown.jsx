import { useEffect, useRef, useState } from 'react';

export default function ProfileDropdown({ session }) {
  const [isOpen, setIsOpen] = useState(false);
  const dropdownRef = useRef(null);

  useEffect(() => {
    function handleClickOutside(event) {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setIsOpen(false);
      }
    }

    function handleEscape(event) {
      if (event.key === 'Escape') {
        setIsOpen(false);
      }
    }

    document.addEventListener('mousedown', handleClickOutside);
    document.addEventListener('keydown', handleEscape);

    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
      document.removeEventListener('keydown', handleEscape);
    };
  }, []);

  const displayName = session?.fullName || 'Member';
  const roleLabel = session?.role || 'PLAYER';

  return (
    <div className="profile-dropdown" ref={dropdownRef}>
      <button
        aria-expanded={isOpen}
        aria-haspopup="menu"
        className="profile-dropdown__trigger"
        onClick={() => setIsOpen((open) => !open)}
        type="button"
      >
        <span className="profile-dropdown__avatar" aria-hidden="true">
          {displayName.charAt(0).toUpperCase()}
        </span>
        <span className="profile-dropdown__label">Profile</span>
        <span className="profile-dropdown__chevron" aria-hidden="true">
          ▾
        </span>
      </button>

      {isOpen ? (
        <div className="profile-dropdown__menu" role="menu">
          <div className="profile-dropdown__header">
            <strong>{displayName}</strong>
            <span>{roleLabel}</span>
          </div>
          <a className="profile-dropdown__item" href="#home" role="menuitem">
            Home
          </a>
          <a className="profile-dropdown__item" href="#dashboard" role="menuitem">
            My Courts
          </a>
          <a className="profile-dropdown__item" href="#tournaments" role="menuitem">
            Matches
          </a>
        </div>
      ) : null}
    </div>
  );
}
