-- Passwords below are bcrypt hashes (jBCrypt-compatible, $2a$ prefix, cost 12).
-- All demo users share the same password: nexomart123
-- Plaintext demo passwords are documented in README.md - never store plaintext.

INSERT INTO users (name, email, password_hash, role, created_at) VALUES
    ('Asha Buyer',       'buyer1@nexomart.com',  '$2a$12$sS.8BwD5OyozOVuhTBNjhep2TX1OLm5ZxCjfQ1exYkEkn2bhL.wGK', 'BUYER',  CURRENT_TIMESTAMP),
    ('Ravi Buyer',       'buyer2@nexomart.com',  '$2a$12$sS.8BwD5OyozOVuhTBNjhep2TX1OLm5ZxCjfQ1exYkEkn2bhL.wGK', 'BUYER',  CURRENT_TIMESTAMP),
    ('Meera Seller',     'seller1@nexomart.com', '$2a$12$sS.8BwD5OyozOVuhTBNjhep2TX1OLm5ZxCjfQ1exYkEkn2bhL.wGK', 'SELLER', CURRENT_TIMESTAMP),
    ('Karthik Seller',   'seller2@nexomart.com', '$2a$12$sS.8BwD5OyozOVuhTBNjhep2TX1OLm5ZxCjfQ1exYkEkn2bhL.wGK', 'SELLER', CURRENT_TIMESTAMP),
    ('adminMahalakshmi', 'admin@nexomart.com',   '$2a$12$sS.8BwD5OyozOVuhTBNjhep2TX1OLm5ZxCjfQ1exYkEkn2bhL.wGK', 'ADMIN',  CURRENT_TIMESTAMP),
    ('Priya Nair',       'seller3@nexomart.com', '$2a$12$sS.8BwD5OyozOVuhTBNjhep2TX1OLm5ZxCjfQ1exYkEkn2bhL.wGK', 'SELLER', CURRENT_TIMESTAMP),
    ('Divya Menon',     'seller4@nexomart.com', '$2a$12$sS.8BwD5OyozOVuhTBNjhep2TX1OLm5ZxCjfQ1exYkEkn2bhL.wGK', 'SELLER', CURRENT_TIMESTAMP);

-- seller1 (Meera Seller, id=3) products - Electronics
INSERT INTO products (seller_id, name, description, price, stock_qty, category, image_url, created_at) VALUES
    (3, 'Wireless Mouse', 'Ergonomic 2.4GHz wireless mouse with USB receiver.', 599.00, 50, 'Electronics', 'https://images.pexels.com/photos/7172700/pexels-photo-7172700.jpeg', CURRENT_TIMESTAMP),
    (3, 'USB-C Charger 65W', 'Fast-charging GaN wall charger with USB-C PD.', 1299.00, 30, 'Electronics', 'https://images.pexels.com/photos/32710069/pexels-photo-32710069.jpeg', CURRENT_TIMESTAMP),
    (3, 'Bluetooth Headphones', 'Over-ear headphones with 30-hour battery life.', 2499.00, 20, 'Electronics', 'https://images.pexels.com/photos/30345418/pexels-photo-30345418.jpeg', CURRENT_TIMESTAMP),
    (3, 'Wireless Earbuds', 'True wireless earbuds with clear sound, touch controls and a charging case for up to 24 hours of playback.', 1799.00, 45, 'Electronics', 'https://images.pexels.com/photos/11599423/pexels-photo-11599423.jpeg', CURRENT_TIMESTAMP),
    (3, 'Smartphone 128GB', '6.5-inch display, 128GB storage, 50MP camera and a long-lasting battery.', 15999.00, 25, 'Electronics', 'https://images.pexels.com/photos/3999536/pexels-photo-3999536.jpeg', CURRENT_TIMESTAMP);

-- seller2 (Karthik Seller, id=4) products - Home & Kitchen
INSERT INTO products (seller_id, name, description, price, stock_qty, category, image_url, created_at) VALUES
    (4, 'Stainless Steel Water Bottle', 'Insulated 1L bottle, keeps drinks cold for 24 hours.', 449.00, 100, 'Home & Kitchen', 'https://images.pexels.com/photos/25382222/pexels-photo-25382222.jpeg', CURRENT_TIMESTAMP),
    (4, 'Plates', 'Set of durable ceramic dinner plates, dishwasher and microwave safe.', 799.00, 60, 'Home & Kitchen', 'https://images.pexels.com/photos/7671231/pexels-photo-7671231.jpeg', CURRENT_TIMESTAMP),
    (4, 'Handcrafted Kitchen Utensil Set', 'Durable acacia wood utensil set including a straining spoon, slotted turner, and salad tongs, stored in a matching wooden caddy.', 349.00, 40, 'Home & Kitchen', 'https://images.pexels.com/photos/30798748/pexels-photo-30798748.jpeg', CURRENT_TIMESTAMP),
    (4, 'Scented Candle', 'Hand-poured soy wax candle with a soothing fragrance and a burn time of around 40 hours.', 449.00, 55, 'Home & Kitchen', 'https://images.pexels.com/photos/11137699/pexels-photo-11137699.jpeg', CURRENT_TIMESTAMP),
    (4, 'Stainless Steel Measuring Spoons', 'Durable stainless steel measuring spoon set with engraved markings for accurate cooking and baking.', 249.00, 90, 'Home & Kitchen', 'https://images.pexels.com/photos/9737802/pexels-photo-9737802.jpeg', CURRENT_TIMESTAMP);

-- seller3 (Priya Nair, id=6) products - Baby Toys
INSERT INTO products (seller_id, name, description, price, stock_qty, category, image_url, created_at) VALUES
    (6, 'Soft Wooden Rattles', 'Smooth, baby-safe wooden rattles with a gentle sound. Easy to grip and free from harmful paints.', 349.00, 60, 'Baby Toys', 'https://images.pexels.com/photos/9271755/pexels-photo-9271755.jpeg', CURRENT_TIMESTAMP),
    (6, 'Shape Sorter Toy', 'Colourful shape sorter that builds hand-eye coordination and early problem-solving skills.', 599.00, 40, 'Baby Toys', 'https://images.pexels.com/photos/11030155/pexels-photo-11030155.jpeg', CURRENT_TIMESTAMP),
    (6, 'Soft Plush Toys', 'Cuddly, hypoallergenic plush toys made from skin-friendly fabric, suitable for newborns and toddlers.', 499.00, 50, 'Baby Toys', 'https://images.pexels.com/photos/22729433/pexels-photo-22729433.jpeg', CURRENT_TIMESTAMP),
    (6, 'Colourful Stacking Rings', 'Classic stacking rings toy that helps toddlers learn colours, sizes and coordination.', 399.00, 45, 'Baby Toys', 'https://images.pexels.com/photos/9271757/pexels-photo-9271757.jpeg', CURRENT_TIMESTAMP),
    (6, 'Toy Xylophone', 'Bright wooden xylophone with two mallets, a fun first musical instrument for kids.', 549.00, 35, 'Baby Toys', 'https://images.pexels.com/photos/6743155/pexels-photo-6743155.jpeg', CURRENT_TIMESTAMP);

-- seller4 (Divya Menon, id=7) products - Makeup
INSERT INTO products (seller_id, name, description, price, stock_qty, category, image_url, created_at) VALUES
    (7, 'Matte Lipstick', 'Long-lasting matte lipstick with rich colour payoff and a comfortable, non-drying finish.', 399.00, 80, 'Makeup', 'https://images.pexels.com/photos/4938509/pexels-photo-4938509.jpeg', CURRENT_TIMESTAMP),
    (7, 'Volumizing Mascara', 'Smudge-proof mascara that lengthens and volumizes lashes for all-day wear.', 349.00, 70, 'Makeup', 'https://images.pexels.com/photos/4857812/pexels-photo-4857812.jpeg', CURRENT_TIMESTAMP),
    (7, 'Makeup Brush Set', 'Set of soft, dense brushes for face and eyes, supplied with a travel pouch.', 799.00, 40, 'Makeup', 'https://images.pexels.com/photos/7256112/pexels-photo-7256112.jpeg', CURRENT_TIMESTAMP),
    (7, 'Eyeshadow Palette', 'Blendable eyeshadow palette with matte and shimmer shades for day and evening looks.', 899.00, 35, 'Makeup', 'https://images.pexels.com/photos/13534390/pexels-photo-13534390.jpeg', CURRENT_TIMESTAMP),
    (7, 'Compact Powder', 'Lightweight compact powder that sets makeup and controls shine with a natural matte finish.', 299.00, 65, 'Makeup', 'https://images.pexels.com/photos/17679435/pexels-photo-17679435.jpeg', CURRENT_TIMESTAMP);