package com.project.fedfaxe.model.dto.bookingcom;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingComPriceBreakdown {

    private MinPrice total;
    private MinPrice baseFare;
    private MinPrice tax;
    private MinPrice totalRounded;
}
