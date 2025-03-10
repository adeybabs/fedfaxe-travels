package com.project.fedfaxe.model.dto;

import java.util.HashMap;
import java.util.Map;

public class AirlineMapper {

    private static final Map<String, String> AIRLINE_MAP = new HashMap<>();

    static {
        AIRLINE_MAP.put("QR", "Qatar Airways");
        AIRLINE_MAP.put("KL", "KLM Royal Dutch Airlines");
        AIRLINE_MAP.put("AC", "Air Canada");
        AIRLINE_MAP.put("AF", "Air France");
        AIRLINE_MAP.put("KQ", "Kenya Airways");
        AIRLINE_MAP.put("MS", "EgyptAir");
        AIRLINE_MAP.put("EK", "Emirates");
        AIRLINE_MAP.put("LX", "Swiss International Air Lines");
        AIRLINE_MAP.put("ET", "Ethiopian Airlines");
        AIRLINE_MAP.put("AT", "Royal Air Maroc");
        AIRLINE_MAP.put("TK", "Turkish Airlines");
        AIRLINE_MAP.put("SN", "Brussels Airlines");
        AIRLINE_MAP.put("LH", "Lufthansa");
        AIRLINE_MAP.put("VS", "Virgin Atlantic");
        AIRLINE_MAP.put("HF", "Air Côte d'Ivoire");
    }

    public static String getAirlineName(String code) {
        return AIRLINE_MAP.getOrDefault(code, "Unknown Airline");
    }
}
