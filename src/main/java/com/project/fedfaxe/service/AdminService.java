package com.project.fedfaxe.service;

import com.project.fedfaxe.model.Admin;
import com.project.fedfaxe.model.dto.SetPasswordRequest;
import com.project.fedfaxe.repository.AdminRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder; // Ensure you have this configured

    public AdminService(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void setPassword(SetPasswordRequest request) {
        Admin admin = adminRepository.findByEmail(request.getAdminEmail())
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        // 🔹 Check if the old password matches the stored password
        if (!passwordEncoder.matches(request.getOldPassword(), admin.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        // 🔹 Ensure new password and confirm password match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("New password and confirm password do not match");
        }

        // 🔹 Update the password and mark the admin as active
        admin.setPassword(passwordEncoder.encode(request.getNewPassword()));
        admin.setActive(true); // Assuming you have an "active" flag

        adminRepository.save(admin);
    }
}
