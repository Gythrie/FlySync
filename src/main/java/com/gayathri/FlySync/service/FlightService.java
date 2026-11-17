
package com.gayathri.FlySync.service;

import com.gayathri.FlySync.adapter.AirlineAdapter;
import com.gayathri.FlySync.dto.FlightFilterRequest;
import com.gayathri.FlySync.dto.FlightResponse;
import com.gayathri.FlySync.dto.FlightSearchHistoryResponse;
import com.gayathri.FlySync.dto.FlightSearchRequest;
import com.gayathri.FlySync.dto.FlightSearchResponse;
import com.gayathri.FlySync.repository.AirportRepository;
import com.gayathri.FlySync.repository.FlightSearchHistoryRepository;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class FlightService {

    private static final Logger logger =
            LoggerFactory.getLogger(FlightService.class);

    private final List<AirlineAdapter> airlineAdapters;
    private final FlightSearchHistoryRepository historyRepository;
    private final AirportRepository airportRepository;

    private final ExecutorService executor =
            Executors.newFixedThreadPool(5);

    public FlightService(
            List<AirlineAdapter> airlineAdapters,
            FlightSearchHistoryRepository historyRepository,
            AirportRepository airportRepository) {

        this.airlineAdapters = airlineAdapters;
        this.historyRepository = historyRepository;
        this.airportRepository = airportRepository;
    }

    private record ProviderResult(
            List<FlightResponse> flights,
            boolean successful
    ) {}

    // Save search history and return response
    private FlightSearchResponse saveAndReturn(
            FlightSearchRequest request,
            FlightSearchResponse response) {

        historyRepository.saveSearch(request, response);
        return response;
    }

    // Search flights from all configured airline providers
    public FlightSearchResponse searchFlights(
            FlightSearchRequest request) {

        // Verify that source airport exists
        if (!airportRepository.existsByIataCode(request.getSource())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid source airport code: " + request.getSource()
            );
        }

        // Verify that destination airport exists
        if (!airportRepository.existsByIataCode(request.getDestination())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid destination airport code: " + request.getDestination()
            );
        }

        if (airlineAdapters.isEmpty()) {
            return saveAndReturn(
                    request,
                    new FlightSearchResponse(
                            "UNAVAILABLE",
                            "No airline providers are configured.",
                            List.of()
                    )
            );
        }

        // Search all airlines concurrently
        List<CompletableFuture<ProviderResult>> futures =
                airlineAdapters.stream()
                        .map(adapter -> CompletableFuture
                                .supplyAsync(
                                        () -> adapter.searchFlights(request),
                                        executor
                                )
                                .orTimeout(5, TimeUnit.SECONDS)
                                .handle((flights, exception) -> {

                                    if (exception != null) {
                                        logger.warn(
                                                "Airline search failed: {}",
                                                adapter.getClass().getSimpleName(),
                                                exception
                                        );

                                        return new ProviderResult(
                                                List.of(), false
                                        );
                                    }

                                    return new ProviderResult(
                                            flights, true
                                    );
                                }))
                        .toList();

        CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        ).join();

        // Aggregate results from all providers
        List<FlightResponse> allFlights = new ArrayList<>();
        int successfulProviders = 0;

        for (CompletableFuture<ProviderResult> future : futures) {

            ProviderResult result = future.join();

            if (result.successful()) {
                successfulProviders++;
                allFlights.addAll(result.flights());
            }
        }

        // No provider returned a successful response
        if (successfulProviders == 0) {
            return saveAndReturn(
                    request,
                    new FlightSearchResponse(
                            "UNAVAILABLE",
                            "Flight search is temporarily unavailable.",
                            List.of()
                    )
            );
        }

        // Apply customer filters and sorting after aggregation
        allFlights = filterFlights(
                allFlights,
                request.getFilter()
        );

        // No flights match the route and selected filters
        if (allFlights.isEmpty()) {
            return saveAndReturn(
                    request,
                    new FlightSearchResponse(
                            "NO_FLIGHTS",
                            "No flights match the selected route, date, and filters.",
                            List.of()
                    )
            );
        }

        // Some providers failed, but others returned results
        if (successfulProviders < airlineAdapters.size()) {
            return saveAndReturn(
                    request,
                    new FlightSearchResponse(
                            "PARTIAL_SUCCESS",
                            "Some airline providers are unavailable. Showing available results.",
                            allFlights
                    )
            );
        }

        // All providers responded successfully
        return saveAndReturn(
                request,
                new FlightSearchResponse(
                        "SUCCESS",
                        "Flights found successfully.",
                        allFlights
                )
        );
    }

    // Retrieve previous search history
    public List<FlightSearchHistoryResponse> getSearchHistory() {
        return historyRepository.findAllHistory();
    }

    // Filter and sort aggregated flight results
    public List<FlightResponse> filterFlights(
            List<FlightResponse> flights,
            FlightFilterRequest filter) {

        // No filters provided: sort by cheapest first
        if (filter == null) {
            return flights.stream()
                    .sorted((a, b) ->
                            Double.compare(a.getPrice(), b.getPrice())
                    )
                    .toList();
        }

        return flights.stream()

                // Maximum price
                .filter(flight ->
                        filter.getMaxPrice() == null ||
                                flight.getPrice() <= filter.getMaxPrice()
                )

                // Airline
                .filter(flight ->
                        filter.getAirline() == null ||
                                filter.getAirline().isBlank() ||
                                filter.getAirline().equalsIgnoreCase(
                                        flight.getAirline()
                                )
                )

                // Earliest departure
                .filter(flight ->
                        filter.getEarliestDeparture() == null ||
                                !flight.getDepartureTime().toLocalTime()
                                        .isBefore(filter.getEarliestDeparture())
                )

                // Latest departure
                .filter(flight ->
                        filter.getLatestDeparture() == null ||
                                !flight.getDepartureTime().toLocalTime()
                                        .isAfter(filter.getLatestDeparture())
                )

                // Price sorting
                .sorted((a, b) -> {
                    if ("PRICE_DESC".equalsIgnoreCase(filter.getSortBy())) {
                        return Double.compare(b.getPrice(), a.getPrice());
                    }

                    return Double.compare(a.getPrice(), b.getPrice());
                })

                .toList();
    }

    @PreDestroy
    public void shutdownExecutor() {
        executor.shutdown();
    }
}
