
package com.gayathri.FlySync.adapter;

import com.gayathri.FlySync.dto.FlightResponse;
import com.gayathri.FlySync.dto.FlightSearchRequest;

import java.util.List;

public interface AirlineAdapter {

    List<FlightResponse> searchFlights(FlightSearchRequest request);

}
