
package com.gayathri.FlySync.service;

import com.gayathri.FlySync.dto.AirportResponse;
import com.gayathri.FlySync.model.Airport;
import com.gayathri.FlySync.repository.AirportRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AirportService {

    private final AirportRepository airportRepository;

    public AirportService(AirportRepository airportRepository) {
        this.airportRepository = airportRepository;
    }

    public List<AirportResponse> searchAirports(String keyword) {

        List<Airport> airports =
                airportRepository.searchByKeyword(keyword);

        return airports.stream()
                .map(airport -> new AirportResponse(
                        airport.getAirportName(),
                        airport.getCity(),
                        airport.getCountry(),
                        airport.getIataCode()
                ))
                .toList();
    }
}
