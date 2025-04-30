package com.project.fedfaxe.model.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingDTO {

    private String id;
    private String guestName;
    private String email;
    private String phoneNumber;
    private String gender;
    private String type;
    private String status;
    private LocalDateTime createdAt;
    private double totalPrice;

    // For stay bookings
    private LocalDate checkIn;
    private LocalDate checkOut;

    // For flight bookings
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String departureAirport;
    private String arrivalAirport;

    // For ride bookings
    private String pickupLocation;
    private String dropOffLocation;
    private String departureDate;
    private String pickupTime;
}
