package com.project.fedfaxe.service.impl;

import com.project.fedfaxe.model.Flight;
import com.project.fedfaxe.model.dto.FlightSearchRequests;
import com.project.fedfaxe.repository.FlightRepository;
import com.project.fedfaxe.service.FlightService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FlightServiceImpl implements FlightService {
//
//    private final FlightRepository flightRepository;
//
//    public FlightServiceImpl(FlightRepository flightRepository){
//        this.flightRepository = flightRepository;
//    }
//
//
//    @Override
//    public List<Flight> searchFlights(FlightSearchRequests request) {
//        LocalDateTime startDate = request.getDepartureDate().atStartOfDay();
//        LocalDateTime endDate = startDate.plusDays(1);
//
//        return flightRepository.findByDepartureAirportCodeAndArrivalAirportCodeAndDepartureTimeBetweenAndCabinClass(
//                request.getDepartureAirportCode(),
//                request.getArrivalAirportCode(),
//                startDate,
//                endDate,
//                request.getCabinClass()
//        );
//    }
//
//    @Override
//    public Flight addFlight(Flight flight) {
//        return null;
//    }
//
//    @Override
//    public Flight getFlightById(String id) {
//        return null;
//    }
//
//    @Override
//    public void deleteFlight(String id) {
//
//    }
}
