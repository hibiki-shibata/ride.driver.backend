CREATE INDEX IF NOT EXISTS idx_driver_details_phone_number ON driver_details (phone_number);
CREATE INDEX IF NOT EXISTS idx_driver_details_area_id ON driver_details (area_id);
CREATE INDEX IF NOT EXISTS idx_driver_details_status ON driver_details (status);
CREATE INDEX IF NOT EXISTS idx_area_name ON area (name);