package com.project.fedfaxe.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.fedfaxe.model.StayBooking;
import com.project.fedfaxe.model.enums.BookingStatus;
import com.project.fedfaxe.repository.BookingRepository;
import com.project.fedfaxe.service.BookingService;
import com.project.fedfaxe.service.PaystackService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/paystack")
public class PayStackWebHookController {

    @Autowired
    private BookingService bookingService;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private PaystackService paystackService;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            HttpServletRequest request,
            @RequestHeader(value = "X-Paystack-Signature", required = false) String signature) throws IOException {

        String payload = new String(request.getInputStream().readAllBytes());
        log.info("Webhook received: {}", payload);

        // Verify signature if provided
        if (signature != null && !paystackService.verifySignature(payload, signature)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid signature");
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(payload);
            String event = root.path("event").asText();
            JsonNode data = root.path("data");
            String reference = data.path("reference").asText();

            switch (event) {
                case "charge.success":
                    bookingService.updateBookingStatus(reference, "CONFIRMED");
                    // You could add email notification here
                    break;
                case "charge.failed":
                    bookingService.updateBookingStatus(reference, "PAYMENT_FAILED");
                    break;
                default:
                    log.info("Unhandled event type: {}", event);
            }

            return ResponseEntity.ok("Webhook processed");

        } catch (Exception e) {
            log.error("Error processing webhook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing webhook: " + e.getMessage());
        }
    }
}
