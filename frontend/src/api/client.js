const AUTH_STORAGE_KEY = 'sportool.auth';

function getDefaultApiBaseUrl() {
  return 'http://localhost:8080/api';
}

export const API_BASE_URL = getDefaultApiBaseUrl();

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

export function saveAuthSession(authResponse) {
  const session = {
    fullName: authResponse?.fullName || '',
    role: authResponse?.role || '',
    userId: authResponse?.userId || null,
    trainerId: authResponse?.trainerId || null,
    loginAt: new Date().toISOString(),
    hasToken: true,
  };

  window.localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(session));
  return session;
}

export function clearAuthSession() {
  window.localStorage.removeItem(AUTH_STORAGE_KEY);
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
  const headers = new Headers(options.headers);

  if (!headers.has('Content-Type') && options.body) {
    headers.set('Content-Type', 'application/json');
  }

  try {
    const response = await fetch(`${API_BASE_URL}${path}`, {
      ...options,
      credentials: 'include',
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

  register(user) {
    return apiRequest('/register', {
      method: 'POST',
      body: JSON.stringify(user),
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

  processPayment(payload) {
    return apiRequest('/payments/process', {
      method: 'POST',
      body: JSON.stringify(payload),
    });
  },

  createBooking(payload) {
    return apiRequest('/bookings', {
      method: 'POST',
      body: JSON.stringify(payload),
    });
  },

  getCourtBookings(courtId, date) {
    const params = new URLSearchParams({
      courtId: String(courtId),
      date,
    });
    return apiRequest(`/bookings?${params.toString()}`);
  },

  getMyBookings() {
    return apiRequest('/bookings?mine=true');
  },

  getMatches() {
    return apiRequest('/matches');
  },

  createMatch(matchData) {
    return apiRequest('/matches', {
      method: 'POST',
      body: JSON.stringify(matchData),
    });
  },

  joinMatch(matchId) {
    return apiRequest(`/matches/join?id=${matchId}`, {
      method: 'POST',
    });
  },

  deleteMatch(matchId) {
    return apiRequest(`/matches?id=${matchId}`, {
      method: 'DELETE',
    });
  },


  getPosts() {
    return apiRequest('/posts');
  },

  createPost(postData) {
    return apiRequest('/posts', {
      method: 'POST',
      body: JSON.stringify(postData),
    });
  },

  deletePost(postId) {
    return apiRequest(`/posts?id=${postId}`, {
      method: 'DELETE',
    });
  },

  getComments(postId) {
    return apiRequest(`/comments?postId=${postId}`);
  },

  createComment(commentData) {
    return apiRequest('/comments', {
      method: 'POST',
      body: JSON.stringify(commentData),
    });
  },

  deleteComment(commentId) {
    return apiRequest(`/comments?id=${commentId}`, {
      method: 'DELETE',
    });
  },

  getTrainers() {
    return apiRequest('/trainers');
  },

  getTrainer(trainerId) {
    return apiRequest(`/trainers/${trainerId}`);
  },

  getMyTrainerProfile() {
    return apiRequest('/trainers/me');
  },

  updateMyTrainerProfile(payload) {
    return apiRequest('/trainers/me', {
      method: 'PUT',
      body: JSON.stringify(payload),
    });
  },

  updateMyTrainerDescription(description) {
    return apiRequest('/trainer-profile/me', {
      method: 'PUT',
      body: JSON.stringify({ description }),
    });
  },

  createTrainerVenue(payload) {
    return apiRequest('/trainer-venues', {
      method: 'POST',
      body: JSON.stringify(payload),
    });
  },

  updateTrainerVenue(payload) {
    return apiRequest('/trainer-venues', {
      method: 'PUT',
      body: JSON.stringify(payload),
    });
  },

  deleteTrainerVenue(venueId) {
    return apiRequest(`/trainer-venues/${venueId}`, {
      method: 'DELETE',
    });
  },

  createTrainerBooking(payload) {
    return apiRequest('/trainer-bookings', {
      method: 'POST',
      body: JSON.stringify(payload),
    });
  },

  getMyTrainerBookings() {
    return apiRequest('/trainer-bookings');
  },

  getTrainerIncomingBookings() {
    return apiRequest('/trainer-bookings?forTrainer=true');
  },

  updateTrainerBookingStatus(bookingId, status) {
    return apiRequest(`/trainer-bookings/${bookingId}/status`, {
      method: 'PUT',
      body: JSON.stringify({ status }),
    });
  },

  getTrainerReviews(trainerId) {
    return apiRequest(`/trainer-reviews?trainerId=${trainerId}`);
  },

  createTrainerReview(payload) {
    return apiRequest('/trainer-reviews', {
      method: 'POST',
      body: JSON.stringify(payload),
    });
  },
};
