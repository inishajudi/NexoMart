package com.nexo.nexomart.service;

import com.nexo.nexomart.dao.UserDAO;
import com.nexo.nexomart.exception.ValidationException;
import com.nexo.nexomart.model.Role;
import com.nexo.nexomart.model.User;
import com.nexo.nexomart.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Business rule tests per spec Section 9 (Service layer, DAO mocked).
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserDAO userDAO;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userDAO);
    }

    @Test
    void registerRejectsInvalidEmail() {
        assertThrows(ValidationException.class,
                () -> authService.register("Name", "not-an-email", "password123", Role.BUYER));
        verifyNoInteractions(userDAO);
    }

    @Test
    void registerRejectsShortPassword() {
        assertThrows(ValidationException.class,
                () -> authService.register("Name", "a@b.com", "short", Role.BUYER));
    }

    @Test
    void registerRejectsAdminRole() {
        assertThrows(ValidationException.class,
                () -> authService.register("Name", "a@b.com", "password123", Role.ADMIN));
    }

    @Test
    void registerRejectsDuplicateEmail() {
        when(userDAO.existsByEmail("a@b.com")).thenReturn(true);

        assertThrows(ValidationException.class,
                () -> authService.register("Name", "a@b.com", "password123", Role.BUYER));
    }

    @Test
    void registerHashesPasswordAndInsertsUser() throws ValidationException {
        when(userDAO.existsByEmail("a@b.com")).thenReturn(false);
        when(userDAO.insert(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = authService.register("Name", "a@b.com", "password123", Role.SELLER);

        assertEquals("a@b.com", result.getEmail());
        assertNotEquals("password123", result.getPasswordHash());
        verify(userDAO).insert(any(User.class));
    }

    @Test
    void authenticateReturnsUserOnCorrectPassword() throws ValidationException {
        String hash = PasswordUtil.hash("correct-password");
        User existing = new User(1L, "Name", "a@b.com", hash, Role.BUYER, null);
        when(userDAO.findByEmail("a@b.com")).thenReturn(Optional.of(existing));

        Optional<User> result = authService.authenticate("a@b.com", "correct-password");

        assertTrue(result.isPresent());
    }

    @Test
    void authenticateReturnsEmptyOnWrongPassword() throws ValidationException {
        String hash = PasswordUtil.hash("correct-password");
        User existing = new User(1L, "Name", "a@b.com", hash, Role.BUYER, null);
        when(userDAO.findByEmail("a@b.com")).thenReturn(Optional.of(existing));

        Optional<User> result = authService.authenticate("a@b.com", "wrong-password");

        assertTrue(result.isEmpty());
    }

    @Test
    void authenticateRejectsBlankInput() {
        assertThrows(ValidationException.class, () -> authService.authenticate("", ""));
    }
}
