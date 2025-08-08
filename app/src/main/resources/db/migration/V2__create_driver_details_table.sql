


-- Table: driver_details
CREATE TABLE IF NOT EXISTS driver_details (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),  -- Use gen_random_uuid() if pgcrypto extension is enabled
    phone_number VARCHAR NOT NULL,
    name VARCHAR NOT NULL,
    vehicle_type VARCHAR,

    
    -- Embedded location fields (add actual fields)
    latitude DOUBLE PRECISION,    -- example field
    longitude DOUBLE PRECISION,   -- example field

    assign_id VARCHAR,
    rate DOUBLE PRECISION,

    status VARCHAR DEFAULT 'AVAILABLE',

    area_id BIGSERIAL REFERENCES area(id),
    driver_comments TEXT,
    
    version INTEGER DEFAULT 0
);
