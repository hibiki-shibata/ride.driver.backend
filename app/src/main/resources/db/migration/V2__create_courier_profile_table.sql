CREATE TABLE IF NOT EXISTS courier_profile (

    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),  -- Use gen_random_uuid() if pgcrypto extension is enabled
    
    phone_number VARCHAR NOT NULL,

    password_hash VARCHAR NOT NULL,

    name VARCHAR NOT NULL,

    vehicle_type VARCHAR,

    rate DOUBLE PRECISION,

    status VARCHAR DEFAULT 'UNAVAILABLE',

    operation_area_id BIGSERIAL REFERENCES operation_area(id) ON DELETE CASCADE,
    comments TEXT
    -- CONSTRAINT fk_area FOREIGN KEY (area_id) REFERENCES area(id)
);
