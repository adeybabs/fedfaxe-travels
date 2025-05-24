package com.project.fedfaxe.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardStats {

    private double totalRevenue;
    private long totalBookings;
    private long pendingOrders;
    private long cancelledOrders;
    private double revenueGrowth;
    private double bookingsGrowth;
    private double pendingGrowth;
    private double cancelledGrowth;
}
