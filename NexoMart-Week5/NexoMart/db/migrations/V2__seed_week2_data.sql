-- V2__seed_week2_data.sql
-- Demo data kept separate from schema migrations per Section 14, rule 3.

MERGE INTO users (id, name, email, password_hash, role) KEY(id) VALUES
  (1, 'Admin', 'admin@nexomart.local', '$2a$10$7QqQpQ6f0mR8b9v6M0lQdOeYQ0Y8Q6f0mR8b9v6M0lQdOeYQ0Y8Q6', 'ADMIN'),
  (2, 'Seller One', 'seller1@nexomart.local', '$2a$10$7QqQpQ6f0mR8b9v6M0lQdOeYQ0Y8Q6f0mR8b9v6M0lQdOeYQ0Y8Q6', 'SELLER'),
  (3, 'Buyer One', 'buyer1@nexomart.local', '$2a$10$7QqQpQ6f0mR8b9v6M0lQdOeYQ0Y8Q6f0mR8b9v6M0lQdOeYQ0Y8Q6', 'BUYER');
-- NOTE: password hashes above are placeholders. Regenerate real bcrypt hashes with
-- PasswordUtil.hash("yourPassword") from Week 1 before using this seed for login testing.

MERGE INTO products (id, seller_id, name, description, price, stock_qty, category, image_url) KEY(id) VALUES
  (1, 2, 'Wireless Mouse', 'Ergonomic 2.4GHz wireless mouse', 699.00, 50, 'Electronics', 'https://example.com/img/mouse.jpg'),
  (2, 2, 'Mechanical Keyboard', 'Hot-swappable mechanical keyboard', 3499.00, 20, 'Electronics', 'https://example.com/img/keyboard.jpg'),
  (3, 2, 'Cotton T-Shirt', 'Plain cotton round-neck t-shirt', 399.00, 100, 'Apparel', 'https://example.com/img/tshirt.jpg'),
  (4, 2, 'Notebook Set', 'Pack of 3 ruled notebooks', 249.00, 200, 'Stationery', 'https://example.com/img/notebook.jpg');
