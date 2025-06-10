package com.project.fedfaxe.model.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlightSearchLocalResponse {

    private String airline;
    private String airlineLogo;
    private String departureAirport;
    private String arrivalAirport;
    private String departureTime;
    private String arrivalTime;
    private String duration;
    private String price;
    private String currency;
    private String travelClass;
    private int stops;
}
