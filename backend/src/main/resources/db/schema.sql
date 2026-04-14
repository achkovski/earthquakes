CREATE TABLE IF NOT EXISTS earthquakes (
    id BIGSERIAL PRIMARY KEY,
    external_id VARCHAR(100) NOT NULL UNIQUE,
    magnitude NUMERIC(4, 2) NOT NULL,
    mag_type VARCHAR(20),
    place VARCHAR(255),
    title VARCHAR(255),
    event_time TIMESTAMP WITH TIME ZONE NOT NULL,
    longitude NUMERIC(9, 4),
    latitude NUMERIC(9, 4),
    depth NUMERIC(7, 2)
);

CREATE INDEX IF NOT EXISTS idx_earthquakes_event_time ON earthquakes (event_time DESC);
