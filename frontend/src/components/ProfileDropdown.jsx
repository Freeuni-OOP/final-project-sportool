export default function ProfileDropdown({ session }) {
  const displayName = session?.fullName || 'Member';
  const initial = displayName.charAt(0).toUpperCase();

  return (
    <a className="profile-dropdown__trigger" href="#profile">
      <span className="profile-dropdown__avatar" aria-hidden="true">
        {initial}
      </span>
      <span className="profile-dropdown__label">Profile</span>
    </a>
  );
}