CREATE TABLE IF NOT EXISTS courier_profile (

    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cp_first_name VARCHAR NOT NULL,
    cp_last_name VARCHAR NOT NULL,
    phone_number VARCHAR UNIQUE NOT NULL,
    vehicle_type VARCHAR NOT NULL,
    password_hash VARCHAR,
    cp_rate DOUBLE PRECISION,
    cp_status VARCHAR NOT NULL,
    cp_comments VARCHAR,
    current_latitude DOUBLE PRECISION NOT NULL,
    current_longitude DOUBLE PRECISION NOT NULL,
    operation_area_id BIGINT NULL REFERENCES operation_area(id) ON DELETE SET NULL
    -- CONSTRAINT fk_area FOREIGN KEY (area_id) REFERENCES area(id)
);



-- 
