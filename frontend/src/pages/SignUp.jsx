import { useState } from 'react';
import { apiClient } from '../api/client.js';
import Button from '../components/Button.jsx';
import Navbar from '../components/Navbar.jsx';

const initialFormState = {
  fullName: '',
  email: '',
  password: '',
  confirmPassword: '',
  role: 'PLAYER',
};

const roleOptions = [
  { label: 'Player', value: 'PLAYER' },
  { label: 'Coach', value: 'COACH' },
];

export default function SignUp() {
  const [formValues, setFormValues] = useState(initialFormState);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  function handleChange(event) {
    const { name, value } = event.target;

    setFormValues((currentValues) => ({
      ...currentValues,
      [name]: value,
    }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setError('');
    setSuccess('');

    if (formValues.password !== formValues.confirmPassword) {
      setError('Passwords do not match.');
      return;
    }

    if (formValues.password.length < 6) {
      setError('Password must be at least 6 characters long.');
      return;
    }

    setIsSubmitting(true);

    try {
      const response = await apiClient.register({
        fullName: formValues.fullName.trim(),
        email: formValues.email.trim(),
        password: formValues.password,
        role: formValues.role,
      });

      setSuccess(response?.message || 'Account created successfully. Redirecting to login...');

      window.setTimeout(() => {
        window.location.hash = 'login';
      }, 900);
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <main className="app-shell">
      <Navbar />

      <section className="auth-page section">
        <div className="container auth-page__grid">
          <div className="auth-copy">
            <p className="eyebrow">Join the club</p>
            <h1>Create your SporTool member account.</h1>
            <p>
              Register to unlock player matchmaking, court booking, and coach
              connections across the SporTool platform.
            </p>
          </div>

          <form className="auth-card" onSubmit={handleSubmit}>
            <div>
              <p className="eyebrow">New member</p>
              <h2>Sign Up</h2>
            </div>

            {error ? (
              <div className="notice notice--error" role="alert">
                {error}
              </div>
            ) : null}

            {success ? (
              <div className="notice notice--success" role="status">
                {success}
              </div>
            ) : null}

            <label className="field">
              <span>Full name</span>
              <input
                autoComplete="name"
                name="fullName"
                onChange={handleChange}
                placeholder="Your full name"
                required
                type="text"
                value={formValues.fullName}
              />
            </label>

            <label className="field">
              <span>Email</span>
              <input
                autoComplete="email"
                name="email"
                onChange={handleChange}
                placeholder="you@sportool.club"
                required
                type="email"
                value={formValues.email}
              />
            </label>

            <label className="field">
              <span>Role</span>
              <select
                name="role"
                onChange={handleChange}
                required
                value={formValues.role}
              >
                {roleOptions.map((option) => (
                  <option key={option.value} value={option.value}>
                    {option.label}
                  </option>
                ))}
              </select>
            </label>

            <label className="field">
              <span>Password</span>
              <input
                autoComplete="new-password"
                minLength={6}
                name="password"
                onChange={handleChange}
                placeholder="At least 6 characters"
                required
                type="password"
                value={formValues.password}
              />
            </label>

            <label className="field">
              <span>Confirm password</span>
              <input
                autoComplete="new-password"
                minLength={6}
                name="confirmPassword"
                onChange={handleChange}
                placeholder="Re-enter your password"
                required
                type="password"
                value={formValues.confirmPassword}
              />
            </label>

            <Button className="auth-card__submit" disabled={isSubmitting} type="submit">
              {isSubmitting ? 'Creating Account...' : 'Create Account'}
            </Button>

            <p className="auth-card__footer">
              Already have an account?{' '}
              <a href="#login">Sign in</a>
            </p>
          </form>
        </div>
      </section>
    </main>
  );
}
