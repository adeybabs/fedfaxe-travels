package com.project.fedfaxe.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.fedfaxe.model.dto.AirlineMapper;
import com.project.fedfaxe.model.dto.AirportResponse;
import com.project.fedfaxe.model.dto.FlightSearchResponse;
import com.project.fedfaxe.service.AmadeusFlightService;
import com.project.fedfaxe.service.FlightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    //private final FlightService flightService;
    private final AmadeusFlightService amadeusFlightService;

    public FlightController(FlightService flightService, AmadeusFlightService amadeusFlightService){
        //this.flightService = flightService;
        this.amadeusFlightService = amadeusFlightService;
    }


    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Flights retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/search")
    public ResponseEntity<String> searchFlights(
            @RequestParam @Parameter(description = "Departure airport code (e.g., LOS)") String origin,
            @RequestParam @Parameter(description = "Arrival airport code (e.g., LHR)") String destination,
            @RequestParam @Parameter(description = "Departure date (format: YYYY-MM-DD)") String departureDate,
            @RequestParam(required = false) @Parameter(description = "Return date (optional, format: YYYY-MM-DD)") String returnDate,
            @RequestParam @Parameter(description = "Number of adults traveling") int adults,
            @RequestParam(defaultValue = "false") @Parameter(description = "Set to 'true' to only show direct flights") boolean directFlightOnly,
            @RequestParam(required = false, defaultValue = "cheapest")
            @Parameter(description = "Sorting option (cheapest, fastest, recommended)")String sortBy,
            @RequestParam(required = false, defaultValue = "ECONOMY")
            @Parameter(description = "Travel class (ECONOMY, BUSINESS, FIRST)")String travelClass) {

        String flightData = amadeusFlightService.searchFlights(
                origin, destination, departureDate, returnDate, adults, directFlightOnly, sortBy, travelClass);

        return ResponseEntity.ok(flightData);
    }

    @Operation(
            summary = "Get available airlines and their prices",
            description = "Returns a list of airlines flying between the specified origin and destination along with their prices.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful Response",
                            content = @Content(schema = @Schema(implementation = Map.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping("/available-airlines")
    public Map<String, String> getAvailableAirlines(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam String departureDate,
            @RequestParam(required = false) String returnDate,
            @RequestParam(defaultValue = "1") int adults,
            @RequestParam(defaultValue = "false") Boolean directFlightOnly,
            @RequestParam(defaultValue = "cheapest") String sortBy,
            @RequestParam(defaultValue = "ECONOMY") String travelClass) {

        ResponseEntity<String> response = searchFlights(origin, destination, departureDate, returnDate, adults, directFlightOnly, sortBy, travelClass);

        // ✅ Convert JSON String response to List<FlightSearchResponse>
        ObjectMapper objectMapper = new ObjectMapper();
        List<FlightSearchResponse> flights = new ArrayList<>();

        try {
            flights = objectMapper.readValue(response.getBody(), new TypeReference<List<FlightSearchResponse>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse flight search response", e);
        }

        //Extract airline names and their prices and map to full name
        Map<String, String> airlinePrices = new HashMap<>();
        for (FlightSearchResponse flight : flights) {
            String airlineName = AirlineMapper.getAirlineName(flight.getAirline());
            airlinePrices.put(airlineName, flight.getPricePerAdult());
        }

        return airlinePrices;
    }


    @Operation(
            summary = "Get Outbound Journeys",
            description = "Fetch available outbound flight options from origin to destination on a given date, with optional filters for direct flights and maximum layovers."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved flight options",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/outbound-journeys")
    public List<Map<String, String>> getOutboundJourneys(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam String departureDate,
            @RequestParam(required = false) String returnDate,
            @RequestParam(defaultValue = "1") int adults,
            @RequestParam(defaultValue = "false") Boolean directFlightOnly,
            @RequestParam(defaultValue = "cheapest") String sortBy,
            @RequestParam(defaultValue = "ECONOMY") String travelClass,
            @Parameter(description = "Maximum number of stops allowed (0 for direct flights, 1 for up to 1 stop, etc.)", example = "1")
            @RequestParam(required = false) Integer maxStops) {

        ResponseEntity<String> response = searchFlights(origin, destination, departureDate, returnDate, adults, directFlightOnly, sortBy, travelClass);

        // ✅ Convert JSON response to List<FlightSearchResponse>
        ObjectMapper objectMapper = new ObjectMapper();
        List<FlightSearchResponse> flights = new ArrayList<>();

        try {
            flights = objectMapper.readValue(response.getBody(), new TypeReference<List<FlightSearchResponse>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse flight search response", e);
        }

        // Apply filtering based on maxStops
        return flights.stream()
                .filter(flight -> maxStops == null || flight.getStops() <= maxStops) // Apply stop filtering
                .map(flight -> Map.of(
                        "departureAirport", flight.getDepartureAirport(),
                        "arrivalAirport", flight.getArrivalAirport(),
                        "departureTime", flight.getDepartureTime(),
                        "arrivalTime", flight.getArrivalTime(),
                        "duration", flight.getDuration(),
                        "airline", flight.getAirline(),
                        "stops", String.valueOf(flight.getStops())
                ))
                .collect(Collectors.toList());
    }


    @Operation(
            summary = "Get Flight Times",
            description = "Fetch flight departure and arrival times, filtered by time of day (morning, afternoon, evening)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flight times retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/flight-times")
    public List<Map<String, String>> getFlightTimes(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam String departureDate,
            @RequestParam(required = false) String returnDate,
            @RequestParam(defaultValue = "1") int adults,
            @RequestParam(defaultValue = "false") Boolean directFlightOnly,
            @RequestParam(defaultValue = "cheapest") String sortBy,
            @RequestParam(defaultValue = "ECONOMY") String travelClass,
            @Parameter(description = "Filter by time of day (ALL, MORNING, AFTERNOON, EVENING)", example = "MORNING")
            @RequestParam(defaultValue = "ALL") String timeOfDay) {

        ResponseEntity<String> response = searchFlights(origin, destination, departureDate, returnDate, adults, directFlightOnly, sortBy, travelClass);

        // Convert JSON response to List<FlightSearchResponse>
        ObjectMapper objectMapper = new ObjectMapper();
        List<FlightSearchResponse> flights;
        try {
            flights = objectMapper.readValue(response.getBody(), new TypeReference<List<FlightSearchResponse>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse flight search response", e);
        }
        // Apply filtering based on time of day
        return flights.stream()
                .filter(flight -> filterByTimeOfDay(flight.getDepartureTime(), timeOfDay))
                .map(flight -> Map.of(
                        "departureAirport", flight.getDepartureAirport(),
                        "arrivalAirport", flight.getArrivalAirport(),
                        "departureTime", flight.getDepartureTime(),
                        "arrivalTime", flight.getArrivalTime(),
                        "duration", flight.getDuration(),
                        "airline", flight.getAirline(),
                        "stops", String.valueOf(flight.getStops())
                ))
                .collect(Collectors.toList());
    }

    /**
     * Filters flight departure times based on the timeOfDay parameter.
     */
    private boolean filterByTimeOfDay(String departureTime, String timeOfDay) {
        try {
            // ✅ Correct parsing from ISO_LOCAL_DATE_TIME
            LocalDateTime dateTime = LocalDateTime.parse(departureTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            LocalTime time = dateTime.toLocalTime();

            // ✅ Define time ranges
            LocalTime morningStart = LocalTime.of(0, 0);
            LocalTime morningEnd = LocalTime.of(11, 59);
            LocalTime afternoonStart = LocalTime.of(12, 0);
            LocalTime afternoonEnd = LocalTime.of(17, 59);
            LocalTime eveningStart = LocalTime.of(18, 0);
            LocalTime eveningEnd = LocalTime.of(23, 59);

            // ✅ Apply filtering
            switch (timeOfDay.toUpperCase()) {
                case "MORNING":
                    return !time.isBefore(morningStart) && !time.isAfter(morningEnd);
                case "AFTERNOON":
                    return !time.isBefore(afternoonStart) && !time.isAfter(afternoonEnd);
                case "EVENING":
                    return !time.isBefore(eveningStart) && !time.isAfter(eveningEnd);
                default:
                    return true; // Return all flights for "ALL"
            }
        } catch (Exception e) {
            System.err.println("Error parsing departureTime: " + departureTime);
            e.printStackTrace();
            return false; // Skip invalid records
        }
    }

    @Operation(
            summary = "Retrieve inbound flight journeys",
            description = "Fetches available inbound flight options from the destination back to the origin, filtered by stops, travel class, and sorting preferences."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of inbound flights"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/inbound-journeys")
    public List<Map<String, String>> getInboundJourneys(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam String returnDate,
            @RequestParam(defaultValue = "1") int adults,
            @RequestParam(defaultValue = "false") Boolean directFlightOnly,
            @RequestParam(defaultValue = "cheapest") String sortBy,
            @RequestParam(defaultValue = "ECONOMY") String travelClass,
            @RequestParam(required = false) Integer maxStops) {

        ResponseEntity<String> response = searchFlights(origin, destination, returnDate, null, adults, directFlightOnly, sortBy, travelClass);
        ObjectMapper objectMapper = new ObjectMapper();
        List<FlightSearchResponse> flights = new ArrayList<>();

        try {
            flights = objectMapper.readValue(response.getBody(), new TypeReference<List<FlightSearchResponse>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse flight search response", e);
        }

        return flights.stream()
                .filter(flight -> maxStops == null || flight.getStops() <= maxStops)
                .map(flight -> Map.of(
                        "departureAirport", flight.getDepartureAirport(),
                        "arrivalAirport", flight.getArrivalAirport(),
                        "departureTime", flight.getDepartureTime(),
                        "arrivalTime", flight.getArrivalTime(),
                        "duration", flight.getDuration(),
                        "airline", flight.getAirline(),
                        "stops", String.valueOf(flight.getStops())
                ))
                .collect(Collectors.toList());
    }

    @Operation(
            summary = "Search Airports and Cities",
            description = "Searches for airports and cities using a keyword (e.g., 'China', 'New York', 'Beijing'). If a country name is provided, it returns all locations in that country."
    )
    @GetMapping("/search-airports")
    public ResponseEntity<List<AirportResponse>> searchAirports(
            @RequestParam String query) {

        List<AirportResponse> airports = amadeusFlightService.searchAirports(query);
        return ResponseEntity.ok(airports);
    }



}
