CREATE TABLE IF NOT EXISTS courier_profile (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),  -- Use gen_random_uuid() if pgcrypto extension is enabled
    phone_number VARCHAR NOT NULL,
    name VARCHAR NOT NULL,
    vehicle_type VARCHAR,

    rate DOUBLE PRECISION,

    status VARCHAR DEFAULT 'UNAVAILABLE',

    area_id BIGSERIAL REFERENCES area(id) ON DELETE CASCADE,
    comments TEXT
    -- CONSTRAINT fk_area FOREIGN KEY (area_id) REFERENCES area(id)
);
