
package com.gayathri.FlySync.controller;

import com.gayathri.FlySync.dto.FlightSearchRequest;
import com.gayathri.FlySync.dto.FlightSearchResponse;
import com.gayathri.FlySync.service.FlightService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import com.gayathri.FlySync.dto.FlightSearchHistoryResponse;
import java.util.List;

@RestController
@RequestMapping("/api/v1/flights")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping("/test")
    public String test() {
        return "Welcome to FlySync Flight Aggregation System!";
    }

    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Flight search completed successfully"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid search request or airport code"
    )
    @PostMapping("/search")
    public FlightSearchResponse searchFlights(
            @Valid @RequestBody FlightSearchRequest request) {
        return flightService.searchFlights(request);
    }

    @GetMapping("/history")
    public List<FlightSearchHistoryResponse> getSearchHistory() {
        return flightService.getSearchHistory();
    }
}
