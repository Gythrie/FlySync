
package com.gayathri.FlySync;

import com.gayathri.FlySync.adapter.AirlineAdapter;
import com.gayathri.FlySync.dto.FlightSearchRequest;
import com.gayathri.FlySync.repository.AirportRepository;
import com.gayathri.FlySync.repository.FlightSearchHistoryRepository;
import com.gayathri.FlySync.service.FlightService;
import com.gayathri.FlySync.dto.FlightFilterRequest;
import com.gayathri.FlySync.dto.FlightResponse;
import com.gayathri.FlySync.dto.FlightSearchResponse;
import com.gayathri.FlySync.adapter.IndigoAdapter;
import com.gayathri.FlySync.adapter.AirIndiaAdapter;
import java.util.ArrayList;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FlightServiceTest {

    private AirportRepository airportRepository;
    private FlightSearchHistoryRepository historyRepository;
    private FlightService flightService;

    @BeforeEach
    void setUp() {

        airportRepository = mock(AirportRepository.class);
        historyRepository = mock(FlightSearchHistoryRepository.class);

        List<AirlineAdapter> adapters = List.of();

        flightService = new FlightService(
                adapters,
                historyRepository,
                airportRepository
        );
    }

    @Test
    void shouldRejectInvalidSourceAirport() {

        FlightSearchRequest request = new FlightSearchRequest(
                "XYZ",
                "DEL",
                LocalDate.of(2026, 12, 15),
                1
        );

        when(airportRepository.existsByIataCode("XYZ"))
                .thenReturn(false);

        assertThrows(
                org.springframework.web.server.ResponseStatusException.class,
                () -> flightService.searchFlights(request)
        );

        verify(airportRepository).existsByIataCode("XYZ");
        verifyNoInteractions(historyRepository);
    }


    @Test
    void shouldRejectInvalidDestinationAirport() {

        FlightSearchRequest request = new FlightSearchRequest(
                "COK", "XYZ",
                LocalDate.of(2026, 12, 15), 1
        );

        when(airportRepository.existsByIataCode("COK"))
                .thenReturn(true);

        when(airportRepository.existsByIataCode("XYZ"))
                .thenReturn(false);

        assertThrows(
                org.springframework.web.server.ResponseStatusException.class,
                () -> flightService.searchFlights(request)
        );

        verify(airportRepository).existsByIataCode("COK");
        verify(airportRepository).existsByIataCode("XYZ");
        verifyNoInteractions(historyRepository);
    }

    @Test
    void shouldReturnUnavailableWhenNoAirlineProvidersExist() {

        FlightSearchRequest request = new FlightSearchRequest(
                "COK", "DEL",
                LocalDate.of(2026, 12, 15), 1
        );

        when(airportRepository.existsByIataCode("COK"))
                .thenReturn(true);

        when(airportRepository.existsByIataCode("DEL"))
                .thenReturn(true);

        FlightSearchResponse response =
                flightService.searchFlights(request);

        assertEquals("UNAVAILABLE", response.getStatus());

        verify(historyRepository).saveSearch(request, response);
    }

    @Test
    void shouldFilterFlightsByMaximumPriceAndSortDescending() {

        FlightResponse indigo = new FlightResponse(
                "IndiGo", "6E-201", "COK", "DEL",
                LocalDateTime.of(2026, 12, 15, 8, 30),
                LocalDateTime.of(2026, 12, 15, 11, 30),
                4500, "INR"
        );

        FlightResponse airIndia = new FlightResponse(
                "Air India", "AI-505", "COK", "DEL",
                LocalDateTime.of(2026, 12, 15, 10, 0),
                LocalDateTime.of(2026, 12, 15, 13, 15),
                6200, "INR"
        );

        FlightFilterRequest filter = new FlightFilterRequest();
        filter.setMaxPrice(5000.0);
        filter.setSortBy("PRICE_DESC");

        List<FlightResponse> result = flightService.filterFlights(
                List.of(indigo, airIndia), filter
        );

        assertEquals(1, result.size());
        assertEquals("6E-201", result.get(0).getFlightNumber());
        assertEquals(4500.0, result.get(0).getPrice());
    }
    @Test
    void shouldAggregateFlightsFromMultipleAirlines() {

        AirportRepository airportRepo = mock(AirportRepository.class);
        FlightSearchHistoryRepository historyRepo =
                mock(FlightSearchHistoryRepository.class);

        when(airportRepo.existsByIataCode("COK")).thenReturn(true);
        when(airportRepo.existsByIataCode("DEL")).thenReturn(true);

        AirlineAdapter indigo = mock(AirlineAdapter.class);
        AirlineAdapter airIndia = mock(AirlineAdapter.class);

        FlightResponse flight1 = new FlightResponse(
                "IndiGo", "6E-201", "COK", "DEL",
                LocalDateTime.of(2026, 12, 15, 8, 30),
                LocalDateTime.of(2026, 12, 15, 11, 30),
                4500, "INR"
        );

        FlightResponse flight2 = new FlightResponse(
                "Air India", "AI-505", "COK", "DEL",
                LocalDateTime.of(2026, 12, 15, 10, 0),
                LocalDateTime.of(2026, 12, 15, 13, 15),
                6200, "INR"
        );

        FlightSearchRequest request = new FlightSearchRequest(
                "COK", "DEL", LocalDate.of(2026, 12, 15), 1
        );

        when(indigo.searchFlights(request)).thenReturn(List.of(flight1));
        when(airIndia.searchFlights(request)).thenReturn(List.of(flight2));

        FlightService service = new FlightService(
                List.of(indigo, airIndia),
                historyRepo,
                airportRepo
        );

        FlightSearchResponse response = service.searchFlights(request);

        assertEquals("SUCCESS", response.getStatus());
        assertEquals(2, response.getFlights().size());

        service.shutdownExecutor();
    }

    @Test
    void shouldReturnPartialSuccessWhenOneAirlineFails() {

        AirportRepository airportRepo = mock(AirportRepository.class);
        FlightSearchHistoryRepository historyRepo =
                mock(FlightSearchHistoryRepository.class);

        when(airportRepo.existsByIataCode("COK")).thenReturn(true);
        when(airportRepo.existsByIataCode("DEL")).thenReturn(true);

        AirlineAdapter indigo = mock(AirlineAdapter.class);
        AirlineAdapter airIndia = mock(AirlineAdapter.class);

        FlightSearchRequest request = new FlightSearchRequest(
                "COK", "DEL", LocalDate.of(2026, 12, 15), 1
        );

        FlightResponse flight = new FlightResponse(
                "IndiGo", "6E-201", "COK", "DEL",
                LocalDateTime.of(2026, 12, 15, 8, 30),
                LocalDateTime.of(2026, 12, 15, 11, 30),
                4500, "INR"
        );

        when(indigo.searchFlights(request)).thenReturn(List.of(flight));

        when(airIndia.searchFlights(request))
                .thenThrow(new RuntimeException("Provider unavailable"));

        FlightService service = new FlightService(
                List.of(indigo, airIndia),
                historyRepo,
                airportRepo
        );

        FlightSearchResponse response = service.searchFlights(request);

        assertEquals("PARTIAL_SUCCESS", response.getStatus());
        assertEquals(1, response.getFlights().size());

        service.shutdownExecutor();
    }

}

