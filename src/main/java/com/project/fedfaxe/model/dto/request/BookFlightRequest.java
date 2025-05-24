package com.project.fedfaxe.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookFlightRequest {

    // Guest details
    @NotBlank(message = "Guest title is required")
    private String title;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Surname is required")
    private String surname;

    private String middleName;

    @NotNull(message = "Date of Birth is required")
    private LocalDate dob;

    @NotBlank(message = "Gender is required")
    private String gender;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    private Boolean airportLoungeSelected;
    private Boolean wheelChairAssistanceSelected;
    private Boolean callReminderSelected;
    private Boolean travelInsuranceSelected;
    private Boolean smsTicketDetailsSelected;

    // Flight details
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String duration;
    private String departureAirport;
    private String arrivalAirport;
    private String flightType;
    private String airline;
    private String pricePerAdult;
}
