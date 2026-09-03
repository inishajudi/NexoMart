package com.nexo.nexomart.dto;

import com.nexo.nexomart.model.Role;
import com.nexo.nexomart.model.User;

/**
 * Outbound shape for user data. Deliberately excludes passwordHash.
 * Per spec Section 13 rule 4: DTOs are separate classes from entities.
 */
public class UserResponseDTO {

    private final Long id;
    private final String name;
    private final String email;
    private final Role role;

    private UserResponseDTO(Long id, String name, String email, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public static UserResponseDTO fromEntity(User user) {
        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }
}
