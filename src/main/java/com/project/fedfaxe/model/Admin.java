package com.project.fedfaxe.model;

import com.project.fedfaxe.model.enums.UserRole;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "admins")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Admin {

    @Id
    private String id;

    private String email;
    private String password;
    private boolean active;  // Default is false
    private UserRole role;
    private String preferredLanguage; // e.g., "en", "fr", "es"
}
