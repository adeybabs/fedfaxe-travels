package com.project.fedfaxe.service;

import com.amadeus.Amadeus;
import com.amadeus.Params;
import com.amadeus.Response;
import com.amadeus.exceptions.ResponseException;
import com.amadeus.resources.Location;

import com.amadeus.resources.FlightOfferSearch;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.fedfaxe.exception.FlightSearchException;
import com.project.fedfaxe.model.dto.AirportResponse;
import com.project.fedfaxe.model.dto.FlightSearchResponse;
import com.project.fedfaxe.model.dto.TravelClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

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

    public String searchFlights(String origin, String destination, String departureDate, String returnDate, int adults, Boolean directFlightOnly, String sortBy, String travelClass) {
        String token = amadeusAuthService.getAccessToken();
        String url = String.format(
                "https://test.api.amadeus.com/v2/shopping/flight-offers?originLocationCode=%s&destinationLocationCode=%s&departureDate=%s&adults=%d",
                origin, destination, departureDate, adults, travelClass
        );
        if (returnDate != null && !returnDate.isEmpty()) {
            url += "&returnDate=" + returnDate;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            List<FlightSearchResponse> flightData = extractRequiredFlightData(response.getBody());

            if (directFlightOnly) {
                flightData = flightData.stream()
                        .filter(flight -> "Direct flight".equalsIgnoreCase(flight.getFlightType()))
                        .collect(Collectors.toList());
            }
            flightData = sortFlights(flightData, sortBy);
            try {
                return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(flightData);
            } catch (JsonProcessingException e) {
                log.error("Error converting flight data to JSON", e);
                return "{}";  // Return empty JSON object if an error occurs
            }
        }
        throw new RuntimeException("Failed to fetch flights from Amadeus");
    }

    private List<FlightSearchResponse> sortFlights(List<FlightSearchResponse> flights, String sortBy) {
        if (flights == null || flights.isEmpty()) {
            return flights;
        }

        switch (sortBy.toLowerCase()) {
            case "fastest":
                flights.sort(Comparator.comparing(this::parseDuration));
                break;
            case "recommended":
                flights.sort(Comparator.comparing((FlightSearchResponse flight) -> convertPriceToNaira(flight.getPricePerAdult()))
                        .thenComparing(this::parseDuration));
                break;
            case "cheapest":
            default:
                flights.sort(Comparator.comparing(flight -> convertPriceToNaira(flight.getPricePerAdult())));
                break;
        }
        return flights;
    }

    private BigDecimal convertPriceToNaira(String price) {
        return new BigDecimal(price.replace("NGN ", "").trim());
    }

    private Duration parseDuration(FlightSearchResponse flight) {
        return Duration.parse(flight.getDuration()); // Converts "PT1H30M" to Duration object
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
                    dto.setTravelClass(dto.getTravelClass());


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



    public List<AirportResponse> searchAirports(String query) {
        try {

            String countryCode = getCountryCode(query);  // Convert "China" → "CN"

            Params params = Params.with("subType", "CITY,AIRPORT");

            if (countryCode != null) {
                params.and("countryCode", countryCode);  // Search all locations in "CN"
            } else {
                params.and("keyword", query);

            }

                Location[] locations = amadeus.referenceData.locations.get(params);

            System.out.println("Raw API Response: " + Arrays.toString(locations)); // Debugging

            List<AirportResponse> results = new ArrayList<>();

            for (Location loc : locations) {
                if (loc.getAddress() != null) {
                    String formattedLocation = loc.getAddress().getCityName() + ", " + getCountryName(loc.getAddress().getCountryCode());
                    results.add(new AirportResponse(loc.getName(), loc.getIataCode(), formattedLocation));
                }
            }

            return results;
        } catch (ResponseException e) {
            throw new RuntimeException("Error fetching location data", e);
        }
    }


    private String formatLocation(Location loc) {
        return loc.getAddress() != null
                ? loc.getAddress().getCityName() + ", " + getCountryName(loc.getAddress().getCountryCode())
                : "Unknown location";
    }

    private String getCountryName(String countryCode) {
        return new java.util.Locale("", countryCode).getDisplayCountry();
    }

    private String getCountryCode(String countryName) {
        return Arrays.stream(Locale.getISOCountries())
                .filter(code -> new Locale("", code).getDisplayCountry().equalsIgnoreCase(countryName))
                .findFirst()
                .orElse(null);
    }


}
