-- ======================================================
-- DROP TABLES
-- ======================================================
-- DROP TABLE IF EXISTS user_addresses;
-- DROP TABLE IF EXISTS user_profiles;
-- DROP TABLE IF EXISTS users;

-- Enable UUID generation (required for gen_random_uuid())
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- ======================================================
-- USERS TABLE
-- ======================================================
CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       username VARCHAR(50) UNIQUE NOT NULL,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password_hash TEXT NOT NULL,
                       status VARCHAR(20) NOT NULL DEFAULT 'active',
                       created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                       updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Index for quick lookups
CREATE INDEX idx_users_email ON users(email);

-- ======================================================
-- USER PROFILES TABLE (1:1)
-- ======================================================
CREATE TABLE user_profiles (
                               user_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
                               first_name VARCHAR(50),
                               last_name VARCHAR(50),
                               phone_number VARCHAR(15),
                               profile_image_url TEXT,
                               created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                               updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Enforce 1:1 relationship (user_id is both PK and FK)
ALTER TABLE user_profiles
    ADD CONSTRAINT fk_user_profiles_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;


-- ======================================================
-- USER ADDRESSES TABLE (1:N)
-- ======================================================
CREATE TABLE user_addresses (
                                id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                address_type VARCHAR(20), -- e.g. 'home', 'billing', 'shipping'
                                address_line_1 VARCHAR(100) NOT NULL,
                                address_line_2 VARCHAR(100),
                                city VARCHAR(50) NOT NULL,
                                state VARCHAR(50),
                                zip_code VARCHAR(20),
                                country VARCHAR(50) NOT NULL,
                                created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                                updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_user_addresses_user_id ON user_addresses(user_id);
