


-- Table: driver_details
CREATE TABLE IF NOT EXISTS driver_details (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),  -- Use gen_random_uuid() if pgcrypto extension is enabled
    phone_number VARCHAR NOT NULL,
    name VARCHAR NOT NULL,
    vehicle_type VARCHAR,

    latitude DOUBLE PRECISION,    
    longitude DOUBLE PRECISION,  

    assign_id VARCHAR,
    rate DOUBLE PRECISION,

    status VARCHAR DEFAULT 'AVAILABLE',

    area_id BIGSERIAL REFERENCES area(id) ON DELETE CASCADE,
    driver_comments TEXT

    -- CONSTRAINT fk_area FOREIGN KEY (area_id) REFERENCES area(id)

    
);
