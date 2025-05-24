package com.project.fedfaxe.repository;

import com.project.fedfaxe.model.StayBooking;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends MongoRepository<StayBooking, String> {

    // Find booking by payment reference
    Optional<StayBooking> findByPaymentReference(String reference);

    // Find bookings by status and expiration date for cleanup
    List<StayBooking> findByStatusAndExpiresAtBefore(String status, LocalDateTime dateTime);


}
