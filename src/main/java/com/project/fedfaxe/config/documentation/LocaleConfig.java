package com.project.fedfaxe.config.documentation;

import com.project.fedfaxe.config.UserLocaleResolver;
import com.project.fedfaxe.repository.AdminRepository;
import com.project.fedfaxe.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.context.annotation.Configuration;


@Configuration
public class LocaleConfig {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    public LocaleConfig(UserRepository userRepository, AdminRepository adminRepository) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
    }

    @Bean
    public LocaleResolver localeResolver() {
        return new UserLocaleResolver(userRepository, adminRepository);
    }
}
