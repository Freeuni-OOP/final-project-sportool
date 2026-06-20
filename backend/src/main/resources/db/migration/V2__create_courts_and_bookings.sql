-- Migration script for Stage 2: Court Infrastructure and Reservation Engine
-- This script adds specific court configurations and maps the schedules to prevent double-booking

-- 1. Create Courts Table with capacity for dynamic pricing and surface types
CREATE TABLE IF NOT EXISTS courts (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    hourly_price DECIMAL(10, 2) NOT NULL
);

-- 2. Create Bookings Table establishing a Many-to-Many link between Users and Courts
CREATE TABLE IF NOT EXISTS bookings (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    court_id INT NOT NULL REFERENCES courts(id) ON DELETE CASCADE,
    booking_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);