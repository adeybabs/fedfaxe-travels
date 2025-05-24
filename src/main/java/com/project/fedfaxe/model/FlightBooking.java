package com.project.fedfaxe.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "flight_bookings")
public class FlightBooking {

    @Id
    private String id;

    private String userId;

    // Flight details
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String duration;
    private String departureAirport;
    private String arrivalAirport;
    private String flightType;
    private String airline;
    private BigDecimal pricePerAdult;



    private Integer adults;

    private BigDecimal totalPrice;
    private BigDecimal flightFare;
    private BigDecimal taxes;
    private BigDecimal discount;
    private String travelClass; // ECONOMY, BUSINESS, FIRST

    // Baggage info
    private Integer checkedBaggageKg;
    private Boolean handBaggageIncluded;

    @Builder.Default
    private String status = "PENDING"; // Default status

    private LocalDateTime createdAt;
    private  LocalDateTime expiresAt;
    private LocalDateTime paymentConfirmedAt;

    //Extra Services
    private boolean airportLoungeSelected;
    private boolean wheelChairAssistanceSelected;
    private boolean callReminderSelected;
    private boolean travelInsuranceSelected;
    private boolean smsTicketDetailsSelected;

    private double airportLoungePrice = 25000;  // Fixed price for Airport Lounge
    private double wheelChairAssistancePrice = 0;
    private double callReminderPrice = 2000;
    private double travelInsurancePrice = 25000;
    private double smsTicketDetailsPrice = 500;


    // Guest details
    private String guestTitle;
    private String firstName;
    private String surname;
    private String middleName;
    private LocalDate dob;
    private String gender;
    private String email;
    private String phoneNumber;
    private String paymentReference;
    private String promoCode;

    public void calculateTotalPrice() {
        // Initialize totalPrice
        this.totalPrice = this.flightFare.add(this.taxes).subtract(this.discount);

        if (airportLoungeSelected) {
            this.totalPrice = this.totalPrice.add(new BigDecimal(airportLoungePrice));
        }
        if (wheelChairAssistanceSelected) {
            this.totalPrice = this.totalPrice.add(new BigDecimal(wheelChairAssistancePrice));
        }
        if (callReminderSelected) {
            this.totalPrice = this.totalPrice.add(new BigDecimal(callReminderPrice));
        }
        if (travelInsuranceSelected) {
            this.totalPrice = this.totalPrice.add(new BigDecimal(travelInsurancePrice));
        }
        if (smsTicketDetailsSelected) {
            this.totalPrice = this.totalPrice.add(new BigDecimal(smsTicketDetailsPrice));
        }
    }

}
