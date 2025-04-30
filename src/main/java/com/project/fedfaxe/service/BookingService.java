package com.project.fedfaxe.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.project.fedfaxe.model.*;
import com.project.fedfaxe.model.dto.request.BookFlightRequest;
import com.project.fedfaxe.model.dto.request.BookRideRequest;
import com.project.fedfaxe.model.dto.request.BookStayRequest;
import com.project.fedfaxe.model.enums.BookingStatus;
import com.project.fedfaxe.repository.BookingRepository;
import com.project.fedfaxe.repository.RideProductRepository;
import com.project.fedfaxe.repository.StayRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final MongoTemplate mongoTemplate;
    private final StayRepository stayRepository;
    private final BookingRepository bookingRepository;
    //private final EmailService emailService;
    private final RideProductRepository rideRepository;

    public StayBooking bookStay(String userId, String stayId, String roomCategoryId, LocalDate checkIn,
                                LocalDate checkOut, JsonNode metadata) {

        Stay stay = stayRepository.findById(stayId)
                .orElseThrow(() -> new RuntimeException("Stay not found"));

        RoomCategory roomCategory = stay.getRoomCategories().stream()
                .filter(room -> room.getId().equals(roomCategoryId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Room category not found"));

        long days = ChronoUnit.DAYS.between(checkIn, checkOut);
        double totalPrice = days * roomCategory.getPrice();

        StayBooking stayBooking = StayBooking.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .stayId(stayId)
                .roomCategoryId(roomCategoryId)
                .checkIn(checkIn)
                .checkOut(checkOut)
                .totalPrice(totalPrice)
                .status("CONFIRMED")
                .guestTitle(metadata.path("title").asText())
                .firstName(metadata.path("firstName").asText())
                .surname(metadata.path("surname").asText())
                .middleName(metadata.path("middleName").asText(null))
                .gender(metadata.path("gender").asText())
                .email(metadata.path("email").asText())
                .phoneNumber(metadata.path("phoneNumber").asText())
                .whatsappNumber(metadata.path("whatsappNumber").asText(null))
                .phoneNumber2(metadata.path("phoneNumber2").asText(null))
                .specialRequests(metadata.path("specialRequests").asText(null))
                .createdAt(LocalDateTime.now())
                .build();

        return mongoTemplate.save(stayBooking);
    }


    public StayBooking createPendingStayBooking(BookStayRequest request, String userId) {
        // Validate stay and room availability
        Stay stay = stayRepository.findById(request.getStayId())
                .orElseThrow(() -> new RuntimeException("Stay not found"));

        RoomCategory roomCategory = stay.getRoomCategories().stream()
                .filter(room -> room.getId().equals(request.getRoomCategoryId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Room category not found"));

        // Check if room is available for the requested dates
        boolean isAvailable = checkRoomAvailability(request.getStayId(),
                request.getRoomCategoryId(), request.getCheckIn(), request.getCheckOut());

        if (!isAvailable) {
            throw new RuntimeException("Room not available for selected dates");
        }

        long days = ChronoUnit.DAYS.between(request.getCheckIn(), request.getCheckOut());
        double totalPrice = days * roomCategory.getPrice();

        StayBooking stayBooking = StayBooking.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .stayId(request.getStayId())
                .roomCategoryId(request.getRoomCategoryId())
                .checkIn(request.getCheckIn())
                .checkOut(request.getCheckOut())
                .totalPrice(totalPrice)
                .status(BookingStatus.PENDING_PAYMENT.name())
                .guestTitle(request.getTitle())
                .firstName(request.getFirstName())
                .surname(request.getSurname())
                .middleName(request.getMiddleName())
                .gender(request.getGender())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .whatsappNumber(request.getWhatsappNumber())
                .phoneNumber2(request.getPhoneNumber2())
                .specialRequests(request.getSpecialRequests())
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(30)) // Payment expires after 30 mins
                .build();

        return mongoTemplate.save(stayBooking);
    }

    public FlightBooking createPendingFlightBooking(BookFlightRequest request, String userId) {

        BigDecimal pricePerAdult = new BigDecimal(request.getPricePerAdult().replace("NGN", "").trim());
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
                .flightFare(pricePerAdult)  // Assuming the base fare is equal to the pricePerAdult
                .taxes(BigDecimal.ZERO)     // Set taxes to zero for now, or modify based on your business logic
                .discount(BigDecimal.ZERO)  // Set discount to zero, you can modify based on discounts
                .airportLoungeSelected(request.getAirportLoungeSelected())
                .wheelChairAssistanceSelected(request.getWheelChairAssistanceSelected())
                .callReminderSelected(request.getCallReminderSelected())
                .travelInsuranceSelected(request.getTravelInsuranceSelected())
                .smsTicketDetailsSelected(request.getSmsTicketDetailsSelected())
                .status(BookingStatus.PENDING_PAYMENT.name())  // Mark the booking as pending
                .build();

        flightBooking.calculateTotalPrice();
        return mongoTemplate.save(flightBooking);
    }


    public RideBooking createPendingRideBooking(BookRideRequest request, String userId) {
        // Validate stay and room availability
        RideProduct ride = rideRepository.findById(request.getRideId())
                .orElseThrow(() -> new RuntimeException("Ride not found"));


        RideBooking rideBooking = RideBooking.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .rideId(request.getRideId())
                .departureDate(request.getDepartureDate())
                .dropOffLocation(request.getDropOffLocation())
                .pickupLocation(request.getPickupLocation())
                .pickupTime(request.getPickupTime())
                .price(request.getPrice())
                .status(BookingStatus.PENDING_PAYMENT.name())
                .guestTitle(request.getTitle())
                .firstName(request.getFirstName())
                .surname(request.getSurname())
                .middleName(request.getMiddleName())
                .gender(request.getGender())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .whatsappNumber(request.getWhatsappNumber())
                .phoneNumber2(request.getPhoneNumber2())
                .noteForDriver(request.getNoteForDriver())
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(30)) // Payment expires after 30 mins
                .build();

        return mongoTemplate.save(rideBooking);
    }




    private boolean checkRoomAvailability(String stayId, String roomCategoryId,
                                          LocalDate checkIn, LocalDate checkOut) {
        // Find all overlapping bookings for this room category that are confirmed
        List<StayBooking> overlappingStayBookings = mongoTemplate.find(
                Query.query(Criteria.where("stayId").is(stayId)
                        .and("roomCategoryId").is(roomCategoryId)
                        .and("status").is("CONFIRMED")
                        .and("checkIn").lt(checkOut)
                        .and("checkOut").gt(checkIn)),
                StayBooking.class
        );

        // Get the room category to check total available rooms
        Stay stay = stayRepository.findById(stayId)
                .orElseThrow(() -> new RuntimeException("Stay not found"));

        RoomCategory roomCategory = stay.getRoomCategories().stream()
                .filter(room -> room.getId().equals(roomCategoryId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Room category not found"));

        int totalRooms = roomCategory.getUnitsAvailable();
        int bookedRooms = overlappingStayBookings.size();

        // Room is available if there are fewer bookings than total rooms
        return bookedRooms < totalRooms;
    }




    @Scheduled(fixedRate = 60 * 60 * 1000) // Run every hour
    public void cleanupExpiredBookings() {
        log.info("Starting cleanup of expired bookings");
        LocalDateTime now = LocalDateTime.now();

        try {
            List<StayBooking> expiredStayBookings = bookingRepository
                    .findByStatusAndExpiresAtBefore(BookingStatus.PENDING_PAYMENT.name(), now);

            log.info("Found {} expired bookings to cleanup", expiredStayBookings.size());

            for (StayBooking stayBooking : expiredStayBookings) {
                stayBooking.setStatus(BookingStatus.CANCELLED.name());
                bookingRepository.save(stayBooking);

                // Optionally notify user about cancellation
              //  emailService.sendStayBookingCancellationNotice(stayBooking);
            }
        } catch (Exception e) {
            log.error("Error during expired bookings cleanup", e);
        }
    }

    public StayBooking updateBookingStatus(String paymentReference, String status) {
        StayBooking booking = bookingRepository.findByPaymentReference(paymentReference)
                .orElseThrow(() -> new RuntimeException("Booking not found for reference: " + paymentReference));

        booking.setStatus(status);

        if ("CONFIRMED".equals(status)) {
            booking.setPaymentConfirmedAt(LocalDateTime.now());
           // emailService.sendStayBookingConfirmation(booking);
        }
        else if ("PAYMENT_FAILED".equals(status)) {
            // Extend expiration time to allow retry
            booking.setExpiresAt(LocalDateTime.now().plusMinutes(30));
            // Send payment failure email
           // emailService.sendStayPaymentFailureNotification(booking);
        }

        return mongoTemplate.save(booking);
    }
}
