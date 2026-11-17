CREATE TABLE IF NOT EXISTS airports (
    id BIGSERIAL PRIMARY KEY,
    airport_name VARCHAR(200) NOT NULL,
    city VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL,
    iata_code VARCHAR(3) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS flight_search_history (
    id BIGSERIAL PRIMARY KEY,
    source VARCHAR(3) NOT NULL,
    destination VARCHAR(3) NOT NULL,
    departure_date DATE NOT NULL,
    passengers INTEGER NOT NULL,
    status VARCHAR(30) NOT NULL,
    result_count INTEGER NOT NULL,
    searched_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);