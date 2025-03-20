package com.project.fedfaxe.model;

import com.project.fedfaxe.model.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;
    private String email;
    private String name;
    private String imageUrl;
    private String provider; // GOOGLE
    private String providerId; // Google user ID
    private UserRole role;

}
