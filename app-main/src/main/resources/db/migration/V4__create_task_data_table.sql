CREATE TABLE IF NOT EXISTS task_data (
    id BIGSERIAL PRIMARY KEY,
    pickup_latitude DOUBLE PRECISION NOT NULL,
    pickup_longitude DOUBLE PRECISION NOT NULL,
    dropoff_latitude DOUBLE PRECISION NOT NULL,
    dropoff_longitude DOUBLE PRECISION NOT NULL,
    task_note VARCHAR NOT NULL,
    consumer_name VARCHAR NOT NULL,
    venue_name VARCHAR NOT NULL,
    task_status VARCHAR NOT NULL
)