CREATE TABLE IF NOT EXISTS task_data (
    id BIGSERIAL PRIMARY KEY,
    pickup_latitude DOUBLE PRECISION NOT NULL,
    pickup_longitude DOUBLE PRECISION NOT NULL,
    dropoff_latitude DOUBLE PRECISION NOT NULL,
    dropoff_longitude DOUBLE PRECISION NOT NULL,
    description CHAR NOT NULL,
    consumer_name CHAR NOT NULL,
    venue_name CHAR NOT NULL,
    status CHAR NOT NULL
)