package com.project.fedfaxe.model.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookStayRequest {

    @NotBlank(message = "Stay ID is required")
    private String stayId;

    @NotBlank(message = "Room category ID is required")
    private String roomCategoryId;

    @FutureOrPresent(message = "Check-in date must be today or in the future")
    private LocalDate checkIn;

    @Future(message = "Check-out date must be in the future")
    private LocalDate checkOut;

    // Guest details
    @NotBlank(message = "Guest title is required")
    private String title;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Surname is required")
    private String surname;

    private String middleName;

    @NotBlank(message = "Gender is required")
    private String gender;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    private String whatsappNumber;
    private String phoneNumber2;

    // Additional fields
    private String pickupLocation;
    private String dropoffLocation;
    private String specialRequests;

    @AssertTrue(message = "You must accept the terms and conditions")
    private Boolean termsAndConditions;

}
