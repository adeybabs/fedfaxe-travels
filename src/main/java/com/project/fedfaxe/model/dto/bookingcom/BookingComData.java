package com.project.fedfaxe.model.dto.bookingcom;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.project.fedfaxe.model.dto.response.FlightSearchResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingComData {

    private Aggregation aggregation;
    private List<BookingComFlightOffer> flightOffers;
    private ErrorDetails error;
    private String errorMessage;
}
