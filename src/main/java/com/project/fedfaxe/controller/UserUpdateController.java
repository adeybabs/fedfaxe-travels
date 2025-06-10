package com.project.fedfaxe.controller;

import com.project.fedfaxe.model.User;
import com.project.fedfaxe.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/update/user")
public class UserUpdateController {

    private final UserRepository userRepository;

    public UserUpdateController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PatchMapping("/language")
    public ResponseEntity<Map<String, String>> updatePreferredLanguage(
            @RequestParam("lang") String langCode,
            Authentication authentication
    ) {
        if (authentication == null || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        String email = authentication.getName(); // get the logged-in user's email

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Supported languages (adjust as needed)
        List<String> supportedLanguages = List.of("en", "fr", "es", "ar", "pt", "sw");

        if (!supportedLanguages.contains(langCode)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported language code");
        }

        user.setPreferredLanguage(langCode);
        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Preferred language updated successfully");
        return ResponseEntity.ok(response);
    }
}
