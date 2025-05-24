package com.project.fedfaxe.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "stay_bookings")
public class StayBooking {

    @Id
    private String id;

    private String userId;  // ID of the user making the booking
    private String stayId;  // ID of the Stay being booked
    private String roomCategoryId; // Room type booked

    private LocalDate checkIn;
    private LocalDate checkOut;
    private double totalPrice;

    @Builder.Default
    private String status = "PENDING"; // Default status

    private LocalDateTime createdAt;
    private  LocalDateTime expiresAt;
    private LocalDateTime paymentConfirmedAt;

    // Guest details
    private String guestTitle;
    private String firstName;
    private String surname;
    private String middleName;
    private String gender;
    private String email;
    private String phoneNumber;
    private String whatsappNumber;
    private String phoneNumber2;

    // Extra options
    private String specialRequests;
    private String paymentReference;
}
