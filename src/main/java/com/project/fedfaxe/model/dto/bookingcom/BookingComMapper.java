package com.project.fedfaxe.model.dto.bookingcom;

import com.project.fedfaxe.model.dto.response.FlightSearchLocalResponse;
import com.project.fedfaxe.model.dto.response.FlightSearchResponse;
import com.project.fedfaxe.model.enums.TravelClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingComMapper {

    private static final Logger logger = LoggerFactory.getLogger(BookingComMapper.class);

    public List<FlightSearchResponse> mapBookingComResponse(BookingComApiResponse apiResponse) {
        if (apiResponse == null || !apiResponse.isStatus() ||
                apiResponse.getData() == null || apiResponse.getData().getFlightOffers() == null) {
            logger.warn("Invalid or empty API response");
            return Collections.emptyList();
        }

        return apiResponse.getData().getFlightOffers().stream()
                .map(this::mapSingleFlightOffer)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private FlightSearchResponse mapSingleFlightOffer(BookingComFlightOffer flightOffer) {
        try {
            FlightSearchResponse response = new FlightSearchResponse();

            // Get the main segment (usually there's only one for domestic flights)
            BookingComSegment mainSegment = flightOffer.getSegments().get(0);

            // Map departure and arrival times
            response.setDepartureTime(formatTime(mainSegment.getDepartureTime()));
            response.setArrivalTime(formatTime(mainSegment.getArrivalTime()));

            // Map airports
            response.setDepartureAirport(mainSegment.getDepartureAirport().getName());
            response.setArrivalAirport(mainSegment.getArrivalAirport().getName());

            // Calculate duration from totalTime (convert seconds to hours:minutes)
            response.setDuration(formatDuration(mainSegment.getTotalTime()));

            // Map airline information from the first leg
            if (!mainSegment.getLegs().isEmpty()) {
                BookingComLeg firstLeg = mainSegment.getLegs().get(0);
                if (!firstLeg.getCarriersData().isEmpty()) {
                    response.setAirline(firstLeg.getCarriersData().get(0).getName());
                }
            }

            // Determine stops and flight type
            int totalStops = calculateTotalStops(flightOffer.getSegments());
            response.setStops(totalStops);
            response.setFlightType(totalStops == 0 ? "Direct" : "Connecting");

            // Map price (get adult price from travellerPrices)
            String pricePerAdult = extractAdultPrice(flightOffer.getTravellerPrices());
            response.setPricePerAdult(pricePerAdult);

            // Map travel class (from first leg)
            if (!mainSegment.getLegs().isEmpty()) {
                String cabinClass = mainSegment.getLegs().get(0).getCabinClass();
                response.setTravelClass(mapTravelClass(cabinClass));
            } else {
                response.setTravelClass(TravelClass.ECONOMY); // Default
            }

            return response;

        } catch (Exception e) {
            logger.error("Error mapping flight offer: {}", e.getMessage(), e);
            return null;
        }
    }

    private String formatTime(String isoDateTime) {
        try {
            // Parse ISO format: 2025-06-15T06:40:00
            LocalDateTime dateTime = LocalDateTime.parse(isoDateTime);
            return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        } catch (Exception e) {
            logger.warn("Failed to parse time: {}", isoDateTime);
            return isoDateTime; // Return original if parsing fails
        }
    }

    private String formatDuration(int totalTimeInSeconds) {
        int hours = totalTimeInSeconds / 3600;
        int minutes = (totalTimeInSeconds % 3600) / 60;

        if (hours > 0) {
            return String.format("%dh %dm", hours, minutes);
        } else {
            return String.format("%dm", minutes);
        }
    }

    private int calculateTotalStops(List<BookingComSegment> segments) {
        return segments.stream()
                .mapToInt(segment -> segment.getLegs().stream()
                        .mapToInt(leg -> leg.getFlightStops() != null ? leg.getFlightStops().size() : 0)
                        .sum())
                .sum();
    }

    private String extractAdultPrice(List<BookingComTravellerPrice> travellerPrices) {
        if (travellerPrices == null || travellerPrices.isEmpty()) {
            return "N/A";
        }

        // Find adult price (travellerType = "ADULT")
        Optional<BookingComTravellerPrice> adultPrice = travellerPrices.stream()
                .filter(tp -> "ADULT".equals(tp.getTravellerType()))
                .findFirst();

        if (adultPrice.isPresent()) {
            MinPrice total = adultPrice.get().getTravellerPriceBreakdown().getTotal();
            return formatPrice(total);
        }

        // Fallback to first price if no adult price found
        MinPrice total = travellerPrices.get(0).getTravellerPriceBreakdown().getTotal();
        return formatPrice(total);
    }

    private String formatPrice(MinPrice price) {
        if (price == null) {
            return "N/A";
        }

        // Convert units and nanos to decimal
        double totalAmount = price.getUnits() + (price.getNanos() / 1_000_000_000.0);

        return String.format("%.2f %s", totalAmount, price.getCurrencyCode());
    }

    private TravelClass mapTravelClass(String cabinClass) {
        if (cabinClass == null) {
            return TravelClass.ECONOMY;
        }

        switch (cabinClass.toUpperCase()) {
            case "ECONOMY":
                return TravelClass.ECONOMY;
            case "BUSINESS":
                return TravelClass.BUSINESS;
            case "FIRST":
                return TravelClass.FIRST;
            case "PREMIUM_ECONOMY":
                return TravelClass.PREMIUM_ECONOMY;
            default:
                return TravelClass.ECONOMY;
        }
    }

}
