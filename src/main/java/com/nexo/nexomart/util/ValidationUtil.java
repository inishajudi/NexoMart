package com.nexo.nexomart.util;

import java.util.regex.Pattern;

/**
 * Shared, dependency-free validation helpers used by the service layer.
 * Validation always happens in service methods before any DAO call
 * (spec Section 13 rule 5).
 */
public final class ValidationUtil {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private ValidationUtil() {
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        // Minimum 8 characters. Extend with complexity rules as needed later.
        return password != null && password.length() >= 8;
    }
}
