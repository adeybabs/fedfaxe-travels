package com.project.fedfaxe.service;

import com.project.fedfaxe.model.Booking;
import com.project.fedfaxe.model.RoomCategory;
import com.project.fedfaxe.model.Stay;
import com.project.fedfaxe.model.dto.BookStayRequest;
import com.project.fedfaxe.repository.StayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final MongoTemplate mongoTemplate;
    private final StayRepository stayRepository;

    public Booking bookStay(BookStayRequest request, String userId) {
        Stay stay = stayRepository.findById(request.getStayId())
                .orElseThrow(() -> new RuntimeException("Stay not found"));

        RoomCategory roomCategory = stay.getRoomCategories().stream()
                .filter(room -> room.getId().equals(request.getRoomCategoryId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Room category not found"));

        // Calculate total price based on number of nights
        long days = ChronoUnit.DAYS.between(request.getCheckIn(), request.getCheckOut());
        double totalPrice = days * roomCategory.getPrice();

        // Save Booking
        Booking booking = Booking.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)  // Get from authenticated user
                .stayId(request.getStayId())
                .roomCategoryId(request.getRoomCategoryId())
                .checkIn(request.getCheckIn())
                .checkOut(request.getCheckOut())
                .totalPrice(totalPrice)
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();

        return mongoTemplate.save(booking);
    }
}
