package com.nexomart.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.nexomart.app.dao.UserDao;
import com.nexomart.app.dto.RegisterRequest;
import com.nexomart.app.exception.AuthenticationException;
import com.nexomart.app.exception.ValidationException;
import com.nexomart.app.model.User;
import com.nexomart.app.util.PasswordUtil;

class UserServiceTest {

    private UserDao userDao;
    private UserService service;

    @BeforeEach
    void setUp() {
        userDao = mock(UserDao.class);
        service = new UserService(userDao);
    }

    private RegisterRequest request(String name, String email, String password, String role) {
        RegisterRequest r = mock(RegisterRequest.class);
        when(r.getName()).thenReturn(name);
        when(r.getEmail()).thenReturn(email);
        when(r.getPassword()).thenReturn(password);
        when(r.getRole()).thenReturn(role);
        return r;
    }

    @Test
    void registerRejectsUnknownRole() {
        RegisterRequest r = request("Asha", "asha@nexomart.com", "password123", "HACKER");
        assertThrows(ValidationException.class, () -> service.register(r));
        verify(userDao, never()).insert(any(User.class));
    }

    @Test
    void registerRejectsDuplicateEmail() {
        when(userDao.existsByEmail("asha@nexomart.com")).thenReturn(true);
        RegisterRequest r = request("Asha", "asha@nexomart.com", "password123", "BUYER");
        assertThrows(ValidationException.class, () -> service.register(r));
        verify(userDao, never()).insert(any(User.class));
    }

    @Test
    void registerSavesUserWithLowercasedEmailAndHashedPassword() throws Exception {
        when(userDao.existsByEmail("new@nexomart.com")).thenReturn(false);
        when(userDao.insert(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        RegisterRequest r = request("New User", "New@NexoMart.com", "password123", "SELLER");

        User saved = service.register(r);

        assertEquals("new@nexomart.com", saved.getEmail());
        assertEquals(User.Role.SELLER, saved.getRole());
        assertEquals(true, PasswordUtil.verify("password123", saved.getPasswordHash()));
    }

    @Test
    void authenticateFailsForUnknownEmail() {
        when(userDao.findByEmail("nobody@nexomart.com")).thenReturn(Optional.empty());
        assertThrows(AuthenticationException.class, () -> service.authenticate("nobody@nexomart.com", "whatever1"));
    }

    @Test
    void authenticateFailsForWrongPasswordAndSucceedsForRightOne() throws Exception {
        User user = mock(User.class);
        when(user.getPasswordHash()).thenReturn(PasswordUtil.hash("secret123"));
        when(userDao.findByEmail("a@nexomart.com")).thenReturn(Optional.of(user));

        assertThrows(AuthenticationException.class, () -> service.authenticate("a@nexomart.com", "wrong-pass"));
        assertEquals(user, service.authenticate("a@nexomart.com", "secret123"));
    }
}