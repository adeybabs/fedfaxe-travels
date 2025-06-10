package com.project.fedfaxe.model.dto.bookingcom;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingComApiResponse {

    private boolean status;
    private String message;
    private long timestamp;
    private BookingComData data;
}
