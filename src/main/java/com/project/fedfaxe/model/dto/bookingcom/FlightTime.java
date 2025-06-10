package com.project.fedfaxe.model.dto.bookingcom;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlightTime {

    private List<TimeCount> arrival;
    private List<TimeCount> departure;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TimeCount {
        private String start;
        private String end;
        private int count;

    }
}
