package com.gayathri.FlySync.repository;

import com.gayathri.FlySync.model.Airport;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AirportRepository {

    private final JdbcTemplate jdbcTemplate;

    public AirportRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Airport> searchByKeyword(String keyword) {

        String sql = """
                SELECT id, airport_name, city, country, iata_code
                FROM airports
                WHERE airport_name ILIKE ?
                   OR city ILIKE ?
                   OR country ILIKE ?
                   OR iata_code ILIKE ?
                ORDER BY city, airport_name
                """;

        String pattern = "%" + keyword.trim() + "%";

        return jdbcTemplate.query(
                sql,
                (resultSet, rowNumber) -> new Airport(
                        resultSet.getLong("id"),
                        resultSet.getString("airport_name"),
                        resultSet.getString("city"),
                        resultSet.getString("country"),
                        resultSet.getString("iata_code")
                ),
                pattern, pattern, pattern, pattern
        );
    }

    public boolean existsByIataCode(String iataCode) {

        String sql = """
            SELECT COUNT(*)
            FROM airports
            WHERE UPPER(iata_code) = UPPER(?)
            """;

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                iataCode
        );

        return count != null && count > 0;
    }
}
