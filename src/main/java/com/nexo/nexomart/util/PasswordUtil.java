package com.nexo.nexomart.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * All password hashing goes through here. Per Section 2 rule 2:
 * passwords hashed with bcrypt (jBCrypt), never plaintext, never MD5/SHA1-only.
 */
public final class PasswordUtil {

    private static final int WORK_FACTOR = 12;

    private PasswordUtil() {
    }

    public static String hash(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(WORK_FACTOR));
    }

    public static boolean matches(String plainTextPassword, String hash) {
        if (plainTextPassword == null || hash == null) {
            return false;
        }
        return BCrypt.checkpw(plainTextPassword, hash);
    }
}
