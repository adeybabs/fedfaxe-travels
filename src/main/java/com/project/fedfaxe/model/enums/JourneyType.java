package com.project.fedfaxe.model.enums;

public enum JourneyType {

    ROUND_TRIP("Round Trip"),
    ONE_WAY("One way"),
    AIRPORT_TRANSFER("Airport Transfer"),
    HOURLY_RENTALS("Hourly Rentals");

    private final String value;

    JourneyType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
