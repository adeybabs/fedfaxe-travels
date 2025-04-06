package com.project.fedfaxe.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CitySearchResult {

    private String city;
    private String country;
    private String iataCode;
}
