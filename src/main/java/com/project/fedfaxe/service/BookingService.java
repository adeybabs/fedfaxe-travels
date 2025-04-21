package com.project.fedfaxe.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.project.fedfaxe.model.StayBooking;
import com.project.fedfaxe.model.RoomCategory;
import com.project.fedfaxe.model.Stay;
import com.project.fedfaxe.model.dto.BookStayRequest;
import com.project.fedfaxe.model.enums.BookingStatus;
import com.project.fedfaxe.repository.BookingRepository;
import com.project.fedfaxe.repository.StayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final MongoTemplate mongoTemplate;
    private final StayRepository stayRepository;
    private final BookingRepository bookingRepository;

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
                .pickupLocation(metadata.path("pickupLocation").asText(null))
                .dropoffLocation(metadata.path("dropoffLocation").asText(null))
                .specialRequests(metadata.path("specialRequests").asText(null))
                .createdAt(LocalDateTime.now())
                .build();

        return mongoTemplate.save(stayBooking);
    }


    public StayBooking createPendingBooking(BookStayRequest request, String userId) {
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
                .pickupLocation(request.getPickupLocation())
                .dropoffLocation(request.getDropoffLocation())
                .specialRequests(request.getSpecialRequests())
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(30)) // Payment expires after 30 mins
                .build();

        return mongoTemplate.save(stayBooking);
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
        LocalDateTime now = LocalDateTime.now();
        List<StayBooking> expiredStayBookings = bookingRepository
                .findByStatusAndExpiresAtBefore(BookingStatus.PENDING_PAYMENT.name(), now);

        for (StayBooking stayBooking : expiredStayBookings) {
            stayBooking.setStatus(BookingStatus.CANCELLED.name());
            bookingRepository.save(stayBooking);
        }
    }

    public StayBooking updateBookingStatus(String paymentReference, String status) {
        StayBooking booking = bookingRepository.findByPaymentReference(paymentReference)
                .orElseThrow(() -> new RuntimeException("Booking not found for reference: " + paymentReference));

        booking.setStatus(status);

        if ("CONFIRMED".equals(status)) {
            booking.setPaymentConfirmedAt(LocalDateTime.now());
        }

        return mongoTemplate.save(booking);
    }
}
