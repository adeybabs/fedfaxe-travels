package com.project.fedfaxe.model.dto.bookingcom;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Getter
@Service
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingComAirport {

    private String type;
    private String code;
    private String name;
    private String city;
    private String cityName;
    private String country;
    private String countryName;
}
