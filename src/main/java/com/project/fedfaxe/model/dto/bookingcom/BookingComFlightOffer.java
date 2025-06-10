package com.project.fedfaxe.model.dto.bookingcom;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingComFlightOffer {

    private String token;
    private List<BookingComSegment> segments;
    private BookingComPriceBreakdown priceBreakdown;
    private List<BookingComTravellerPrice> travellerPrices;
    private String tripType;
    private BookingComBrandedFareInfo brandedFareInfo;
}
