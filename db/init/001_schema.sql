BEGIN;


CREATE TABLE IF NOT EXISTS users (
    id                      SERIAL PRIMARY KEY,
    email                   VARCHAR(255) NOT NULL UNIQUE,
    password_hash           VARCHAR(255) NOT NULL,
    full_name               VARCHAR(255) NOT NULL,
    phone_number            VARCHAR(50),
    nationality             VARCHAR(100),
    date_of_birth           DATE,
    -- is_active               BOOLEAN NOT NULL DEFAULT TRUE,
    status                  VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    driver_license_number   VARCHAR(100), UNIQUE

    CONSTRAINT chk_user_status CHECK (status IN ('ACTIVE','BLOCKED')
);


CREATE TABLE IF NOT EXISTS roles (
    id                      SERIAL PRIMARY KEY,
    role_name               VARCHAR(100) NOT NULL UNIQUE

    CONSTRAINT chk_role_name CHECK (role_name IN ('RENTER','OWNER','ADMIN'))
);


CREATE TABLE IF NOT EXISTS user_roles (
    user_id                 INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id                 INT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);


CREATE TABLE IF NOT EXISTS vehicles (
    id                      SERIAL PRIMARY KEY,
    owner_user_id           INT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,

    title                   VARCHAR(255),  -- optional title

    -- basic info:
    brand                   VARCHAR(100) NOT NULL,
    model                   VARCHAR(100) NOT NULL,
    year                    INT,         NOT NULL,
    car_type                VARCHAR(50), NOT NULL, -- e.g., SUV, Sedan, Hatchback
    kilometers_driven       INT,         NOT NULL,
    license_plate           VARCHAR(50) UNIQUE, NOT NULL,

    -- specifications:
    fuel_type               VARCHAR(50), NOT NULL,
    transmission            VARCHAR(50), NOT NULL,
    seats                   INT,        NOT NULL,
    doors                   INT,        NOT NULL,

    -- characteristics:
    air_conditioning        BOOLEAN NOT NULL DEFAULT FALSE,
    gps                     BOOLEAN NOT NULL DEFAULT FALSE,
    bluetooth               BOOLEAN NOT NULL DEFAULT FALSE,

    -- location & pricing:
    location_city           VARCHAR(100) NOT NULL,
    exact_address           VARCHAR(255), NOT NULL,
    daily_price             NUMERIC(12,2) NOT NULL,
    description             TEXT,

    status                  VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',  

    CONSTRAINT chk_vehicle_price_positive CHECK (daily_price >= 0),
    CONSTRAINT chk_vehicle_year_reasonable CHECK (year IS NULL OR (year >= 1900 AND year <= EXTRACT(YEAR FROM now())::INT + 1)),
    CONSTRAINT chk_vehicle_status CHECK (status IN ('ACTIVE','SUSPENDED','BLOCKED'))
);


CREATE TABLE IF NOT EXISTS vehicle_images (
    id                      SERIAL PRIMARY KEY,
    vehicle_id              INT NOT NULL REFERENCES vehicles(id) ON DELETE CASCADE,
    url                     TEXT NOT NULL,
);


CREATE TABLE IF NOT EXISTS bookings (
    id                      SERIAL PRIMARY KEY,
    vehicle_id              INT NOT NULL REFERENCES vehicles(id) ON DELETE RESTRICT,
    renter_user_id          INT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,

    start_datetime          TIMESTAMPTZ NOT NULL,
    end_datetime            TIMESTAMPTZ NOT NULL,

    status                  VARCHAR(50) NOT NULL DEFAULT 'PENDING',

    total_price             NUMERIC(12,2) NOT NULL,

    CONSTRAINT chk_booking_dates CHECK (end_datetime > start_datetime),
    CONSTRAINT chk_booking_total_price_nonnegative CHECK (total_price >= 0),
    CONSTRAINT chk_booking_status CHECK (status IN ('PENDING','CONFIRMED','CANCELLED'))
);


CREATE TABLE IF NOT EXISTS reviews (
    id                      SERIAL PRIMARY KEY,

    booking_id              INT REFERENCES bookings(id) ON DELETE SET NULL,
    author_user_id          INT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    owner_user_id           INT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    vehicle_id              INT NOT NULL REFERENCES vehicles(id) ON DELETE RESTRICT,

    rating                  INT NOT NULL,
    comment                 TEXT,

    CONSTRAINT chk_review_rating CHECK (rating BETWEEN 1 AND 5)
);


CREATE TABLE IF NOT EXISTS vehicle_blocks (
    id                      SERIAL PRIMARY KEY,
    vehicle_id              INT NOT NULL REFERENCES vehicles(id) ON DELETE CASCADE,

    start_date              DATE NOT NULL,
    end_date                DATE NOT NULL,

    created_by_user_id      INT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,

    CONSTRAINT chk_vehicle_block_dates CHECK (end_date >= start_date)
);


COMMIT;
