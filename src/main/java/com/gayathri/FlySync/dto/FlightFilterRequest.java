
package com.gayathri.FlySync.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.AssertTrue;

import java.time.LocalTime;

public class FlightFilterRequest {

    @DecimalMin(value = "0.0", inclusive = false,
            message = "Maximum price must be greater than zero")
    private Double maxPrice;

    @Size(min = 1, message = "Airline cannot be blank")
    private String airline;

    private LocalTime earliestDeparture;

    private LocalTime latestDeparture;

    @Pattern(
            regexp = "PRICE_ASC|PRICE_DESC",
            message = "sortBy must be PRICE_ASC or PRICE_DESC"
    )
    private String sortBy;

    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getAirline() {
        return airline;
    }

    public void setAirline(String airline) {
        this.airline = airline;
    }

    public LocalTime getEarliestDeparture() {
        return earliestDeparture;
    }

    public void setEarliestDeparture(LocalTime earliestDeparture) {
        this.earliestDeparture = earliestDeparture;
    }

    public LocalTime getLatestDeparture() {
        return latestDeparture;
    }

    public void setLatestDeparture(LocalTime latestDeparture) {
        this.latestDeparture = latestDeparture;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    @AssertTrue(
            message = "Earliest departure cannot be later than latest departure"
    )
    public boolean isDepartureTimeRangeValid() {
        if (earliestDeparture == null || latestDeparture == null) {
            return true;
        }

        return !earliestDeparture.isAfter(latestDeparture);
    }
}
