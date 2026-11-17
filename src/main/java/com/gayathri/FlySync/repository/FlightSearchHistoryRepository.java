
package com.gayathri.FlySync.repository;

import com.gayathri.FlySync.dto.FlightSearchRequest;
import com.gayathri.FlySync.dto.FlightSearchResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.gayathri.FlySync.dto.FlightSearchHistoryResponse;
import java.util.List;

@Repository
public class FlightSearchHistoryRepository {

    private final JdbcTemplate jdbcTemplate;

    public FlightSearchHistoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveSearch(
            FlightSearchRequest request,
            FlightSearchResponse response) {

        String sql = """
                INSERT INTO flight_search_history
                (source, destination, departure_date,
                 passengers, status, result_count)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        int resultCount = response.getFlights() == null
                ? 0
                : response.getFlights().size();

        jdbcTemplate.update(
                sql,
                request.getSource(),
                request.getDestination(),
                request.getDepartureDate(),
                request.getPassengers(),
                response.getStatus(),
                resultCount
        );
    }


    public List<FlightSearchHistoryResponse> findAllHistory() {

        String sql = """
            SELECT id, source, destination, departure_date,
                   passengers, status, result_count, searched_at
            FROM flight_search_history
            ORDER BY searched_at DESC
            """;

        return jdbcTemplate.query(
                sql,
                (resultSet, rowNumber) ->
                        new FlightSearchHistoryResponse(
                                resultSet.getLong("id"),
                                resultSet.getString("source"),
                                resultSet.getString("destination"),
                                resultSet.getDate("departure_date").toLocalDate(),
                                resultSet.getInt("passengers"),
                                resultSet.getString("status"),
                                resultSet.getInt("result_count"),
                                resultSet.getTimestamp("searched_at").toLocalDateTime()
                        )
        );
    }

}

