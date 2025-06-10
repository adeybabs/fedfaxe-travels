package com.project.fedfaxe.model.dto.bookingcom;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingComTravellerPrice {

    private BookingComPriceBreakdown travellerPriceBreakdown;
    private String travellerReference;
    private String travellerType;
}
