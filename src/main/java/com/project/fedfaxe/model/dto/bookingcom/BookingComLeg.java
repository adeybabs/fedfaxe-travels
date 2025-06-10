package com.project.fedfaxe.model.dto.bookingcom;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingComLeg {

    private String departureTime;
    private String arrivalTime;
    private BookingComAirport departureAirport;
    private BookingComAirport arrivalAirport;
    private String cabinClass;
    private BookingComFlightInfo flightInfo;
    private List<BookingComCarrierData> carriersData;
    private int totalTime;
    private List<Object> flightStops;
}
