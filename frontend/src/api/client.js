const AUTH_STORAGE_KEY = 'sportool.auth';
const TOKEN_STORAGE_KEY = 'sportool.authToken';

function getDefaultApiBaseUrl() {
  if (import.meta.env.VITE_API_BASE_URL) {
    return import.meta.env.VITE_API_BASE_URL.replace(/\/$/, '');
  }

  if (typeof window === 'undefined') {
    return 'http://localhost:8080/backend/api';
  }

  const { protocol, hostname, origin, pathname, port } = window.location;
  const contextRoot = pathname.split('/').filter(Boolean)[0];

  if (contextRoot === 'backend') {
    return `${origin}/backend/api`;
  }

  if (port && port !== '8080') {
    return `${protocol}//${hostname}:8080/backend/api`;
  }

  return `${origin}/backend/api`;
}

export const API_BASE_URL = getDefaultApiBaseUrl();
export const API_CREDENTIALS_MODE =
  import.meta.env.VITE_API_WITH_CREDENTIALS === 'true' ? 'include' : 'same-origin';

export class ApiError extends Error {
  constructor(message, { status, data } = {}) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.data = data;
  }
}

export function getStoredAuth() {
  try {
    const storedAuth = window.localStorage.getItem(AUTH_STORAGE_KEY);
    return storedAuth ? JSON.parse(storedAuth) : null;
  } catch {
    return null;
  }
}

export function getAuthToken() {
  return window.localStorage.getItem(TOKEN_STORAGE_KEY);
}

export function saveAuthSession(authResponse) {
  const token =
    authResponse?.token ||
    authResponse?.jwt ||
    authResponse?.accessToken ||
    authResponse?.authToken ||
    null;

  if (token) {
    window.localStorage.setItem(TOKEN_STORAGE_KEY, token);
  }

  const session = {
    fullName: authResponse?.fullName || '',
    role: authResponse?.role || '',
    loginAt: new Date().toISOString(),
    hasToken: Boolean(token),
  };

  window.localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(session));
  return session;
}

export function clearAuthSession() {
  window.localStorage.removeItem(AUTH_STORAGE_KEY);
  window.localStorage.removeItem(TOKEN_STORAGE_KEY);
}

async function parseJsonResponse(response) {
  const contentType = response.headers.get('content-type') || '';

  if (!contentType.includes('application/json')) {
    const text = await response.text();
    return text ? { message: text } : null;
  }

  return response.json();
}

export async function apiRequest(path, options = {}) {
  const token = getAuthToken();
  const headers = new Headers(options.headers);

  if (!headers.has('Content-Type') && options.body) {
    headers.set('Content-Type', 'application/json');
  }

  if (token && !headers.has('Authorization')) {
    headers.set('Authorization', `Bearer ${token}`);
  }

  try {
    const response = await fetch(`${API_BASE_URL}${path}`, {
      ...options,
      credentials: options.credentials || API_CREDENTIALS_MODE,
      headers,
    });

    const data = await parseJsonResponse(response);

    if (!response.ok) {
      throw new ApiError(
        data?.message || `Request failed with status ${response.status}.`,
        { status: response.status, data },
      );
    }

    return data;
  } catch (error) {
    if (error instanceof ApiError) {
      throw error;
    }

    throw new ApiError(
      'Cannot reach the SporTool server. Please check that Tomcat is running.',
      { status: 0, data: null },
    );
  }
}

export const apiClient = {
  login(credentials) {
    return apiRequest('/login', {
      method: 'POST',
      body: JSON.stringify(credentials),
    });
  },

  getCourts(filters = {}) {
    const params = new URLSearchParams();

    if (filters.type && filters.type !== 'all') {
      params.set('type', filters.type);
    }

    const query = params.toString();
    return apiRequest(`/courts${query ? `?${query}` : ''}`);
  },

  createBooking(payload) {
    return apiRequest('/bookings', {
      method: 'POST',
      body: JSON.stringify(payload),
    });
  },
};