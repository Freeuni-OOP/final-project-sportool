-- users table
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('PLAYER', 'COACH', 'ADMIN')),
    full_name VARCHAR(100)
    );

-- courts table
CREATE TABLE IF NOT EXISTS courts (
    id SERIAL PRIMARY KEY,
    court_name VARCHAR(255) NOT NULL,
    court_type VARCHAR(50) NOT NULL,
    location VARCHAR(255) NOT NULL,
    price_per_hour NUMERIC(10, 2) NOT NULL
    );

INSERT INTO courts (id, court_name, court_type, location, price_per_hour) VALUES
    (1, 'Padel Tbilisi', 'Padel', 'Vake Park', 55.00),
    (2, 'Saburtalo Football Arena', 'Football', 'Tsintsadze St', 40.00),
    (3, 'Marjanishvili Tennis Club', 'Tennis', 'Marjanishvili', 35.00),
    (4, 'Vera Park Basketball Court', 'Basketball', 'Vera', 25.00),
    (5, 'Digomi Padel Center', 'Padel', 'Digomi', 50.00),
    (6, 'Lisi Lake Football Pitch', 'Football', 'Lisi', 45.00),
    (7, 'Rustaveli Tennis Academy', 'Tennis', 'Rustaveli Ave', 40.00),
    (8, 'Gldani Indoor Basketball', 'Basketball', 'Gldani', 30.00)
ON CONFLICT (id) DO NOTHING;

SELECT setval(
    pg_get_serial_sequence('courts', 'id'),
    (SELECT COALESCE(MAX(id), 1) FROM courts)
);

-- trainers table
CREATE TABLE IF NOT EXISTS trainers (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(id),
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    sport_type VARCHAR(50) NOT NULL,
    price_per_session NUMERIC(10, 2) NOT NULL,
    rating NUMERIC(3, 2) DEFAULT 0.0,
    review_count INT DEFAULT 0
    );


-- bookings table
CREATE TABLE IF NOT EXISTS bookings (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    court_id INT NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    total_price NUMERIC(10, 2) NOT NULL,
    status VARCHAR(50) DEFAULT 'CONFIRMED',
    payment_status VARCHAR(20) DEFAULT 'PAID',
    payment_reference VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (court_id) REFERENCES courts(id) ON DELETE CASCADE
);

--posts table
CREATE TABLE IF NOT EXISTS posts (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

--comments table
CREATE TABLE comments (
    id SERIAL PRIMARY KEY,
    post_id INT NOT NULL,
    user_id INT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);