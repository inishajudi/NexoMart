-- Passwords: bcrypt hashes ($2a$, cost 12) for shared demo password: nexomart123
-- Plaintext demo passwords are documented in README.md - never store plaintext.

INSERT INTO users (name, email, password_hash, role, created_at) VALUES
    ('Kavi',      'buyer1@nexomart.com',  '$2a$12$sS.8BwD5OyozOVuhTBNjhep2TX1OLm5ZxCjfQ1exYkEkn2bhL.wGK', 'BUYER',  CURRENT_TIMESTAMP),
    ('Krish',     'buyer2@nexomart.com',  '$2a$12$sS.8BwD5OyozOVuhTBNjhep2TX1OLm5ZxCjfQ1exYkEkn2bhL.wGK', 'BUYER',  CURRENT_TIMESTAMP),
    ('Priyan',    'seller1@nexomart.com', '$2a$12$sS.8BwD5OyozOVuhTBNjhep2TX1OLm5ZxCjfQ1exYkEkn2bhL.wGK', 'SELLER', CURRENT_TIMESTAMP),
    ('Keerthi',      'seller2@nexomart.com', '$2a$12$sS.8BwD5OyozOVuhTBNjhep2TX1OLm5ZxCjfQ1exYkEkn2bhL.wGK', 'SELLER', CURRENT_TIMESTAMP),
    ('Inisha Judi',      'admin@nexomart.com',   '$2a$12$sS.8BwD5OyozOVuhTBNjhep2TX1OLm5ZxCjfQ1exYkEkn2bhL.wGK', 'ADMIN',  CURRENT_TIMESTAMP),
    ('Madhu',     'seller3@nexomart.com', '$2a$12$sS.8BwD5OyozOVuhTBNjhep2TX1OLm5ZxCjfQ1exYkEkn2bhL.wGK', 'SELLER', CURRENT_TIMESTAMP),
    ('Dharshini', 'seller4@nexomart.com', '$2a$12$sS.8BwD5OyozOVuhTBNjhep2TX1OLm5ZxCjfQ1exYkEkn2bhL.wGK', 'SELLER', CURRENT_TIMESTAMP);

-- seller1 (Priyan, id=3) products - Dresses
INSERT INTO products (seller_id, name, description, price, stock_qty, category, image_url, created_at) VALUES
    (3, 'Cotton Kurti',        'Lightweight and breathable kurti suitable for daily wear and Perfect for casual outings or brunches',                              1599.00, 40, 'Dresses', 'https://images.pexels.com/photos/35485419/pexels-photo-35485419.jpeg',   CURRENT_TIMESTAMP),
    (3, 'T Shirts',           'Comfortable casual top available in various styles and prints',                                      500.00, 35, 'Dresses', 'https://images.pexels.com/photos/6311612/pexels-photo-6311612.jpeg',   CURRENT_TIMESTAMP),
    (3, 'Floral Dress',        'Dress featuring multiple patterns and comfortable',                   1000.00, 30, 'Dresses', 'https://images.pexels.com/photos/30294272/pexels-photo-30294272.jpeg',   CURRENT_TIMESTAMP),
    (3, 'Night Dress',         'Comfortable loose-fitting clothing for sleeping and relaxing',                                         799.00, 50, 'Dresses', 'https://images.pexels.com/photos/25328648/pexels-photo-25328648.jpeg',   CURRENT_TIMESTAMP),
    (3, 'Midi Dress',       'Flattering midi-length dress in a vibrant block print, crafted from soft rayon and Elasticated waist and concealed side zip for a comfortable, neat fit.',                  999.00, 25, 'Dresses', 'https://images.pexels.com/photos/12453986/pexels-photo-12453986.jpeg',   CURRENT_TIMESTAMP);

-- seller2 (Keerthi, id=4) products - Accessories
INSERT INTO products (seller_id, name, description, price, stock_qty, category, image_url, created_at) VALUES
    (4, 'Earrings',   'Trendy studs, hoops and jhumkas',                                                    150.00, 80, 'Accessories', 'https://images.pexels.com/photos/31605846/pexels-photo-31605846.jpeg', CURRENT_TIMESTAMP),
    (4, 'Necklace',         'Fashion jewellery for casual/party wear',                                     500.00, 30, 'Accessories', 'https://images.pexels.com/photos/38101141/pexels-photo-38101141.jpeg',   CURRENT_TIMESTAMP),
    (4, 'Bracelet','Stylish artificial jewellery',                                            200.00, 90, 'Accessories', 'https://images.pexels.com/photos/19784819/pexels-photo-19784819.jpeg',   CURRENT_TIMESTAMP),
    (4, 'Hair Clips',     'Claw clips, hair pins and tic-tac clips',                      100.00, 45, 'Accessories', 'https://images.pexels.com/photos/33343184/pexels-photo-33343184.jpeg',   CURRENT_TIMESTAMP),
    (4, 'Handbags',          'Everyday fashionable handbags',                             799.00, 55, 'Accessories', 'https://images.pexels.com/photos/23223849/pexels-photo-23223849.jpeg',     CURRENT_TIMESTAMP);

-- seller3 (Madhu, id=6) products - Skincare
INSERT INTO products (seller_id, name, description, price, stock_qty, category, image_url, created_at) VALUES
    (6, 'Vitamin C Brightening Serum',  'Lightweight 15% Vitamin C serum that fades dark spots and boosts radiance. Alcohol-free formula suitable for all skin types.',                                       350.00, 60, 'Skincare', 'https://images.pexels.com/photos/28255122/pexels-photo-28255122.jpeg',   CURRENT_TIMESTAMP),
    (6, 'Hydrating Aloe Gel Moisturiser','Gel-cream moisturiser with pure aloe vera and hyaluronic acid for deep, non-greasy hydration. Absorbs quickly, leaving skin plump and calm.',                       499.00, 55, 'Skincare', 'https://images.pexels.com/photos/14798340/pexels-photo-14798340.jpeg',   CURRENT_TIMESTAMP),
    (6, 'Rice Water Gentle Cleanser',   'Mild foam cleanser enriched with fermented rice water and niacinamide. Removes impurities while maintaining the skin barrier. Fragrance-free.',                      699.00, 70, 'Skincare', 'https://images.pexels.com/photos/33343234/pexels-photo-33343234.png',   CURRENT_TIMESTAMP),
    (6, 'SPF 50 Sunscreen Lotion',      'Broad-spectrum UVA/UVB sunscreen with a lightweight, no-white-cast finish. Water-resistant for up to 80 minutes. PA++++ rated.',                                     549.00, 80, 'Skincare', 'https://images.pexels.com/photos/16378486/pexels-photo-16378486.jpeg',   CURRENT_TIMESTAMP),
    (6, 'Moisturiser',   'To improve skin texture and reduce fine lines and smoother skin.',    399.00, 40, 'Skincare', 'https://images.pexels.com/photos/4841230/pexels-photo-4841230.jpeg',   CURRENT_TIMESTAMP);

-- seller4 (Dharshini, id=7) products - Beauty
INSERT INTO products (seller_id, name, description, price, stock_qty, category, image_url, created_at) VALUES
    (7, 'Velvet Matte Lip Colour',      'Highly pigmented matte lip colour with a velvety, featherweight finish. Long-wearing formula that does not dry out lips. Available in 12 shades.',                   399.00, 80, 'Beauty', 'https://images.pexels.com/photos/10037325/pexels-photo-10037325.jpeg',   CURRENT_TIMESTAMP),
    (7, 'Lash Boost Mascara',           'Buildable fibre mascara that adds dramatic length and volume without clumping. Smudge-proof and humidity-resistant for all-day wear.',                               349.00, 70, 'Beauty', 'https://images.pexels.com/photos/10207462/pexels-photo-10207462.png',   CURRENT_TIMESTAMP),
    (7, 'Pro Blending Brush Set',       'Set of 8 professional-grade brushes with ultra-soft synthetic bristles for seamless blending of foundation, contour and eyeshadow. Includes a zip-up travel pouch.',799.00, 40, 'Beauty', 'https://images.pexels.com/photos/7712471/pexels-photo-7712471.jpeg',   CURRENT_TIMESTAMP),
    (7, 'Smoky Nude Eyeshadow Palette', '12-shade palette featuring a curated mix of matte and shimmer nudes and neutrals. Highly blendable formula for effortless everyday and evening eye looks.',          899.00, 35, 'Beauty', 'https://images.pexels.com/photos/6662825/pexels-photo-6662825.jpeg', CURRENT_TIMESTAMP),
    (7, 'HD Setting Powder',            'Ultra-fine translucent setting powder that blurs pores, locks makeup in place and controls shine throughout the day. Fragrance-free, suitable for all skin tones.',  299.00, 65, 'Beauty', 'https://images.pexels.com/photos/16212410/pexels-photo-16212410.jpeg', CURRENT_TIMESTAMP);
