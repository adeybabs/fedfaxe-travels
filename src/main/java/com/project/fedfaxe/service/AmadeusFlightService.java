package com.project.fedfaxe.service;

import com.amadeus.Amadeus;
import com.amadeus.exceptions.ResponseException;
import com.amadeus.resources.FlightOfferSearch;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.fedfaxe.exception.FlightSearchException;
import com.project.fedfaxe.model.dto.FlightSearchResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AmadeusFlightService {

    private final Amadeus amadeus;
    private final RestTemplate restTemplate = new RestTemplate();
    private final AmadeusAuthService amadeusAuthService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AmadeusFlightService(
            @Value("${amadeus.api-key}") String apiKey,
            @Value("${amadeus.api-secret}") String apiSecret, AmadeusAuthService amadeusAuthService) {
        this.amadeusAuthService = amadeusAuthService;
        this.amadeus = Amadeus.builder(apiKey, apiSecret).build();
    }

    public String searchFlights(String origin, String destination, String departureDate, int adults) {
        String token = amadeusAuthService.getAccessToken();
        String url = String.format(
                "https://test.api.amadeus.com/v2/shopping/flight-offers?originLocationCode=%s&destinationLocationCode=%s&departureDate=%s&adults=%d",
                origin, destination, departureDate, adults
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);


//        if (response.getStatusCode() == HttpStatus.OK) {
//            return response.getBody();
//        }
        if (response.getStatusCode() == HttpStatus.OK) {
//            return extractRequiredFlightData(response.getBody());
            List<FlightSearchResponse> flightData = extractRequiredFlightData(response.getBody());
            try {
                return new ObjectMapper().writeValueAsString(flightData);
            } catch (JsonProcessingException e) {
                log.error("Error converting flight data to JSON", e);
                return "{}";  // Return empty JSON object if an error occurs
            }

        }
        throw new RuntimeException("Failed to fetch flights from Amadeus");
    }

    private List<FlightSearchResponse> extractRequiredFlightData(String responseBody) {
        List<FlightSearchResponse> filteredFlights = new ArrayList<>();
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode dataArray = rootNode.get("data");

            if (dataArray != null && dataArray.isArray()) {
                for (JsonNode flight : dataArray) {
                    FlightSearchResponse dto = new FlightSearchResponse();
                    JsonNode itinerary = flight.get("itineraries").get(0);
                    JsonNode segment = itinerary.get("segments").get(0);
                    JsonNode price = flight.get("price");

                    dto.setDepartureTime(segment.get("departure").get("at").asText());
                    dto.setArrivalTime(segment.get("arrival").get("at").asText());
                    dto.setDuration(itinerary.get("duration").asText());
                    dto.setDepartureAirport(segment.get("departure").get("iataCode").asText());
                    dto.setArrivalAirport(segment.get("arrival").get("iataCode").asText());
                    dto.setFlightType(segment.get("numberOfStops").asInt() == 0 ? "Direct flight" : "With stops");
                    dto.setAirline(segment.get("carrierCode").asText());

                    // Convert price from EUR to NGN
                    String currency = price.get("currency").asText();
                    BigDecimal totalPrice = new BigDecimal(price.get("total").asText());

                    if ("EUR".equals(currency)) {
                        BigDecimal exchangeRate = getExchangeRate("EUR", "NGN");
                        totalPrice = totalPrice.multiply(exchangeRate);
                    }

                    dto.setPricePerAdult("NGN " + totalPrice.toPlainString());


                    filteredFlights.add(dto);
                }
            }
        } catch (Exception e) {
            log.error("Error processing Amadeus response: ", e);
        }
        return filteredFlights;
    }

    private BigDecimal getExchangeRate(String from, String to) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.exchangerate-api.com/v4/latest/" + from;

        ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
        return new BigDecimal(response.getBody().get("rates").get(to).asText());
    }

}
