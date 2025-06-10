package com.project.fedfaxe.model.dto.bookingcom;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Baggage {

    private String paramName;
    private int count;
    private boolean enabled;
    private String baggageType;
}
