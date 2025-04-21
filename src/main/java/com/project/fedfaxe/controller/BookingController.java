package com.project.fedfaxe.controller;

import com.project.fedfaxe.model.StayBooking;
import com.project.fedfaxe.model.RoomCategory;
import com.project.fedfaxe.model.Stay;
import com.project.fedfaxe.model.dto.BookStayRequest;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/booking")
public class BookingController {


    private final BookingService bookingService;
    private final StayRepository stayRepository;

    @Autowired
    private PaystackService paystackService;

    public BookingController(BookingService bookingService, StayRepository stayRepository) {
        this.bookingService = bookingService;
        this.stayRepository = stayRepository;
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
    public ResponseEntity<Map<String, String>> initiateBooking(
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
        double totalPrice = days * roomCategory.getPrice();

        // Initialize payment and get payment URL
        String paymentUrl = String.valueOf(paystackService.initializeStayPayment(request, userId, totalPrice));

        Map<String, String> response = new HashMap<>();
        response.put("paymentUrl", paymentUrl);
        return ResponseEntity.ok(response);
    }







}
