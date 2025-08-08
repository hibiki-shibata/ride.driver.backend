-- CREATE SEQUENCE IF NOT EXISTS driver_details_seq
--     START WITH 1
--     INCREMENT BY 1;

-- DROP TABLE IF EXISTS driver_details;
-- DROP TABLE IF EXISTS area;

-- CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- DO $$ BEGIN
--     CREATE TYPE vehicle_type AS ENUM ('CAR', 'BIKE', 'VAN', 'TRUCK'); -- customize as needed
-- EXCEPTION
--     WHEN duplicate_object THEN null;
-- END $$;

-- DO $$ BEGIN
--     CREATE TYPE driver_status AS ENUM ('AVAILABLE', 'UNAVAILABLE', 'ON_TRIP'); -- customize as needed
-- EXCEPTION
--     WHEN duplicate_object THEN null;
-- END $$;

-- CREATE TABLE area (
--     id BIGSERIAL PRIMARY KEY,
--     name VARCHAR(255) NOT NULL
-- );

-- CREATE TABLE driver_details (
--     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
--     phone_number VARCHAR(50) NOT NULL,
--     name VARCHAR(255) NOT NULL,
--     vehicle_type vehicle_type NOT NULL,
--     location_latitude DOUBLE PRECISION,
--     location_longitude DOUBLE PRECISION,
--     assign_id VARCHAR(255),
--     rate DOUBLE PRECISION,
--     status driver_status DEFAULT 'AVAILABLE' NOT NULL,
--     area_id BIGINT REFERENCES area(id),
--     driver_comments TEXT,
--     version INTEGER DEFAULT 0 NOT NULL
-- );

