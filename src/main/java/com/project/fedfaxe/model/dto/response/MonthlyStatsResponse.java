package com.project.fedfaxe.model.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class MonthlyStatsResponse {
    private List<String> labels;        // Month names
    private List<Long> bookingCounts;    // Booking counts per month
    private List<Double> revenues;       // Revenue per month
}
