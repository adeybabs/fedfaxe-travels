package com.project.fedfaxe.controller;

import com.project.fedfaxe.model.dto.BookingDTO;
import com.project.fedfaxe.model.dto.DashboardStats;
import com.project.fedfaxe.model.dto.response.MonthlyStatsResponse;
import com.project.fedfaxe.service.AdminDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Dashboard", description = "Endpoints for Admin Dashboard statistics and recent bookings")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @Operation(summary = "Get Stay Dashboard Stats", description = "Retrieves dashboard statistics for stay bookings.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved stay dashboard stats")
    @GetMapping("/stays/dashboard")
    public ResponseEntity<DashboardStats> getStayDashboardStats() {
        return ResponseEntity.ok(adminDashboardService.getStayDashboardStats());
    }

    @Operation(summary = "Get Flight Dashboard Stats", description = "Retrieves dashboard statistics for flight bookings.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved flight dashboard stats")
    @GetMapping("/flights/dashboard")
    public ResponseEntity<DashboardStats> getFlightDashboardStats() {
        return ResponseEntity.ok(adminDashboardService.getFlightDashboardStats());
    }

    @Operation(summary = "Get Ride Dashboard Stats", description = "Retrieves dashboard statistics for ride bookings.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved ride dashboard stats")
    @GetMapping("/rides/dashboard")
    public ResponseEntity<DashboardStats> getRideDashboardStats() {
        return ResponseEntity.ok(adminDashboardService.getRideDashboardStats());
    }

    @Operation(summary = "Get Stay Monthly Stats", description = "Retrieves monthly statistics for stays.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved stay monthly stats")
    @GetMapping("/stays/monthly-stats")
    public ResponseEntity<MonthlyStatsResponse> getStayMonthlyStats(
            @Parameter(description = "Number of months to retrieve stats for")
            @RequestParam(defaultValue = "4") int months) {
        return ResponseEntity.ok(adminDashboardService.getStayMonthlyStats(months));
    }

    @Operation(summary = "Get Flight Monthly Stats", description = "Retrieves monthly statistics for flights.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved flight monthly stats")
    @GetMapping("/flights/monthly-stats")
    public ResponseEntity<MonthlyStatsResponse> getFlightMonthlyStats(
            @Parameter(description = "Number of months to retrieve stats for")
            @RequestParam(defaultValue = "4") int months) {
        return ResponseEntity.ok(adminDashboardService.getFlightMonthlyStats(months));
    }

    @Operation(summary = "Get Ride Monthly Stats", description = "Retrieves monthly statistics for rides.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved ride monthly stats")
    @GetMapping("/rides/monthly-stats")
    public ResponseEntity<MonthlyStatsResponse> getRideMonthlyStats(
            @Parameter(description = "Number of months to retrieve stats for")
            @RequestParam(defaultValue = "4") int months) {
        return ResponseEntity.ok(adminDashboardService.getRideMonthlyStats(months));
    }

    @Operation(summary = "Get Recent Stay Bookings", description = "Retrieves recent stay bookings with pagination.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved recent stay bookings")
    @GetMapping("/stays/recent-bookings")
    public ResponseEntity<Page<BookingDTO>> getRecentStayBookings(
            @Parameter(description = "Page number for pagination") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size for pagination") @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(adminDashboardService.getRecentStayBookings(page, size));
    }

    @Operation(summary = "Get Recent Flight Bookings", description = "Retrieves recent flight bookings with pagination.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved recent flight bookings")
    @GetMapping("/flights/recent-bookings")
    public ResponseEntity<Page<BookingDTO>> getRecentFlightBookings(
            @Parameter(description = "Page number for pagination") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size for pagination") @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(adminDashboardService.getRecentFlightBookings(page, size));
    }

    @Operation(summary = "Get Recent Ride Bookings", description = "Retrieves recent ride bookings with pagination.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved recent ride bookings")
    @GetMapping("/rides/recent-bookings")
    public ResponseEntity<Page<BookingDTO>> getRecentRideBookings(
            @Parameter(description = "Page number for pagination") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size for pagination") @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(adminDashboardService.getRecentRideBookings(page, size));
    }

    @Operation(summary = "Get Combined Dashboard Stats", description = "Retrieves combined dashboard statistics for stays, flights, and rides.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved combined dashboard stats")
    @GetMapping("/combined/dashboard")
    public ResponseEntity<DashboardStats> getCombinedDashboardStats() {
        DashboardStats stayStats = adminDashboardService.getStayDashboardStats();
        DashboardStats flightStats = adminDashboardService.getFlightDashboardStats();
        DashboardStats rideStats = adminDashboardService.getRideDashboardStats();

        DashboardStats combinedStats = DashboardStats.builder()
                .totalRevenue(stayStats.getTotalRevenue() + flightStats.getTotalRevenue() + rideStats.getTotalRevenue())
                .totalBookings(stayStats.getTotalBookings() + flightStats.getTotalBookings() + rideStats.getTotalBookings())
                .pendingOrders(stayStats.getPendingOrders() + flightStats.getPendingOrders() + rideStats.getPendingOrders())
                .cancelledOrders(stayStats.getCancelledOrders() + flightStats.getCancelledOrders() + rideStats.getCancelledOrders())
                .build();

        return ResponseEntity.ok(combinedStats);
    }

    @Operation(summary = "Get Combined Monthly Stats", description = "Retrieves combined monthly statistics for stays, flights, and rides.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved combined monthly stats")
    @GetMapping("/combined/monthly-stats")
    public ResponseEntity<MonthlyStatsResponse> getCombinedMonthlyStats(
            @Parameter(description = "Number of months to retrieve stats for")
            @RequestParam(defaultValue = "4") int months) {
        MonthlyStatsResponse stayStats = adminDashboardService.getStayMonthlyStats(months);
        MonthlyStatsResponse flightStats = adminDashboardService.getFlightMonthlyStats(months);
        MonthlyStatsResponse rideStats = adminDashboardService.getRideMonthlyStats(months);

        MonthlyStatsResponse combinedStats = new MonthlyStatsResponse();
        combinedStats.setLabels(stayStats.getLabels());

        List<Long> combinedBookingCounts = new ArrayList<>();
        List<Double> combinedRevenues = new ArrayList<>();

        for (int i = 0; i < stayStats.getLabels().size(); i++) {
            Long bookings = stayStats.getBookingCounts().get(i) +
                    flightStats.getBookingCounts().get(i) +
                    rideStats.getBookingCounts().get(i);

            Double revenue = stayStats.getRevenues().get(i) +
                    flightStats.getRevenues().get(i) +
                    rideStats.getRevenues().get(i);

            combinedBookingCounts.add(bookings);
            combinedRevenues.add(revenue);
        }

        combinedStats.setBookingCounts(combinedBookingCounts);
        combinedStats.setRevenues(combinedRevenues);

        return ResponseEntity.ok(combinedStats);
    }
}
