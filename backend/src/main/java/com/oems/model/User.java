package com.oems.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "users")
public class User {

    @Id
    private String id;

    private String username;
    private String email;
    private String password;
    private String fullName;
    private Role role;

    @Builder.Default
    private boolean active = true;

    @Builder.Default
    private int failedLoginAttempts = 0;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
