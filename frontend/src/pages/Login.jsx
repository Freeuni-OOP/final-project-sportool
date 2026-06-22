import { useState } from 'react';
import { apiClient, saveAuthSession } from '../api/client.js';
import Button from '../components/Button.jsx';
import Navbar from '../components/Navbar.jsx';

const initialFormState = {
  email: '',
  password: '',
};

export default function Login({ onAuthenticated }) {
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
    setIsSubmitting(true);

    try {
      const response = await apiClient.login({
        email: formValues.email.trim(),
        password: formValues.password,
      });

      const session = saveAuthSession(response);

      window.dispatchEvent(new Event('sportool:auth-changed'));

      setSuccess(response?.message || 'Login successful. Redirecting...');
      onAuthenticated?.(session);

      window.setTimeout(() => {
        window.location.hash = 'dashboard';
      }, 450);
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
              <p className="eyebrow">Secure member access</p>
              <h1>Sign in to command your next match.</h1>
              <p>
                Jump back into SporTool matchmaking, court discovery, and your
                personal booking flow in seconds.
              </p>
            </div>

            <form className="auth-card" onSubmit={handleSubmit}>
              <div>
                <p className="eyebrow">Welcome back</p>
                <h2>Login</h2>
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
                <span>Password</span>
                <input
                    autoComplete="current-password"
                    name="password"
                    onChange={handleChange}
                    placeholder="Enter your password"
                    required
                    type="password"
                    value={formValues.password}
                />
              </label>

              <div className="auth-card__meta">
                <label>
                  <input type="checkbox" /> Keep me signed in
                </label>
                <a href="#forgot-password">Forgot Password?</a>
              </div>

              <Button className="auth-card__submit" disabled={isSubmitting} type="submit">
                {isSubmitting ? 'Signing In...' : 'Sign In'}
              </Button>

              <p className="auth-card__footer">
                Don&apos;t have an account?{' '}
                <a href="#signup">Sign up</a>
              </p>
            </form>
          </div>
        </section>
      </main>
  );
}