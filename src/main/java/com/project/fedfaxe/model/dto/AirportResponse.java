package com.project.fedfaxe.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AirportResponse {

    private String name;
    private String iataCode;
    private String fullLocation;
}
