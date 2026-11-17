
package com.gayathri.FlySync.adapter;

import com.gayathri.FlySync.dto.FlightResponse;
import com.gayathri.FlySync.dto.FlightSearchRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class IndigoAdapter implements AirlineAdapter {

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
                    "6E-201", "COK", "DEL",
                    8, 30, 11, 30, 4500, 5
            ),
            new SampleFlight(
                    "6E-305", "COK", "BLR",
                    14, 0, 15, 15, 3200, 3
            ),
            new SampleFlight(
                    "6E-410", "DEL", "BOM",
                    17, 30, 19, 45, 5100, 4
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
                            "IndiGo",
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
