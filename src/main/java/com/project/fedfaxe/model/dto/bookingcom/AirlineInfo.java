package com.project.fedfaxe.model.dto.bookingcom;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AirlineInfo {
    private String name;
    private String logoUrl;
    private String iataCode;
    private int count;
    private MinPrice minPrice;

}
