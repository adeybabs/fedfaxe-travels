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
public class BookPackageRequest {
    private String packageId;
    private double priceWithFlights;
    private double priceWithoutFlights;
    private String currency;

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
    private String specialRequests;

    @AssertTrue(message = "You must accept the terms and conditions")
    private Boolean termsAndConditions;
}
