-- Passwords below are bcrypt hashes (jBCrypt-compatible, $2a$ prefix, cost 12).
-- All demo users share the same password: nexomart123
-- Plaintext demo passwords are documented in README.md - never store plaintext.

INSERT INTO users (name, email, password_hash, role, created_at) VALUES
    ('Kavi Buyer',       'buyer1@nexomart.com',  '$2a$12$sS.8BwD5OyozOVuhTBNjhep2TX1OLm5ZxCjfQ1exYkEkn2bhL.wGK', 'BUYER',  CURRENT_TIMESTAMP),
    ('Krish Buyer',       'buyer2@nexomart.com',  '$2a$12$sS.8BwD5OyozOVuhTBNjhep2TX1OLm5ZxCjfQ1exYkEkn2bhL.wGK', 'BUYER',  CURRENT_TIMESTAMP),
    ('Inisha Seller',     'seller1@nexomart.com', '$2a$12$sS.8BwD5OyozOVuhTBNjhep2TX1OLm5ZxCjfQ1exYkEkn2bhL.wGK', 'SELLER', CURRENT_TIMESTAMP),
    ('Maha Seller',   'seller2@nexomart.com', '$2a$12$sS.8BwD5OyozOVuhTBNjhep2TX1OLm5ZxCjfQ1exYkEkn2bhL.wGK', 'SELLER', CURRENT_TIMESTAMP),
    ('adminMahalakshmi', 'admin@nexomart.com',   '$2a$12$sS.8BwD5OyozOVuhTBNjhep2TX1OLm5ZxCjfQ1exYkEkn2bhL.wGK', 'ADMIN',  CURRENT_TIMESTAMP),
    ('Keerthi Nair',       'seller3@nexomart.com', '$2a$12$sS.8BwD5OyozOVuhTBNjhep2TX1OLm5ZxCjfQ1exYkEkn2bhL.wGK', 'SELLER', CURRENT_TIMESTAMP),
    ('Divya Menon',      'seller4@nexomart.com', '$2a$12$sS.8BwD5OyozOVuhTBNjhep2TX1OLm5ZxCjfQ1exYkEkn2bhL.wGK', 'SELLER', CURRENT_TIMESTAMP);

-- ── SKIN CARE (8 products) ──────────────────────────────────────────────────
-- seller1 (Meera Seller, id=3)
INSERT INTO products (seller_id, name, description, price, stock_qty, category, image_url, created_at) VALUES
    (3, 'Cetaphil Gentle Skin Cleanser 250ml',
     'Cetaphil. Mild, soap-free face wash for sensitive and dry skin. Removes dirt and oil without stripping moisture. Dermatologist recommended.',
     349.00, 80, 'Skin Care',
     'https://images.pexels.com/photos/4202325/pexels-photo-4202325.jpeg',
     CURRENT_TIMESTAMP),

    (3, 'Cetaphil Moisturizing Cream 250g',
     'Cetaphil. Rich, non-greasy moisturizing cream that provides 48-hour hydration for dry to very dry skin. Fragrance-free and non-comedogenic.',
     499.00, 60, 'Skin Care',
     'https://images.pexels.com/photos/3762879/pexels-photo-3762879.jpeg',
     CURRENT_TIMESTAMP),

    (3, 'Dot & Key Watermelon Cooling Toner 200ml',
     'Dot & Key. Hydrating face toner with watermelon extract that soothes and balances skin. Alcohol-free formula suitable for all skin types.',
     395.00, 50, 'Skin Care',
     'https://images.pexels.com/photos/6621374/pexels-photo-6621374.jpeg',
     CURRENT_TIMESTAMP),

    (3, 'Dot & Key Vitamin C Serum 30ml',
     'Dot & Key. Brightening Vitamin C serum with niacinamide that fades dark spots, evens skin tone and boosts radiance. Lightweight and fast-absorbing.',
     595.00, 45, 'Skin Care',
     'https://images.pexels.com/photos/4041392/pexels-photo-4041392.jpeg',
     CURRENT_TIMESTAMP),

    (3, 'The Minimalist Niacinamide 10% Serum 30ml',
     'The Minimalist. High-strength niacinamide serum that visibly reduces pores, controls sebum and improves uneven skin tone. Suitable for oily and acne-prone skin.',
     599.00, 70, 'Skin Care',
     'https://images.pexels.com/photos/6621462/pexels-photo-6621462.jpeg',
     CURRENT_TIMESTAMP),

    (3, 'The Minimalist SPF 50 Sunscreen 50ml',
     'The Minimalist. Lightweight, non-greasy sunscreen with broad-spectrum SPF 50 PA+++ protection. Water-resistant formula with no white cast.',
     349.00, 90, 'Skin Care',
     'https://images.pexels.com/photos/3621234/pexels-photo-3621234.jpeg',
     CURRENT_TIMESTAMP),

    (3, 'Mamaearth Ubtan Face Wash 100ml',
     'Mamaearth. Natural ubtan face wash with turmeric and saffron that brightens skin and removes tan. Made with toxin-free, natural ingredients.',
     249.00, 100, 'Skin Care',
     'https://images.pexels.com/photos/4202327/pexels-photo-4202327.jpeg',
     CURRENT_TIMESTAMP),

    (3, 'Mamaearth Vitamin C Face Cream 50g',
     'Mamaearth. Daily moisturizer with Vitamin C and turmeric that reduces dark spots and gives skin a healthy glow. SPF 20 protection included.',
     399.00, 75, 'Skin Care',
     'https://images.pexels.com/photos/3997373/pexels-photo-3997373.jpeg',
     CURRENT_TIMESTAMP);

-- ── BEAUTY (7 products) ─────────────────────────────────────────────────────
-- seller2 (Karthik Seller, id=4)
INSERT INTO products (seller_id, name, description, price, stock_qty, category, image_url, created_at) VALUES
    (4, 'MAC Matte Lipstick - Ruby Woo',
     'MAC. Iconic retro matte lipstick in the cult shade Ruby Woo. Vivid blue-red with a classic matte finish. Long-lasting formula with full coverage.',
     1850.00, 40, 'Beauty',
     'https://images.pexels.com/photos/4938509/pexels-photo-4938509.jpeg',
     CURRENT_TIMESTAMP),

    (4, 'MAC Studio Fix Powder Plus Foundation',
     'MAC. Matte powder foundation that provides medium-to-full coverage with a natural finish. Controls oil and minimizes pores for up to 8 hours.',
     3200.00, 30, 'Beauty',
     'https://images.pexels.com/photos/17679435/pexels-photo-17679435.jpeg',
     CURRENT_TIMESTAMP),

    (4, 'Swiss Beauty Ultra Smooth Lip Color',
     'Swiss Beauty. Creamy, hydrating lipstick with smooth glide and rich pigment in 12 shades. Enriched with Vitamin E for soft, moisturized lips.',
     199.00, 120, 'Beauty',
     'https://images.pexels.com/photos/4834671/pexels-photo-4834671.jpeg',
     CURRENT_TIMESTAMP),

    (4, 'Swiss Beauty Waterproof Kajal',
     'Swiss Beauty. Intense black kajal pencil with a smooth, smudge-proof formula. Enriched with almond oil for comfortable all-day wear.',
     149.00, 150, 'Beauty',
     'https://images.pexels.com/photos/4857812/pexels-photo-4857812.jpeg',
     CURRENT_TIMESTAMP),

    (4, 'Swiss Beauty 12-Shade Eyeshadow Palette',
     'Swiss Beauty. Blendable eyeshadow palette with 6 matte and 6 shimmer shades. Perfect for day-to-night looks with long-lasting pigment.',
     399.00, 55, 'Beauty',
     'https://images.pexels.com/photos/13534390/pexels-photo-13534390.jpeg',
     CURRENT_TIMESTAMP),

    (4, 'MAC Prep + Prime Fix+ Setting Spray 100ml',
     'MAC. Lightweight setting spray that hydrates, refreshes and sets makeup for a longer-lasting finish. Infused with green tea, chamomile and cucumber.',
     2700.00, 25, 'Beauty',
     'https://images.pexels.com/photos/6621374/pexels-photo-6621374.jpeg',
     CURRENT_TIMESTAMP),

    (4, 'Swiss Beauty Professional Makeup Brush Set',
     'Swiss Beauty. Set of 12 professional makeup brushes for face and eye application. Soft synthetic bristles with a sturdy handle, comes in a zip pouch.',
     599.00, 45, 'Beauty',
     'https://images.pexels.com/photos/7256112/pexels-photo-7256112.jpeg',
     CURRENT_TIMESTAMP);