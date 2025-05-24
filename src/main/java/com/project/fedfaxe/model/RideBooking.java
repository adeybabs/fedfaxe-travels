package com.project.fedfaxe.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "ride_bookings")
public class RideBooking {

    @Id
    private String id;

    private String userId;  // ID of the user making the booking
    private String rideId;

    @Builder.Default
    private String status = "PENDING"; // Default status

    private LocalDateTime createdAt;
    private  LocalDateTime expiresAt;
    private LocalDateTime paymentConfirmedAt;

    private String departureDate;
    private String pickupTime;
    private double price;

    // client details
    private String guestTitle;
    private String firstName;
    private String surname;
    private String middleName;
    private String gender;
    private String email;
    private String phoneNumber;
    private String whatsappNumber;
    private String phoneNumber2;
    private String pickupLocation;
    private String dropOffLocation;
    private String paymentReference;
    private String noteForDriver;

}
