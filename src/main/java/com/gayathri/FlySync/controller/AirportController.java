
package com.gayathri.FlySync.controller;

import com.gayathri.FlySync.dto.AirportResponse;
import com.gayathri.FlySync.service.AirportService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/airports")
public class AirportController {

    private final AirportService airportService;

    public AirportController(AirportService airportService) {
        this.airportService = airportService;
    }

    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Matching airports retrieved successfully"
    )
    @GetMapping("/search")
    public List<AirportResponse> searchAirports(
            @RequestParam String keyword) {

        return airportService.searchAirports(keyword);
    }
}
