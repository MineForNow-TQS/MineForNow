BEGIN;



CREATE TABLE IF NOT EXISTS users (
    id                      BIGSERIAL PRIMARY KEY,

    email                   VARCHAR(255) NOT NULL UNIQUE,
    password_hash           VARCHAR(255) NOT NULL,
    full_name               VARCHAR(255) NOT NULL,
    phone_number            VARCHAR(50),
    date_of_birth           DATE,
    nationality             VARCHAR(100),

    role                    VARCHAR(20) NOT NULL DEFAULT 'RENTER',
    status                  VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    driver_license_number   VARCHAR(100) UNIQUE,
    driver_license_country  VARCHAR(100),
    driver_license_expiry   DATE,

    created_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at              TIMESTAMPTZ,

    CONSTRAINT chk_user_status CHECK (status IN ('ACTIVE','BLOCKED')),
    CONSTRAINT chk_user_role   CHECK (role IN ('RENTER','OWNER','ADMIN'))
);

CREATE INDEX IF NOT EXISTS idx_users_role   ON users(role);
CREATE INDEX IF NOT EXISTS idx_users_status ON users(status);




CREATE TABLE IF NOT EXISTS owner_requests (
    id              BIGSERIAL PRIMARY KEY,
    user_id          BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,

    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    requested_at    TIMESTAMPTZ NOT NULL DEFAULT now(),

    reviewed_by     BIGINT REFERENCES users(id) ON DELETE SET NULL,
    reviewed_at     TIMESTAMPTZ,
    reason          TEXT,

    CONSTRAINT chk_owner_requests_status CHECK (status IN ('PENDING','APPROVED','REJECTED'))
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_owner_requests_user_pending
  ON owner_requests(user_id)
  WHERE status = 'PENDING';

CREATE INDEX IF NOT EXISTS idx_owner_requests_user   ON owner_requests(user_id);
CREATE INDEX IF NOT EXISTS idx_owner_requests_status ON owner_requests(status);



CREATE TABLE IF NOT EXISTS vehicles (
    id                      BIGSERIAL PRIMARY KEY,
    owner_user_id           BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,

    title                   VARCHAR(255),  -- optional title

    -- basic info:
    brand                   VARCHAR(100) NOT NULL,
    model                   VARCHAR(100) NOT NULL,
    year                    INT NOT NULL,
    car_type                VARCHAR(50) NOT NULL, -- e.g., SUV, Sedan, Hatchback
    mileage                 INT NOT NULL,
    license_plate           VARCHAR(50) NOT NULL UNIQUE,

    -- specifications:
    fuel_type               VARCHAR(50) NOT NULL,
    transmission            VARCHAR(50) NOT NULL,
    seats                   INT NOT NULL,
    doors                   INT NOT NULL,

    -- characteristics:
    air_conditioning        BOOLEAN NOT NULL DEFAULT FALSE,
    gps                     BOOLEAN NOT NULL DEFAULT FALSE,
    bluetooth               BOOLEAN NOT NULL DEFAULT FALSE,

    -- location & pricing:
    city                    VARCHAR(100) NOT NULL,
    location                VARCHAR(255) NOT NULL,
    daily_price             NUMERIC(12,2) NOT NULL,
    currency                VARCHAR(10) NOT NULL DEFAULT 'EUR',

    description             TEXT NOT NULL,

    status                  VARCHAR(20) NOT NULL DEFAULT 'VISIBLE',

    created_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at              TIMESTAMPTZ,

    CONSTRAINT chk_vehicle_year_reasonable CHECK (year BETWEEN 1900 AND (EXTRACT(YEAR FROM now())::INT + 1)),
    CONSTRAINT chk_vehicle_mileage_nonnegative CHECK (mileage >= 0),
    CONSTRAINT chk_vehicle_seats_reasonable CHECK (seats BETWEEN 1 AND 99),
    CONSTRAINT chk_vehicle_doors_reasonable CHECK (doors BETWEEN 1 AND 10),
    CONSTRAINT chk_vehicle_price_nonnegative CHECK (daily_price >= 0),

    CONSTRAINT chk_vehicle_type CHECK (car_type IN ('citadino','sedan','suv','desportivo','carrinha','hatchback','monovolume','coupé')),
    CONSTRAINT chk_vehicle_fuel CHECK (fuel_type IN ('gasoline','diesel','electric','hybrid','gpl')),
    CONSTRAINT chk_vehicle_transmission CHECK (transmission IN ('manual','automatic')),
    CONSTRAINT chk_vehicle_status CHECK (status IN ('VISIBLE','HIDDEN','BLOCKED'))
);

CREATE INDEX IF NOT EXISTS idx_vehicles_owner ON vehicles(owner_user_id);
CREATE INDEX IF NOT EXISTS idx_vehicles_city  ON vehicles(city);
CREATE INDEX IF NOT EXISTS idx_vehicles_filters ON vehicles(city, car_type, fuel_type, transmission, seats);
CREATE INDEX IF NOT EXISTS idx_vehicles_status ON vehicles(status);



CREATE TABLE IF NOT EXISTS vehicle_images (
    id                      BIGSERIAL PRIMARY KEY,
    vehicle_id              BIGINT NOT NULL REFERENCES vehicles(id) ON DELETE CASCADE,
    url                     TEXT NOT NULL,
    is_cover                BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_vehicle_images_vehicle ON vehicle_images(vehicle_id);
CREATE INDEX IF NOT EXISTS idx_vehicle_images_cover   ON vehicle_images(vehicle_id, is_cover);



CREATE TABLE IF NOT EXISTS bookings (
    id                      BIGSERIAL PRIMARY KEY,
    vehicle_id              BIGINT NOT NULL REFERENCES vehicles(id) ON DELETE RESTRICT,
    renter_user_id          BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,

    start_datetime          TIMESTAMPTZ NOT NULL,
    end_datetime            TIMESTAMPTZ NOT NULL,

    status                  VARCHAR(20) NOT NULL DEFAULT 'PENDING',

    total_price             NUMERIC(12,2) NOT NULL,
    currency                VARCHAR(10) NOT NULL DEFAULT 'EUR',

    created_at              TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT chk_booking_dates CHECK (end_datetime > start_datetime),
    CONSTRAINT chk_booking_total_price_nonnegative CHECK (total_price >= 0),
    CONSTRAINT chk_booking_status CHECK (status IN ('PENDING','CONFIRMED','CANCELLED'))
);

CREATE INDEX IF NOT EXISTS idx_bookings_vehicle ON bookings(vehicle_id);
CREATE INDEX IF NOT EXISTS idx_bookings_renter  ON bookings(renter_user_id);
CREATE INDEX IF NOT EXISTS idx_bookings_period  ON bookings(start_datetime, end_datetime);
CREATE INDEX IF NOT EXISTS idx_bookings_status  ON bookings(status);



CREATE TABLE IF NOT EXISTS reviews (
    id                      BIGSERIAL PRIMARY KEY,

    booking_id              BIGINT REFERENCES bookings(id) ON DELETE SET NULL,
    author_user_id          BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    owner_user_id           BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    vehicle_id              BIGINT NOT NULL REFERENCES vehicles(id) ON DELETE RESTRICT,

    rating                  INT NOT NULL,
    comment                 TEXT,

    created_at              TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT chk_review_rating CHECK (rating BETWEEN 1 AND 5)
);

CREATE INDEX IF NOT EXISTS idx_reviews_vehicle ON reviews(vehicle_id);
CREATE INDEX IF NOT EXISTS idx_reviews_owner   ON reviews(owner_user_id);
CREATE INDEX IF NOT EXISTS idx_reviews_author  ON reviews(author_user_id);
CREATE INDEX IF NOT EXISTS idx_reviews_booking ON reviews(booking_id);

CREATE UNIQUE INDEX IF NOT EXISTS uq_reviews_booking
    ON reviews(booking_id)
    WHERE booking_id IS NOT NULL;



CREATE TABLE IF NOT EXISTS vehicle_blocks (
    id                      BIGSERIAL PRIMARY KEY,
    vehicle_id              BIGINT NOT NULL REFERENCES vehicles(id) ON DELETE CASCADE,

    start_date              DATE NOT NULL,
    end_date                DATE NOT NULL,

    created_by_user_id      BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT chk_vehicle_block_dates CHECK (end_date >= start_date)
);

CREATE INDEX IF NOT EXISTS idx_vehicle_blocks_vehicle ON vehicle_blocks(vehicle_id);
CREATE INDEX IF NOT EXISTS idx_vehicle_blocks_period  ON vehicle_blocks(start_date, end_date);



COMMIT;
