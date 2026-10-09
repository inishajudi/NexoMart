package com.nexomart.app.dto;

import com.nexomart.app.model.User;

/**
 * Safe user representation returned in API responses.
 * Never exposes passwordHash.
 */
public class UserResponseDTO {

    private long   id;
    private String name;
    private String email;
    private String role;
    private String createdAt;

    /** Private — use the Builder. */
    private UserResponseDTO() {}

    public long   getId()        { return id; }
    public String getName()      { return name; }
    public String getEmail()     { return email; }
    public String getRole()      { return role; }
    public String getCreatedAt() { return createdAt; }

    /** Builder pattern as required by Section 12 of the spec. */
    public static class Builder {
        private final UserResponseDTO dto = new UserResponseDTO();

        public Builder id(long id)             { dto.id        = id;              return this; }
        public Builder name(String name)       { dto.name      = name;            return this; }
        public Builder email(String email)     { dto.email     = email;           return this; }
        public Builder role(String role)       { dto.role      = role;            return this; }
        public Builder createdAt(String value) { dto.createdAt = value;           return this; }

        public UserResponseDTO build()         { return dto; }
    }

    /** Convenience factory — converts a User entity without exposing passwordHash. */
    public static UserResponseDTO from(User user) {
        return new Builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt().toString())
                .build();
    }
}
