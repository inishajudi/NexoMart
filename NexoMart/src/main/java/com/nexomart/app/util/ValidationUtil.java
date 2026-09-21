package com.nexomart.app.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;

import com.nexomart.app.exception.ValidationException;

/** Small collection of reusable validation checks for user-supplied input. */
public final class ValidationUtil {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private ValidationUtil() {
    }

    public static void requireNonBlank(String value, String fieldName) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " is required.");
        }
    }

    public static void requireValidEmail(String email) throws ValidationException {
        requireNonBlank(email, "Email");
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new ValidationException("Email address is not valid.");
        }
    }

    public static void requireMinLength(String value, int minLength, String fieldName) throws ValidationException {
        if (value == null || value.length() < minLength) {
            throw new ValidationException(fieldName + " must be at least " + minLength + " characters.");
        }
    }

    public static void requirePositive(BigDecimal value, String fieldName) throws ValidationException {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException(fieldName + " must be greater than zero.");
        }
    }

    public static void requireNonNegative(int value, String fieldName) throws ValidationException {
        if (value < 0) {
            throw new ValidationException(fieldName + " cannot be negative.");
        }
    }

    public static void requirePositiveInt(int value, String fieldName) throws ValidationException {
        if (value <= 0) {
            throw new ValidationException(fieldName + " must be greater than zero.");
        }
    }
}
