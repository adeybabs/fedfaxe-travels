package com.project.fedfaxe.model.dto.bookingcom;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Aggregation {
    private List<AirlineInfo> airlines;
    private List<StopInfo> stops;
    private int totalCount;
    private int filteredTotalCount;
    private List<DepartureInterval> departureIntervals;
    private List<FlightTime> flightTimes;
    private ShortLayoverConnection shortLayoverConnection;
    private int durationMin;
    private int durationMax;
    private MinPrice minPrice;
    private MinPrice minRoundPrice;
    private MinPrice minPriceFiltered;
    private List<Baggage> baggage;
    private Budget budget;
    private Budget budgetPerAdult;
    private List<DurationRange> duration;
    private List<String> filtersOrder;
}
