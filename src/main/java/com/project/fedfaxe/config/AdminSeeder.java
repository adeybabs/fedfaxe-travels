package com.project.fedfaxe.config;

import com.project.fedfaxe.model.Admin;
import com.project.fedfaxe.repository.AdminRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdminSeeder implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminSeeder(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        List<Admin> admins = List.of(
                Admin.builder()
                        .email("admin@fedfaxe.com")
                        .password(passwordEncoder.encode("AdminSecret!")) // Securely hash password
                        .active(false)
                        .build(),
                Admin.builder()
                        .email("admin2@fedfaxe.com")
                        .password(passwordEncoder.encode("Admin2Secret!")) // Second admin
                        .active(false)
                        .build()
        );

        // Avoid duplicates by checking if each email exists
        for (Admin admin : admins) {
            if (adminRepository.findByEmail(admin.getEmail()).isEmpty()) {
                adminRepository.save(admin);
            }
        }
    }
}
