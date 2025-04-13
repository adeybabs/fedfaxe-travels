package com.project.fedfaxe.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AirportSearchResult {

    private String name;
    private String iataCode;
    private String city;
    private String country;
}
