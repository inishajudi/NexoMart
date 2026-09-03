package com.nexo.nexomart.util;

/**
 * One-off local utility - NOT a servlet, NOT deployed. Run it once to print a
 * bcrypt hash for whatever admin password you choose, then paste the printed
 * hash into db/migrations/../seed.sql (or src/main/resources/seed.sql) in the
 * password_hash column for the seeded ADMIN row.
 *
 * Usage (after `mvn compile`):
 *   mvn -q exec:java -Dexec.mainClass=com.nexo.nexomart.util.GenerateAdminHash -Dexec.args="YourChosenPassword"
 *
 * We generate the hash this way instead of committing a guessed/shared one,
 * so nobody accidentally ships a known admin password.
 */
public final class GenerateAdminHash {

    private GenerateAdminHash() {
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java GenerateAdminHash <plainTextPassword>");
            return;
        }
        String hash = PasswordUtil.hash(args[0]);
        System.out.println("Bcrypt hash for seed.sql:");
        System.out.println(hash);
    }
}
