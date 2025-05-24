//package com.project.fedfaxe.service;
//
//import com.project.fedfaxe.model.RoomCategory;
//import com.project.fedfaxe.model.Stay;
//import com.project.fedfaxe.model.StayBooking;
//import com.project.fedfaxe.repository.FlightRepository;
//import com.project.fedfaxe.repository.PackageProductRepository;
//import com.project.fedfaxe.repository.RideProductRepository;
//import com.project.fedfaxe.repository.StayRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.stereotype.Service;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Slf4j
//@Service
//public class EmailService {
//
//    private final JavaMailSender mailSender;
//    private final StayRepository stayRepository;
//    private final RideProductRepository rideRepository;
//    private final PackageProductRepository packageRepository;
//    private final FlightRepository flightRepository;
//
//    @Value("${spring.mail.username}")
//    private String fromEmail;
//
//    @Value("${app.email.booking.subject}")
//    private String bookingSubject;
//
//    @Autowired
//    public EmailService(JavaMailSender mailSender,
//                        StayRepository stayRepository,
//                        RideProductRepository rideRepository,
//                        PackageProductRepository packageRepository,
//                        FlightRepository flightRepository) {
//        this.mailSender = mailSender;
//        this.stayRepository = stayRepository;
//        this.rideRepository = rideRepository;
//        this.packageRepository = packageRepository;
//        this.flightRepository = flightRepository;
//    }
//
//    // Method for Stay booking confirmation
//    public void sendStayBookingConfirmation(StayBooking booking) {
//        try {
//            Stay stay = stayRepository.findById(booking.getStayId())
//                    .orElseThrow(() -> new RuntimeException("Stay not found"));
//
//            RoomCategory roomCategory = stay.getRoomCategories().stream()
//                    .filter(room -> room.getId().equals(booking.getRoomCategoryId()))
//                    .findFirst()
//                    .orElseThrow(() -> new RuntimeException("Room category not found"));
//
//            Map<String, Object> templateModel = new HashMap<>();
//            templateModel.put("bookingType", "Stay");
//            templateModel.put("customerName", booking.getFirstName() + " " + booking.getSurname());
//            templateModel.put("itemName", stay.getName());
//            templateModel.put("roomName", roomCategory.getType());
//            templateModel.put("checkIn", booking.getCheckIn().toString());
//            templateModel.put("checkOut", booking.getCheckOut().toString());
//            templateModel.put("totalAmount", booking.getTotalPrice());
//            templateModel.put("reference", booking.getPaymentReference());
//
//            sendEmail(booking.getEmail(),
//                    "Your Stay Booking Confirmation - " + stay.getName(),
//                    "emails/stay-booking-confirmation",
//                    templateModel);
//
//        } catch (Exception e) {
//            log.error("Failed to send stay booking confirmation email", e);
//        }
//    }
//
//    // Method for Stay booking failure
//    public void sendStayPaymentFailureNotification(StayBooking booking) {
//        try {
//            Stay stay = stayRepository.findById(booking.getStayId())
//                    .orElseThrow(() -> new RuntimeException("Stay not found"));
//
//            Map<String, Object> templateModel = new HashMap<>();
//            templateModel.put("customerName", booking.getFirstName() + " " + booking.getSurname());
//            templateModel.put("itemName", stay.getName());
//            templateModel.put("expiresAt", booking.getExpiresAt().toString());
//            templateModel.put("paymentLink", "https://fedfaxetravels.com/resume-payment/" + booking.getPaymentReference());
//
//            sendEmail(booking.getEmail(),
//                    "Payment Failed for Your Stay Booking",
//                    "emails/stay-payment-failed",
//                    templateModel);
//
//        } catch (Exception e) {
//            log.error("Failed to send payment failure email", e);
//        }
//    }
//
//    // Method for Stay booking cancellation
//    public void sendStayBookingCancellationNotice(StayBooking booking) {
//        try {
//            Map<String, Object> templateModel = new HashMap<>();
//            templateModel.put("customerName", booking.getFirstName() + " " + booking.getSurname());
//            templateModel.put("bookingReference", booking.getPaymentReference());
//
//            sendEmail(booking.getEmail(),
//                    "Your Booking Has Been Cancelled",
//                    "emails/stay-booking-cancelled",
//                    templateModel);
//
//        } catch (Exception e) {
//            log.error("Failed to send cancellation email", e);
//        }
//    }
//
//    // Similar methods for Ride, Package, and Flight bookings
//    // public void sendRideBookingConfirmation(RideBooking booking) { ... }
//    // public void sendPackageBookingConfirmation(PackageBooking booking) { ... }
//    // public void sendFlightBookingConfirmation(FlightBooking booking) { ... }
//
//    // Common method to send email
//    private void sendEmail(String to, String subject, String templateName, Map<String, Object> templateModel) {
//        try {
//            // For simple text emails
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setFrom(fromEmail);
//            message.setTo(to);
//            message.setSubject(subject);
//
//            // Generate email content from template or format text here
//            String emailContent = generateEmailContent(templateName, templateModel);
//            message.setText(emailContent);
//
//            mailSender.send(message);
//            log.info("Email sent to: {}", to);
//        } catch (Exception e) {
//            log.error("Failed to send email", e);
//            throw e;
//        }
//    }
//
//    private String generateEmailContent(String templateName, Map<String, Object> model) {
//        // This is a simplified version - in a real app, you might use Thymeleaf
//        // For now, this returns a basic formatted message
//        StringBuilder content = new StringBuilder();
//
//        content.append("Dear ").append(model.get("customerName")).append(",\n\n");
//
//        if (templateName.contains("confirmation")) {
//            content.append("Thank you for your booking! Your booking is confirmed.\n\n");
//            content.append("Booking Details:\n");
//            content.append("- ").append(model.get("bookingType")).append(": ").append(model.get("itemName")).append("\n");
//
//            if (model.containsKey("roomName")) {
//                content.append("- Room: ").append(model.get("roomName")).append("\n");
//            }
//
//            if (model.containsKey("checkIn") && model.containsKey("checkOut")) {
//                content.append("- Check-in: ").append(model.get("checkIn")).append("\n");
//                content.append("- Check-out: ").append(model.get("checkOut")).append("\n");
//            }
//
//            content.append("- Total Amount: ").append(model.get("totalAmount")).append("\n\n");
//            content.append("We look forward to welcoming you!\n");
//        } else if (templateName.contains("failed")) {
//            content.append("We're sorry, but your payment was not successful.\n\n");
//            content.append("You can complete your payment by clicking this link before it expires at ")
//                    .append(model.get("expiresAt")).append(":\n");
//            content.append(model.get("paymentLink")).append("\n\n");
//            content.append("If you need assistance, please contact our support team.\n");
//        } else if (templateName.contains("cancelled")) {
//            content.append("Your booking (Reference: ").append(model.get("bookingReference"))
//                    .append(") has been cancelled due to payment timeout.\n\n");
//            content.append("If you still wish to make this booking, please visit our website again.\n");
//        }
//
//        content.append("\nBest regards,\nThe Booking Team");
//
//        return content.toString();
//    }
//}
