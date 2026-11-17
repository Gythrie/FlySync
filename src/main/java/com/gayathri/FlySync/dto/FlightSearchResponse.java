
package com.gayathri.FlySync.dto;

import java.util.List;

public class FlightSearchResponse {

    private String status;
    private String message;
    private List<FlightResponse> flights;

    public FlightSearchResponse() {
    }

    public FlightSearchResponse(
            String status,
            String message,
            List<FlightResponse> flights) {

        this.status = status;
        this.message = message;
        this.flights = flights;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<FlightResponse> getFlights() {
        return flights;
    }

    public void setFlights(List<FlightResponse> flights) {
        this.flights = flights;
    }
}
