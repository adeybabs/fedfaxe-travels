package com.project.fedfaxe.service;

import org.springframework.stereotype.Service;

@Service
public class AmadeusFlightService {

//    private final Amadeus amadeus;

//    public AmadeusFlightService(@Value("${amadeus.api.key}") String apiKey,
//                                @Value("${amadeus.api.secret}") String apiSecret) {
//        this.amadeus = Amadeus.builder(apiKey, apiSecret).build();
//    }
//
//    public FlightOfferSearch[] searchFlights(String from, String to, String departureDate, int adults) {
//        try {
//            return amadeus.shopping.flightOffersSearch.get(
//                    Params.with("originLocationCode", from)
//                            .and("destinationLocationCode", to)
//                            .and("departureDate", departureDate)
//                            .and("adults", adults)
//                            .and("max", 5)); // Get top 5 results
//        } catch (ResponseException e) {
//            e.printStackTrace();
//            return new FlightOfferSearch[0];
//        }
//    }
}
