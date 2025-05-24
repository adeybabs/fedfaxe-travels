package com.project.fedfaxe.repository;

import com.project.fedfaxe.model.FlightBooking;

import org.bson.Document;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface FlightBookingRepository extends MongoRepository<FlightBooking, String> {

    Long countByStatus(String status);
    Long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    Long countByStatusAndCreatedAtBetween(String status, LocalDateTime start, LocalDateTime end);

    @Aggregation(pipeline = {
            "{ $match: { status: ?0 } }",
            "{ $group: { _id: null, total: { $sum: '$totalPrice' } } }"
    })
    Double sumTotalRevenueByStatus(String status);

    @Aggregation(pipeline = {
            "{ $match: { status: ?0, createdAt: { $gte: ?1, $lte: ?2 } } }",
            "{ $group: { _id: null, total: { $sum: '$totalPrice' } } }"
    })
    Double sumTotalRevenueByStatusAndDateRange(String status, LocalDateTime startDate, LocalDateTime endDate);

    @Aggregation(pipeline = {
            "{ $match: { createdAt: { $gte: ?0, $lte: ?1 } } }",
            "{ $group: { _id: { $month: '$createdAt' }, count: { $sum: 1 }, revenue: { $sum: '$totalPrice' } } }"
    })
    List<Document> getMonthlyStatsAggregation(LocalDateTime startDate, LocalDateTime endDate);
}
