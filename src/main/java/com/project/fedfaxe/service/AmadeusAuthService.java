package com.project.fedfaxe.service;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class AmadeusAuthService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${amadeus.api-key}")
    private String clientId;

    @Value("${amadeus.api-secret}")
    private String clientSecret;

    public String getAccessToken() {
        String url = "https://test.api.amadeus.com/v1/security/oauth2/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "grant_type=client_credentials"
                + "&client_id=" + clientId
                + "&client_secret=" + clientSecret;

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody().get("access_token").toString();
        }
        throw new RuntimeException("Failed to get Amadeus token");
    }
}
