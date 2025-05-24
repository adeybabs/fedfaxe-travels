package com.project.fedfaxe.service;

import com.project.fedfaxe.model.*;
import com.project.fedfaxe.model.dto.request.BookFlightRequest;
import com.project.fedfaxe.model.dto.request.BookPackageRequest;
import com.project.fedfaxe.model.dto.request.BookRideRequest;
import com.project.fedfaxe.model.dto.request.BookStayRequest;
import com.project.fedfaxe.model.dto.response.InitializePaymentResponse;
import com.project.fedfaxe.repository.RideProductRepository;
import com.project.fedfaxe.repository.StayRepository;
import jakarta.xml.bind.DatatypeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
    private final RideProductRepository rideRepository;
    private final MongoTemplate mongoTemplate;
    @Lazy
    private final BookingService bookingService;


    public InitializePaymentResponse initializeStayPayment(BookStayRequest request, String userId) {
        // Create pending booking first
        StayBooking stayBooking = bookingService.createPendingStayBooking(request, userId);

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
        body.put("callback_url", "https://fedfaxetravels.com//verify-payment"); // optional
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

    public InitializePaymentResponse initializeFlightPayment(BookFlightRequest request, String userId) {
        FlightBooking flightBooking = bookingService.createPendingFlightBooking(request, userId);

        String reference = "FL-" + flightBooking.getId().substring(0, 8);
        flightBooking.setPaymentReference(reference);

        int adults = 1; // Default value
        if (flightBooking.getAdults() != null) {
            adults = flightBooking.getAdults();
        }

        // Calculate total price - ensure non-zero amount
        BigDecimal flightFare = flightBooking.getFlightFare() != null ?
                flightBooking.getFlightFare() : BigDecimal.ZERO;
        BigDecimal totalPrice = flightFare.multiply(new BigDecimal(adults));

        // Make sure we have a minimum price (e.g., 100 Naira = 10000 kobo)
        if (totalPrice.compareTo(BigDecimal.ZERO) <= 0) {
            totalPrice = new BigDecimal("100");
        }

        // Convert to kobo and ensure we have an integer
        int amountInKobo = totalPrice.multiply(new BigDecimal("100")).intValue();

        // Ensure amount is at least 100 kobo (minimum for Paystack)
        if (amountInKobo < 100) {
            amountInKobo = 100;
        }
        mongoTemplate.save(flightBooking);

       // BigDecimal totalPrice = flightBooking.getFlightFare().multiply(new BigDecimal(flightBooking.getAdults()));

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("bookingType", "FLIGHT");
        metadata.put("userId", userId);
        metadata.put("flightId", flightBooking.getId());
        metadata.put("departureAirport", request.getDepartureAirport());
        metadata.put("arrivalAirport", request.getArrivalAirport());
        metadata.put("departureTime", request.getDepartureTime().toString());
        metadata.put("arrivalTime", request.getArrivalTime().toString());
        metadata.put("passengerName", request.getFirstName() + " " + request.getSurname());
        metadata.put("email", request.getEmail());
        metadata.put("phone", request.getPhoneNumber());
        metadata.put("flightFare", totalPrice.toString());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(secretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("email", request.getEmail());
        body.put("amount", totalPrice.multiply(new BigDecimal(100)).intValue());
        body.put("callback_url", "https://fedfaxetravels.com/verify-payment");
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

    public InitializePaymentResponse initializeRidePayment(BookRideRequest request, String userId) {
        // Create pending booking first
        RideBooking rideBooking = bookingService.createPendingRideBooking(request, userId);

        // Generate payment reference
        String reference = "RD-" + rideBooking.getId().substring(0, 8);
        rideBooking.setPaymentReference(reference);
        mongoTemplate.save(rideBooking);

        RideProduct ride = rideRepository.findById(request.getRideId())
                .orElseThrow(() -> new RuntimeException("Ride not found"));


        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(secretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("bookingType", "RIDE");
        metadata.put("userId", userId);
        metadata.put("rideId", request.getRideId());
        metadata.put("currency", request.getCurrency());
        metadata.put("departureDate", request.getDepartureDate());
        metadata.put("pickupTime", request.getPickupTime());
        metadata.put("pickupLocation", request.getPickupLocation());
        metadata.put("dropOffLocation", request.getDropOffLocation());
        metadata.put("pricingInfo", request.getPricingInfo());
        metadata.put("guestName", request.getFirstName() + " " + request.getSurname());
        metadata.put("email", request.getEmail());
        metadata.put("phone", request.getPhoneNumber());

        Map<String, Object> body = new HashMap<>();
        body.put("email", request.getEmail());
        body.put("amount", request.getPrice()); // Paystack uses kobo
        body.put("callback_url", "https://fedfaxetravels.com//verify-payment"); // optional
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


    public InitializePaymentResponse initializePackagePayment(BookPackageRequest request, String userId) {
        // Create pending booking first
        PackageBooking packageBooking = bookingService.createPendingPackageBooking(request, userId);

        // Generate payment reference
        String reference = "PK-" + packageBooking.getId().substring(0, 8);
        packageBooking.setPaymentReference(reference);
        mongoTemplate.save(packageBooking);

        RideProduct ride = rideRepository.findById(request.getPackageId())
                .orElseThrow(() -> new RuntimeException("Package not found"));


        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(secretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("bookingType", "RIDE");
        metadata.put("userId", userId);
        metadata.put("packageId", request.getPackageId());
        metadata.put("currency", request.getCurrency());
        metadata.put("guestName", request.getFirstName() + " " + request.getSurname());
        metadata.put("email", request.getEmail());
        metadata.put("phone", request.getPhoneNumber());

        Map<String, Object> body = new HashMap<>();
        body.put("email", request.getEmail());
        body.put("price_with_flights", request.getPriceWithFlights());
        body.put("priceWithoutFlights", request.getPriceWithoutFlights());
        body.put("callback_url", "https://fedfaxetravels.com//verify-payment"); // optional
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