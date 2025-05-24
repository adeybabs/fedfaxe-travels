package com.project.fedfaxe.service;

import com.project.fedfaxe.model.FlightBooking;
import com.project.fedfaxe.model.RideBooking;
import com.project.fedfaxe.model.StayBooking;
import com.project.fedfaxe.model.dto.BookingDTO;
import com.project.fedfaxe.model.dto.DashboardStats;
import com.project.fedfaxe.model.dto.response.MonthlyStatsResponse;
import com.project.fedfaxe.model.enums.BookingStatus;
import com.project.fedfaxe.repository.FlightBookingRepository;
import com.project.fedfaxe.repository.RideBookingRepository;
import com.project.fedfaxe.repository.StayBookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.bson.Document;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final StayBookingRepository stayBookingRepository;
    private final FlightBookingRepository flightBookingRepository;
    private final RideBookingRepository rideBookingRepository;

    public DashboardStats getStayDashboardStats() {
        return getDashboardStats(
                stayBookingRepository::count,
                () -> stayBookingRepository.countByStatus(BookingStatus.PENDING_PAYMENT.name()),
                () -> stayBookingRepository.countByStatus(BookingStatus.CANCELLED.name()),
                () -> stayBookingRepository.sumTotalRevenueByStatus(BookingStatus.CONFIRMED.name())
        );
    }

    public DashboardStats getFlightDashboardStats() {
        return getDashboardStats(
                flightBookingRepository::count,
                () -> flightBookingRepository.countByStatus(BookingStatus.PENDING_PAYMENT.name()),
                () -> flightBookingRepository.countByStatus(BookingStatus.CANCELLED.name()),
                () -> {
                    Double revenue = flightBookingRepository.sumTotalRevenueByStatus(BookingStatus.CONFIRMED.name());
                    return revenue != null ? revenue : 0.0;
                }
        );
    }

    public DashboardStats getRideDashboardStats() {
        return getDashboardStats(
                rideBookingRepository::count,
                () -> rideBookingRepository.countByStatus(BookingStatus.PENDING_PAYMENT.name()),
                () -> rideBookingRepository.countByStatus(BookingStatus.CANCELLED.name()),
                () -> rideBookingRepository.sumTotalRevenueByStatus(BookingStatus.CONFIRMED.name())
        );
    }

    private DashboardStats getDashboardStats(
            Supplier<Long> totalBookingsSupplier,
            Supplier<Long> pendingOrdersSupplier,
            Supplier<Long> cancelledOrdersSupplier,
            Supplier<Double> totalRevenueSupplier
    ) {
        Long totalBookings = totalBookingsSupplier.get();
        Long pendingOrders = pendingOrdersSupplier.get();
        Long cancelledOrders = cancelledOrdersSupplier.get();
        Double totalRevenue = totalRevenueSupplier.get();

        // Get last month data for comparison
        LocalDateTime startOfLastMonth = LocalDateTime.now().minusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfLastMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).minusSeconds(1);

        // Calculate growth rates compared to last month
        // Implementation of growth calculations will depend on your specific repository methods
        double revenueGrowth = 0.0;
        double bookingsGrowth = 0.0;
        double pendingGrowth = 0.0;
        double cancelledGrowth = 0.0;

        return DashboardStats.builder()
                .totalRevenue(totalRevenue != null ? totalRevenue : 0.0)
                .totalBookings(totalBookings)
                .pendingOrders(pendingOrders)
                .cancelledOrders(cancelledOrders)
                .revenueGrowth(revenueGrowth)
                .bookingsGrowth(bookingsGrowth)
                .pendingGrowth(pendingGrowth)
                .cancelledGrowth(cancelledGrowth)
                .build();
    }

    public MonthlyStatsResponse getStayMonthlyStats(int months) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(months - 1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);

        List<Document> monthlyStats = stayBookingRepository.getMonthlyStatsAggregation(startDate, endDate);
        return processMonthlyStats(monthlyStats, months);
    }

    public MonthlyStatsResponse getFlightMonthlyStats(int months) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(months - 1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);

        List<Document> monthlyStats = flightBookingRepository.getMonthlyStatsAggregation(startDate, endDate);
        return processMonthlyStats(monthlyStats, months);
    }

    public MonthlyStatsResponse getRideMonthlyStats(int months) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(months - 1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);

        List<Document> monthlyStats = rideBookingRepository.getMonthlyStatsAggregation(startDate, endDate);
        return processMonthlyStats(monthlyStats, months);
    }

    private MonthlyStatsResponse processMonthlyStats(List<Document> monthlyStats, int months) {
        // Initialize arrays
        List<String> labels = new ArrayList<>();
        List<Long> bookingCounts = new ArrayList<>();
        List<Double> revenues = new ArrayList<>();

        // Create a map to easily look up month data
        Map<Integer, Document> monthDataMap = new HashMap<>();
        for (Document doc : monthlyStats) {
            Document idDoc = (Document) doc.get("_id");
            int month = idDoc.getInteger("month");
            monthDataMap.put(month, doc);
        }

        // Fill data for the last N months
        LocalDate now = LocalDate.now();
        for (int i = months - 1; i >= 0; i--) {
            LocalDate date = now.minusMonths(i);
            int monthValue = date.getMonthValue();
            String monthName = date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

            labels.add(monthName);

            Document monthData = monthDataMap.get(monthValue);
            if (monthData != null) {
                bookingCounts.add(Long.valueOf(monthData.getInteger("count")));
                revenues.add(monthData.getDouble("revenue"));
            } else {
                bookingCounts.add(0L);
                revenues.add(0.0);
            }
        }

        MonthlyStatsResponse response = new MonthlyStatsResponse();
        response.setLabels(labels);
        response.setBookingCounts(bookingCounts);
        response.setRevenues(revenues);

        return response;
    }

    public Page<BookingDTO> getRecentStayBookings(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return stayBookingRepository.findAll(pageRequest).map(this::convertToStayBookingDTO);
    }

    public Page<BookingDTO> getRecentFlightBookings(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return flightBookingRepository.findAll(pageRequest).map(this::convertToFlightBookingDTO);
    }

    public Page<BookingDTO> getRecentRideBookings(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return rideBookingRepository.findAll(pageRequest).map(this::convertToRideBookingDTO);
    }

    private BookingDTO convertToStayBookingDTO(StayBooking booking) {
        return BookingDTO.builder()
                .id(booking.getId())
                .guestName(booking.getFirstName() + " " + booking.getSurname())
                .email(booking.getEmail())
                .phoneNumber(booking.getPhoneNumber())
                .gender(booking.getGender())
                .type("Stay")
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .totalPrice(booking.getTotalPrice())
                .checkIn(booking.getCheckIn())
                .checkOut(booking.getCheckOut())
                .build();
    }

    private BookingDTO convertToFlightBookingDTO(FlightBooking booking) {
        return BookingDTO.builder()
                .id(booking.getId())
                .guestName(booking.getFirstName() + " " + booking.getSurname())
                .email(booking.getEmail())
                .phoneNumber(booking.getPhoneNumber())
                .gender(booking.getGender())
                .type("Flight")
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .totalPrice(booking.getTotalPrice() != null ? booking.getTotalPrice().doubleValue() : 0.0)
                .departureTime(booking.getDepartureTime())
                .arrivalTime(booking.getArrivalTime())
                .departureAirport(booking.getDepartureAirport())
                .arrivalAirport(booking.getArrivalAirport())
                .build();
    }

    private BookingDTO convertToRideBookingDTO(RideBooking booking) {
        return BookingDTO.builder()
                .id(booking.getId())
                .guestName(booking.getFirstName() + " " + booking.getSurname())
                .email(booking.getEmail())
                .phoneNumber(booking.getPhoneNumber())
                .gender(booking.getGender())
                .type("Ride")
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .totalPrice(booking.getPrice())
                .pickupLocation(booking.getPickupLocation())
                .dropOffLocation(booking.getDropOffLocation())
                .departureDate(booking.getDepartureDate())
                .pickupTime(booking.getPickupTime())
                .build();
    }

}

