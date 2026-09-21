package com.nexomart.app.util;

import org.mindrot.jbcrypt.BCrypt;

/** Hashing and verification of passwords. Plaintext passwords are never stored or logged. */
public final class PasswordUtil {

    private static final int LOG_ROUNDS = 12;

    private PasswordUtil() {
    }

    public static String hash(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(LOG_ROUNDS));
    }

    public static boolean verify(String plainTextPassword, String storedHash) {
        if (plainTextPassword == null || storedHash == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainTextPassword, storedHash);
        } catch (IllegalArgumentException e) {
            // malformed hash in DB - treat as non-match rather than propagating
            return false;
        }
    }
}
