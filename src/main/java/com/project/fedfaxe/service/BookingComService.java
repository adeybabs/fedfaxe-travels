package com.project.fedfaxe.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.fedfaxe.exception.ExternalApiException;
import com.project.fedfaxe.model.dto.bookingcom.BookingComApiResponse;
import com.project.fedfaxe.model.dto.bookingcom.BookingComMapper;
import com.project.fedfaxe.model.dto.response.FlightSearchResponse;
import com.project.fedfaxe.model.enums.TravelClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class BookingComService {


    @Value("${rapidapi.key}")
    private String rapidApiKey;

    @Value("${rapidapi.host}")
    private String rapidApiHost;

    private static final String BASE_URL = "https://booking-com15.p.rapidapi.com/api/v1/flights/searchFlights";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final BookingComMapper flightMapper;

    public BookingComService(BookingComMapper flightMapper) {
        this.flightMapper = flightMapper;
    }

//    public String searchDomesticFlights(String origin, String destination, String departDate, int adults) {
//        try {
//            String fromId = origin + ".AIRPORT";
//            String toId = destination + ".AIRPORT";
//
//            String url = String.format("%s?fromId=%s&toId=%s&departDate=%s&stops=none&pageNo=1&adults=%d&children=0%%2C17&sort=BEST&cabinClass=ECONOMY&currency_code=NGN",
//                    BASE_URL, fromId, toId, departDate, adults);
//
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(URI.create(url))
//                    .header("x-rapidapi-key", rapidApiKey)
//                    .header("x-rapidapi-host", rapidApiHost)
//                    .GET()
//                    .build();
//
//            HttpResponse<String> response = HttpClient.newHttpClient()
//                    .send(request, HttpResponse.BodyHandlers.ofString());
//
//            if (response.statusCode() == 200) {
//                return response.body(); // You can later map this into your DTO (like FlightSearchResponse)
//            } else {
//                throw new RuntimeException("Failed to fetch domestic flights: " + response.body());
//            }
//        } catch (Exception e) {
//            throw new RuntimeException("Error calling Booking.com API", e);
//        }
//    }


    public List<FlightSearchResponse> searchDomesticFlights(String origin, String destination, String departDate, int adults) {
        try {
            String fromId = origin + ".AIRPORT";
            String toId = destination + ".AIRPORT";

            String url = String.format("%s?fromId=%s&toId=%s&departDate=%s&stops=none&pageNo=1&adults=%d&children=0%%2C17&sort=BEST&cabinClass=ECONOMY&currency_code=NGN",
                    BASE_URL, fromId, toId, departDate, adults);

            log.info("Calling Booking.com API: {}", url);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("x-rapidapi-key", rapidApiKey)
                    .header("x-rapidapi-host", rapidApiHost)
                    .GET()
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                log.info("Successfully received response from Booking.com API");
                log.debug("API Response: {}", response.body());

                try {
                    BookingComApiResponse apiResponse = objectMapper.readValue(
                            response.body(), BookingComApiResponse.class);

                    // Check if the response contains an error
                    if (!apiResponse.isStatus() ||
                            (apiResponse.getData() != null && apiResponse.getData().getError() != null)) {
                        log.error("Booking.com API returned error: {}",
                                apiResponse.getData() != null ? apiResponse.getData().getError() : apiResponse.getMessage());
                        throw new ExternalApiException("API Error: " +
                                (apiResponse.getData() != null && apiResponse.getData().getError() != null ?
                                        apiResponse.getData().getError() : apiResponse.getMessage()));
                    }

                    List<FlightSearchResponse> mappedResults = flightMapper.mapBookingComResponse(apiResponse);
                    log.info("Mapped {} flight offers", mappedResults.size());

                    return mappedResults;

                } catch (JsonProcessingException e) {
                    log.error("Error parsing API response: {}", e.getMessage());
                    log.error("Raw response: {}", response.body());
                    throw new ExternalApiException("Error parsing API response", e);
                }
            } else {
                log.error("Booking.com API returned status {}: {}", response.statusCode(), response.body());
                throw new ExternalApiException("Failed to fetch domestic flights: HTTP " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            log.error("Network error calling Booking.com API", e);
            throw new ExternalApiException("Network error occurred while calling Booking.com API", e);
        } catch (ExternalApiException e) {
            // Re-throw our custom exceptions
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error calling Booking.com API", e);
            throw new ExternalApiException("Unexpected error occurred", e);
        }
    }
}