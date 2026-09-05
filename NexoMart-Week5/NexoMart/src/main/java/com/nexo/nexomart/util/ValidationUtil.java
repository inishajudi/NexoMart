package com.nexo.nexomart.util;

import com.nexo.nexomart.exception.ValidationException;

import java.math.BigDecimal;

/** Small, dependency-free validation helpers used at the top of service methods,
 *  per Section 13 rule 5 (validate before any DAO call). */
public final class ValidationUtil {

    private ValidationUtil() { }

    public static void requireNotBlank(String value, String field) throws ValidationException {
        if (value == null || value.isBlank()) {
            throw new ValidationException(field, field + " must not be blank");
        }
    }

    public static void requirePositive(BigDecimal value, String field) throws ValidationException {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException(field, field + " must be greater than zero");
        }
    }

    public static void requirePositive(Integer value, String field) throws ValidationException {
        if (value == null || value <= 0) {
            throw new ValidationException(field, field + " must be a positive integer");
        }
    }

    public static void requireNonNegative(Integer value, String field) throws ValidationException {
        if (value == null || value < 0) {
            throw new ValidationException(field, field + " must not be negative");
        }
    }
}
