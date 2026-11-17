package com.gayathri.FlySync.dto;

public class AirportResponse {

    private String airportName;
    private String city;
    private String country;
    private String iataCode;

    public AirportResponse() {
    }

    public AirportResponse(String airportName, String city,
                           String country, String iataCode) {
        this.airportName = airportName;
        this.city = city;
        this.country = country;
        this.iataCode = iataCode;
    }

    public String getAirportName() {
        return airportName;
    }

    public void setAirportName(String airportName) {
        this.airportName = airportName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getIataCode() {
        return iataCode;
    }

    public void setIataCode(String iataCode) {
        this.iataCode = iataCode;
    }
}