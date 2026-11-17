
package com.gayathri.FlySync.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class FlightSearchRequest {

    @NotBlank(message = "Source airport is required")
    @Size(min = 3, max = 3, message = "Source must be a 3-letter IATA code")
    private String source;

    @NotBlank(message = "Destination airport is required")
    @Size(min = 3, max = 3, message = "Destination must be a 3-letter IATA code")
    private String destination;

    @NotNull(message = "Departure date is required")
    @FutureOrPresent(message = "Departure date cannot be in the past")
    private LocalDate departureDate;

    @Min(value = 1, message = "At least one passenger is required")
    @Max(value = 9, message = "Maximum 9 passengers allowed")
    private int passengers;

    @Valid
    private FlightFilterRequest filter;

    public FlightSearchRequest() {
    }

    public FlightSearchRequest(String source, String destination,
                               LocalDate departureDate, int passengers) {
        this.source = source;
        this.destination = destination;
        this.departureDate = departureDate;
        this.passengers = passengers;
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

    public FlightFilterRequest getFilter() {
        return filter;
    }

    public void setFilter(FlightFilterRequest filter) {
        this.filter = filter;
    }

    @AssertTrue(message = "Source and destination must be different")
    public boolean isRouteValid() {
        if (source == null || destination == null) {
            return true;
        }

        return !source.equalsIgnoreCase(destination);
    }
}
