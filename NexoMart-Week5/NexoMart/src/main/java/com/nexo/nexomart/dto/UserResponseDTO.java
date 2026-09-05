package com.nexo.nexomart.dto;

import java.time.LocalDateTime;

/** Per Section 13 rule 4: DTOs are separate classes from entities, and this one must
 *  not contain passwordHash. */
public class UserResponseDTO {
    private final Long id;
    private final String name;
    private final String email;
    private final String role;
    private final LocalDateTime createdAt;

    public UserResponseDTO(Long id, String name, String email, String role, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
