package com.gayathri.FlySync.model;

public class Airport {

    private Long id;
    private String airportName;
    private String city;
    private String country;
    private String iataCode;

    public Airport() {
    }

    public Airport(Long id, String airportName,
                   String city, String country, String iataCode) {
        this.id = id;
        this.airportName = airportName;
        this.city = city;
        this.country = country;
        this.iataCode = iataCode;
    }

    public Long getId() {
        return id;
    }

    public String getAirportName() {
        return airportName;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getIataCode() {
        return iataCode;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAirportName(String airportName) {
        this.airportName = airportName;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setIataCode(String iataCode) {
        this.iataCode = iataCode;
    }
}