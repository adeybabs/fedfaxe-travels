package com.project.fedfaxe.service;

import com.project.fedfaxe.model.StayBooking;
import com.project.fedfaxe.model.RoomCategory;
import com.project.fedfaxe.model.Stay;
import com.project.fedfaxe.model.dto.BookStayRequest;
import com.project.fedfaxe.model.dto.InitializePaymentResponse;
import com.project.fedfaxe.repository.StayRepository;
import jakarta.xml.bind.DatatypeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaystackService {

    @Value("${paystack.secret.key}")
    private String secretKey;

    @Value("${paystack.base.url}")
    private String baseUrl;


    private final RestTemplate restTemplate;
    private final StayRepository stayRepository;
    private final BookingService bookingService;
    private final MongoTemplate mongoTemplate;


    public InitializePaymentResponse initializeStayPayment(BookStayRequest request, String userId) {
        // Create pending booking first
        StayBooking stayBooking = bookingService.createPendingBooking(request, userId);

        // Generate payment reference
        String reference = "BK-" + stayBooking.getId().substring(0, 8);
        stayBooking.setPaymentReference(reference);
        mongoTemplate.save(stayBooking);

        Stay stay = stayRepository.findById(request.getStayId())
                .orElseThrow(() -> new RuntimeException("Stay not found"));

        RoomCategory roomCategory = stay.getRoomCategories().stream()
                .filter(room -> room.getId().equals(request.getRoomCategoryId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Room category not found"));

        long nights = ChronoUnit.DAYS.between(request.getCheckIn(), request.getCheckOut());
        double totalPrice = nights * roomCategory.getPrice();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(secretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("bookingType", "STAY");
        metadata.put("userId", userId);
        metadata.put("stayId", request.getStayId());
        metadata.put("roomCategoryId", request.getRoomCategoryId());
        metadata.put("checkIn", request.getCheckIn().toString());
        metadata.put("checkOut", request.getCheckOut().toString());
        metadata.put("guestName", request.getFirstName() + " " + request.getSurname());
        metadata.put("email", request.getEmail());
        metadata.put("phone", request.getPhoneNumber());

        Map<String, Object> body = new HashMap<>();
        body.put("email", request.getEmail());
        body.put("amount", (int)(totalPrice * 100)); // Paystack uses kobo
        body.put("callback_url", "https://yourfrontend.com/verify-payment"); // optional
        body.put("metadata", metadata);
        body.put("reference", reference);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://api.paystack.co/transaction/initialize", entity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
            return InitializePaymentResponse.builder()
                    .authorizationUrl((String) data.get("authorization_url"))
                    .accessCode((String) data.get("access_code"))
                    .reference((String) data.get("reference"))
                    .build();
        } else {
            throw new RuntimeException("Failed to initialize payment");
        }
    }


    public boolean verifySignature(String payload, String signature) {
        try {
            Mac sha512Hmac = Mac.getInstance("HmacSHA512");
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA512");
            sha512Hmac.init(keySpec);
            byte[] macData = sha512Hmac.doFinal(payload.getBytes());
            String result = DatatypeConverter.printHexBinary(macData).toLowerCase();
            return signature.equals(result);
        } catch (Exception e) {
            log.error("Signature verification failed", e);
            return false;
        }
    }
}