package com.project.fedfaxe.config;

import com.project.fedfaxe.model.User;
import com.project.fedfaxe.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauthUser = super.loadUser(userRequest);
        String email = oauthUser.getAttribute("email");
        log.info("User logged in with email: {}", email);

        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isEmpty()) {
            log.info("New user detected. Saving to database.");
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(oauthUser.getAttribute("name"));
            newUser.setImageUrl(oauthUser.getAttribute("picture"));
            newUser.setProvider(userRequest.getClientRegistration().getRegistrationId());
            newUser.setProviderId(oauthUser.getAttribute("sub"));
            userRepository.save(newUser);
        }

        return oauthUser;
    }
}
