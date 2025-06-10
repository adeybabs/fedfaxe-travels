package com.project.fedfaxe.config;

import com.project.fedfaxe.model.Admin;
import com.project.fedfaxe.model.User;
import com.project.fedfaxe.repository.AdminRepository;
import com.project.fedfaxe.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;
import java.util.Optional;

public class UserLocaleResolver implements LocaleResolver {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    public UserLocaleResolver(UserRepository userRepository, AdminRepository adminRepository) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String email = auth.getName(); // assuming username is email

            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                String langTag = userOpt.get().getPreferredLanguage();
                if (langTag != null && !langTag.isBlank()) {
                    return Locale.forLanguageTag(langTag);
                }
            }

        // Then check in Admin repository
        Optional<Admin> adminOpt = adminRepository.findByEmail(email);
        if (adminOpt.isPresent()) {
            String langTag = adminOpt.get().getPreferredLanguage();
            if (langTag != null && !langTag.isBlank()) {
                return Locale.forLanguageTag(langTag);
            }
        }
        }

        // fallback locale (default)
        return Locale.ENGLISH;
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        // Usually unused - locale is determined dynamically from user preferences
    }
}
