CREATE INDEX IF NOT EXISTS idx_courier_profile_phone_number ON courier_profile (phone_number);
CREATE INDEX IF NOT EXISTS idx_courier_profile_area_id ON courier_profile (operation_area_id);
CREATE INDEX IF NOT EXISTS idx_courier_profile_status ON courier_profile (status);
CREATE INDEX IF NOT EXISTS idx_operation_area_name ON operation_area (id);