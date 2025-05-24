package com.project.fedfaxe.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "package_bookings")
public class PackageBooking {
    @Id
    private String id;

    private String userId;  // ID of the user making the booking
    private String packageId;

    @Builder.Default
    private String status = "PENDING"; // Default status

    private LocalDateTime createdAt;
    private  LocalDateTime expiresAt;
    private LocalDateTime paymentConfirmedAt;

    private double priceWithFlight;
    private double priceWithoutFlight;

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
    private String paymentReference;
}
