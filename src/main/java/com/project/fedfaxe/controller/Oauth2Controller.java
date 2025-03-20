package com.project.fedfaxe.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "OAuth2 Authentication APIs")
public class Oauth2Controller {

    @Value("${app.backend.url}")
    private String backendUrl;

    @Operation(
            summary = "Google OAuth2 Login",
            description = "Redirects user to Google for authentication. After successful login, user is redirected to the configured frontend URL."
    )
    @GetMapping("/login/google")
    public String googleLogin() {
        return "Redirect to: " + backendUrl + "/oauth2/authorization/google";
    }
}
