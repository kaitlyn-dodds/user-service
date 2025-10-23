-- Insert Users
INSERT INTO users (id, username, email, password_hash, status, created_at, updated_at)
VALUES
    ('4f6a1f00-4b4c-4b5c-9b1f-18e74e66c5b0', 'redpanda8989', 'kaitlyn@example.com', '', 'ACTIVE', NOW(), NOW()),
    ('23c82f4e-728d-4ffb-b4f0-9ac2c310b1c7', 'mathat', 'matthew@example.com', '', 'INACTIVE', NOW(), NOW()),
    ('be93c23e-27f3-4b8d-b403-1d6cb56bb36f', 'supperdog', 'sarah@example.com', '', 'ACTIVE', NOW(), NOW());


-- Insert User Profiles (shared primary key = user.id)
INSERT INTO user_profiles (user_id, first_name, last_name, phone_number, profile_image_url, created_at, updated_at)
VALUES
    ('4f6a1f00-4b4c-4b5c-9b1f-18e74e66c5b0', 'Kaitlyn', 'Dodds', '+1-303-555-0123', 'https://cdn.example.com/profiles/kaitlyn.png', NOW(), NOW()),
    ('23c82f4e-728d-4ffb-b4f0-9ac2c310b1c7', 'Matthew', 'Reed', '+1-720-555-0192', 'https://cdn.example.com/profiles/matthew.png', NOW(), NOW()),
    ('be93c23e-27f3-4b8d-b403-1d6cb56bb36f', 'Sarah', 'Kim', '+1-970-555-0332', 'https://cdn.example.com/profiles/sarah.png', NOW(), NOW());


-- Insert User Addresses (1:many)
INSERT INTO user_addresses (id, user_id, address_line_1, address_line_2, city, state, zip_code, country, created_at, updated_at)
VALUES
    -- Kaitlyn
    ('a88d99e2-45e3-49d0-9b1a-15f86db5b140', '4f6a1f00-4b4c-4b5c-9b1f-18e74e66c5b0', '123 Maple St', NULL, 'Denver', 'CO', '80202', 'USA', NOW(), NOW()),
    ('a9f43a5d-3729-49e5-9a58-8a1f147b422a', '4f6a1f00-4b4c-4b5c-9b1f-18e74e66c5b0', '45 Industrial Blvd', 'Suite 200', 'Aurora', 'CO', '80012', 'USA', NOW(), NOW()),

    -- Matthew
    ('f32b9f7d-84d3-48b5-a321-3f726905b22e', '23c82f4e-728d-4ffb-b4f0-9ac2c310b1c7', '9 Evergreen Ct', NULL, 'Boulder', 'CO', '80301', 'USA', NOW(), NOW()),

    -- Sarah
    ('c8a4b5c0-0b3c-4b54-bd6e-9d94659a8df0', 'be93c23e-27f3-4b8d-b403-1d6cb56bb36f', '742 Evergreen Terrace', NULL, 'Fort Collins', 'CO', '80521', 'USA', NOW(), NOW());
