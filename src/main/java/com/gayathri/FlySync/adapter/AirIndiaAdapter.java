
package com.gayathri.FlySync.adapter;

import com.gayathri.FlySync.dto.FlightResponse;
import com.gayathri.FlySync.dto.FlightSearchRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class AirIndiaAdapter implements AirlineAdapter {

    private record SampleFlight(
            String flightNumber,
            String source,
            String destination,
            int departureHour,
            int departureMinute,
            int arrivalHour,
            int arrivalMinute,
            double price,
            int availableSeats
    ) {}

    private final List<SampleFlight> inventory = List.of(
            new SampleFlight(
                    "AI-505", "COK", "DEL",
                    10, 0, 13, 15, 6200, 6
            ),
            new SampleFlight(
                    "AI-620", "COK", "BLR",
                    9, 15, 10, 30, 3800, 2
            ),
            new SampleFlight(
                    "AI-715", "DEL", "BOM",
                    15, 0, 17, 10, 4800, 5
            )
    );

    @Override
    public List<FlightResponse> searchFlights(
            FlightSearchRequest request) {

        return inventory.stream()
                .filter(flight ->
                        flight.source().equalsIgnoreCase(request.getSource())
                )
                .filter(flight ->
                        flight.destination().equalsIgnoreCase(request.getDestination())
                )
                .filter(flight ->
                        flight.availableSeats() >= request.getPassengers()
                )
                .map(flight -> {

                    LocalDate date = request.getDepartureDate();

                    return new FlightResponse(
                            "Air India",
                            flight.flightNumber(),
                            flight.source(),
                            flight.destination(),
                            date.atTime(
                                    flight.departureHour(),
                                    flight.departureMinute()
                            ),
                            date.atTime(
                                    flight.arrivalHour(),
                                    flight.arrivalMinute()
                            ),
                            flight.price(),
                            "INR"
                    );
                })
                .toList();
    }
}
