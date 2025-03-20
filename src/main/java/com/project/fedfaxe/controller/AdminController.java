package com.project.fedfaxe.controller;

import com.project.fedfaxe.model.Admin;
import com.project.fedfaxe.model.dto.AdminLoginRequest;
import com.project.fedfaxe.model.dto.SetPasswordRequest;
import com.project.fedfaxe.repository.AdminRepository;
import com.project.fedfaxe.service.AdminService;
import com.project.fedfaxe.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminService adminService;

    @Autowired
    private JwtUtil jwtUtil;

    public AdminController(AdminRepository adminRepository, PasswordEncoder passwordEncoder, AdminService adminService) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminService = adminService;
    }


    @Operation(summary = "Admin login", description = "Allows an admin to log in using their email and password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ 'status': 'SUCCESS', 'message': 'Login successful', 'token': 'jwt-token-here' }"))),
            @ApiResponse(responseCode = "403", description = "Password must be changed before accessing the system",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ 'status': 'FORBIDDEN', 'message': 'Password must be changed before accessing the system' }"))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ 'status': 'UNAUTHORIZED', 'message': 'Invalid email or password' }")))
    })
    @PostMapping("/login")
    public ResponseEntity<?> adminLogin(@RequestBody AdminLoginRequest loginRequest) {
        Optional<Admin> adminOpt = adminRepository.findByEmail(loginRequest.getEmail());

        if (adminOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }

        Admin admin = adminOpt.get();

        // Verify password
        if (!passwordEncoder.matches(loginRequest.getPassword(), admin.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }

        // Check if admin must change password
        if (!admin.isActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Password must be changed before accessing the system");
        }

        // Generate JWT Token using JwtUtil
        String token = jwtUtil.generateToken(admin.getEmail());

        return ResponseEntity.ok(Map.of("status", "SUCCESS", "message", "Login successful", "token", token));

    }


    @Operation(summary = "Set new admin password", description = "Allows an admin to set a new password after logging in with the default credentials.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ 'status': 'SUCCESS', 'message': 'Password changed successfully' }"))),
            @ApiResponse(responseCode = "400", description = "Invalid request format",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ 'status': 'BAD_REQUEST', 'message': 'Invalid input' }"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized access",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{ 'status': 'UNAUTHORIZED', 'message': 'Invalid token' }")))
    })
    @PostMapping("/set-password")
    public ResponseEntity<String> setPassword(@RequestBody SetPasswordRequest request) {
        adminService.setPassword(request);
        return ResponseEntity.ok("Password changed successfully");
    }
}
