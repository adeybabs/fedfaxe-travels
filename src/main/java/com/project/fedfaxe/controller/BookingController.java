package com.project.fedfaxe.controller;

import com.project.fedfaxe.model.*;
import com.project.fedfaxe.model.dto.request.BookFlightRequest;
import com.project.fedfaxe.model.dto.request.BookPackageRequest;
import com.project.fedfaxe.model.dto.request.BookRideRequest;
import com.project.fedfaxe.model.dto.request.BookStayRequest;
import com.project.fedfaxe.model.dto.response.InitializePaymentResponse;
import com.project.fedfaxe.repository.PackageProductRepository;
import com.project.fedfaxe.repository.RideProductRepository;
import com.project.fedfaxe.repository.StayRepository;
import com.project.fedfaxe.service.BookingService;
import com.project.fedfaxe.service.PaystackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/booking")
public class BookingController {


    private final StayRepository stayRepository;
    private final RideProductRepository rideRepository;
    private final PackageProductRepository packageRepository;
    private final PaystackService paystackService;

    public BookingController(StayRepository stayRepository, RideProductRepository rideRepository, PackageProductRepository packageRepository, PaystackService paystackService) {
        this.stayRepository = stayRepository;
        this.rideRepository = rideRepository;
        this.packageRepository = packageRepository;
        this.paystackService = paystackService;
    }


    @Operation(summary = "Book a stay", description = "Creates a new booking for an authenticated OAuth2 user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Booking successfully created",
                    content = @Content(schema = @Schema(implementation = StayBooking.class))),
            @ApiResponse(responseCode = "401", description = "User not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/stay")
    public ResponseEntity<Map<String, String>> initiateStayBooking(
            @Valid @RequestBody BookStayRequest request,
            Authentication authentication
    ) {
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        String userId = ((JwtAuthenticationToken) authentication).getToken().getSubject();

        // Calculate totalPrice - you already have this logic
        Stay stay = stayRepository.findById(request.getStayId())
                .orElseThrow(() -> new RuntimeException("Stay not found"));

        RoomCategory roomCategory = stay.getRoomCategories().stream()
                .filter(room -> room.getId().equals(request.getRoomCategoryId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Room category not found"));

        long days = ChronoUnit.DAYS.between(request.getCheckIn(), request.getCheckOut());
        //double totalPrice = days * roomCategory.getPrice();

        // Initialize payment and get payment URL
        InitializePaymentResponse paymentResponse = paystackService.initializeStayPayment(request, userId);

        Map<String, String> response = new HashMap<>();
        response.put("paymentUrl", paymentResponse.getAuthorizationUrl());
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Book a flight", description = "Creates a new flight booking for an authenticated OAuth2 user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Booking successfully created",
                    content = @Content(schema = @Schema(implementation = StayBooking.class))),
            @ApiResponse(responseCode = "401", description = "User not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/flight")
    public ResponseEntity<Map<String, String>> initiateFlightBooking(
            @Valid @RequestBody BookFlightRequest request,
            Authentication authentication
    ) {
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        String userId = ((JwtAuthenticationToken) authentication).getToken().getSubject();
        BigDecimal pricePerAdult = new BigDecimal(request.getPricePerAdult().replace("NGN", "").trim());

        // Build the Flight object using Lombok's builder
        Flight selectedFlight = Flight.builder()
                .departureTime(request.getDepartureTime())
                .arrivalTime(request.getArrivalTime())
                .duration(request.getDuration())
                .departureAirport(request.getDepartureAirport())
                .arrivalAirport(request.getArrivalAirport())
                .flightType(request.getFlightType())
                .airline(request.getAirline())
                .pricePerAdult(pricePerAdult)
                .build();

        FlightBooking flightBooking = FlightBooking.builder()
                .userId(userId)
                .departureTime(request.getDepartureTime())
                .arrivalTime(request.getArrivalTime())
                .duration(request.getDuration())
                .departureAirport(request.getDepartureAirport())
                .arrivalAirport(request.getArrivalAirport())
                .flightType(request.getFlightType())
                .airline(request.getAirline())
                .pricePerAdult(pricePerAdult)
                .flightFare(pricePerAdult)  // Assume the base fare is the same as the pricePerAdult for simplicity
                .taxes(BigDecimal.ZERO)     // Set taxes as per your logic
                .discount(BigDecimal.ZERO)  // Set discount as per your logic
                .airportLoungeSelected(request.getAirportLoungeSelected())
                .wheelChairAssistanceSelected(request.getWheelChairAssistanceSelected())
                .callReminderSelected(request.getCallReminderSelected())
                .travelInsuranceSelected(request.getTravelInsuranceSelected())
                .smsTicketDetailsSelected(request.getSmsTicketDetailsSelected())
                .build();

        flightBooking.calculateTotalPrice();

        // Initialize payment and get payment URL
        InitializePaymentResponse paymentResponse = paystackService.initializeFlightPayment(request, userId);

        Map<String, String> response = new HashMap<>();
        response.put("paymentUrl", paymentResponse.getAuthorizationUrl());
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Book a ride", description = "Creates a new booking for an authenticated OAuth2 user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Booking successfully created",
                    content = @Content(schema = @Schema(implementation = StayBooking.class))),
            @ApiResponse(responseCode = "401", description = "User not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/ride")
    public ResponseEntity<Map<String, String>> initiateRideBooking(
            @Valid @RequestBody BookRideRequest request,
            Authentication authentication
    ) {
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        String userId = ((JwtAuthenticationToken) authentication).getToken().getSubject();

        RideProduct ride = rideRepository.findById(request.getRideId())
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        // Initialize payment and get payment URL
        InitializePaymentResponse paymentResponse = paystackService.initializeRidePayment(request, userId);

        Map<String, String> response = new HashMap<>();
        response.put("paymentUrl", paymentResponse.getAuthorizationUrl());
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Book a package", description = "Creates a new booking for an authenticated OAuth2 user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Booking successfully created",
                    content = @Content(schema = @Schema(implementation = StayBooking.class))),
            @ApiResponse(responseCode = "401", description = "User not authenticated",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/package")
    public ResponseEntity<Map<String, String>> initiatePackageBooking(
            @Valid @RequestBody BookPackageRequest request,
            Authentication authentication
    ) {
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        String userId = ((JwtAuthenticationToken) authentication).getToken().getSubject();

        PackageProduct packages = packageRepository.findById(request.getPackageId())
                .orElseThrow(() -> new RuntimeException("Package not found"));

        // Initialize payment and get payment URL
        InitializePaymentResponse paymentResponse = paystackService.initializePackagePayment(request, userId);

        Map<String, String> response = new HashMap<>();
        response.put("paymentUrl", paymentResponse.getAuthorizationUrl());
        return ResponseEntity.ok(response);
    }

}
