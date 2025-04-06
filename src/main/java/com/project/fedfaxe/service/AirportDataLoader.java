package com.project.fedfaxe.service;

import com.project.fedfaxe.model.Airport;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


@Component
public class AirportDataLoader {

    private static final Logger logger = LoggerFactory.getLogger(AirportDataLoader.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @PostConstruct
    public void loadData() throws IOException {
        // Check if data already exists
        if (mongoTemplate.count(new Query(), "airports") > 0) {
            logger.info("Airport data already loaded");
            return;
        }


        logger.info("Loading airport data from OpenFlights database...");
        List<Airport> airports = new ArrayList<>();

        // Parse airports.dat file (CSV format with quoted fields)
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream("data/airports.dat")))) {
            //try (BufferedReader reader = new BufferedReader(new FileReader("data/airports.dat"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = parseCsvLine(line);

                // Skip if no IATA code
                if (fields[4].isEmpty() || fields[4].equals("\\N")) {
                    continue;
                }

                Airport airport = new Airport();
                airport.setId(fields[0]);
                airport.setName(fields[1]);
                airport.setCity(fields[2]);
                airport.setCountry(fields[3]);
                airport.setIataCode(fields[4]);
                airport.setIcaoCode(fields[5]);
                airport.setLatitude(Double.parseDouble(fields[6]));
                airport.setLongitude(Double.parseDouble(fields[7]));
                airport.setAltitude(Integer.parseInt(fields[8]));
                airport.setTimezone(Float.parseFloat(fields[9]));
                airport.setDst(fields[10]);
                airport.setTimezoneName(fields[11]);

                airports.add(airport);

                // Insert in batches
                if (airports.size() >= 1000) {
                    mongoTemplate.insertAll(airports);
                    airports.clear();
                }
            }

            // Insert remaining airports
            if (!airports.isEmpty()) {
                mongoTemplate.insertAll(airports);
            }

            logger.info("Airport data loaded successfully");
        }
    }

    // Handle quoted CSV fields properly
    private String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder currentField = new StringBuilder();

        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(currentField.toString());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }
        fields.add(currentField.toString());

        return fields.toArray(new String[0]);
    }
}
