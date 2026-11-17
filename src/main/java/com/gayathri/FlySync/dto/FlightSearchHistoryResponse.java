
package com.gayathri.FlySync.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class FlightSearchHistoryResponse {

    private Long id;
    private String source;
    private String destination;
    private LocalDate departureDate;
    private int passengers;
    private String status;
    private int resultCount;
    private LocalDateTime searchedAt;

    public FlightSearchHistoryResponse() {
    }

    public FlightSearchHistoryResponse(
            Long id,
            String source,
            String destination,
            LocalDate departureDate,
            int passengers,
            String status,
            int resultCount,
            LocalDateTime searchedAt) {

        this.id = id;
        this.source = source;
        this.destination = destination;
        this.departureDate = departureDate;
        this.passengers = passengers;
        this.status = status;
        this.resultCount = resultCount;
        this.searchedAt = searchedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    public int getPassengers() {
        return passengers;
    }

    public void setPassengers(int passengers) {
        this.passengers = passengers;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getResultCount() {
        return resultCount;
    }

    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }

    public LocalDateTime getSearchedAt() {
        return searchedAt;
    }

    public void setSearchedAt(LocalDateTime searchedAt) {
        this.searchedAt = searchedAt;
    }
}
