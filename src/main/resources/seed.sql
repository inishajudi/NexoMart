-- NexoMart seed data.
-- NOT run automatically (kept separate from schema.sql per spec Section 14 rule 3).
-- Run manually against your DB once schema.sql has been applied, e.g.:
--   via the H2 console, or: java -cp h2*.jar org.h2.tools.RunScript -url <jdbcUrl> -script seed.sql

-- 1) Seed the ADMIN account (F1: admin has no public signup flow).
--    Generate a real hash first - do NOT commit a guessed one:
--      mvn -q exec:java -Dexec.mainClass=com.nexo.nexomart.util.GenerateAdminHash -Dexec.args="YourChosenPassword"
--    Then replace REPLACE_WITH_BCRYPT_HASH below with the printed value.
INSERT INTO users (name, email, password_hash, role, created_at)
VALUES ('NexoMart Admin', 'admin@nexomart.local', 'REPLACE_WITH_BCRYPT_HASH', 'ADMIN', CURRENT_TIMESTAMP);

-- 2) A couple of demo buyer/seller accounts for local testing.
--    Password for both, once hashed the same way, is intentionally left as a
--    placeholder too - generate your own via GenerateAdminHash before using.
INSERT INTO users (name, email, password_hash, role, created_at)
VALUES ('Demo Seller', 'seller@nexomart.local', 'REPLACE_WITH_BCRYPT_HASH', 'SELLER', CURRENT_TIMESTAMP);

INSERT INTO users (name, email, password_hash, role, created_at)
VALUES ('Demo Buyer', 'buyer@nexomart.local', 'REPLACE_WITH_BCRYPT_HASH', 'BUYER', CURRENT_TIMESTAMP);

-- Product/order seed rows will be added from Week 2 once the products table
-- is populated through the seller flow.
