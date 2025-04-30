package com.project.fedfaxe.model.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookRideRequest {


    private String rideId;
    private String departureDate;
    private String pickupTime;
    private double price;
    private String pricingInfo;
    private String currency;

    @NotBlank(message = "Pickup location is required")
    private String pickupLocation;

    @NotBlank(message = "DropOff is required")
    private String dropOffLocation;


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

    private String noteForDriver;

    @AssertTrue(message = "You must accept the terms and conditions")
    private Boolean termsAndConditions;

}
