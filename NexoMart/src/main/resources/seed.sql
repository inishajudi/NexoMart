-- Passwords below are bcrypt hashes (jBCrypt-compatible, $2a$ prefix, cost 12).
-- All demo users share the same password: nexomart123

INSERT INTO users (name, email, password_hash, role, created_at) VALUES
    ('Asha Buyer',       'buyer1@nexomart.com',  '$2a$12$sS.8BwD5OyozOVuhTBNjhep2TX1OLm5ZxCjfQ1exYkEkn2bhL.wGK', 'BUYER',  CURRENT_TIMESTAMP),
    ('Ravi Buyer',       'buyer2@nexomart.com',  '$2a$12$sS.8BwD5OyozOVuhTBNjhep2TX1OLm5ZxCjfQ1exYkEkn2bhL.wGK', 'BUYER',  CURRENT_TIMESTAMP),
    ('Meera Seller',     'seller1@nexomart.com', '$2a$12$sS.8BwD5OyozOVuhTBNjhep2TX1OLm5ZxCjfQ1exYkEkn2bhL.wGK', 'SELLER', CURRENT_TIMESTAMP),
    ('Karthik Seller',   'seller2@nexomart.com', '$2a$12$sS.8BwD5OyozOVuhTBNjhep2TX1OLm5ZxCjfQ1exYkEkn2bhL.wGK', 'SELLER', CURRENT_TIMESTAMP),
    ('adminMahalakshmi', 'admin@nexomart.com',   '$2a$12$sS.8BwD5OyozOVuhTBNjhep2TX1OLm5ZxCjfQ1exYkEkn2bhL.wGK', 'ADMIN',  CURRENT_TIMESTAMP),
    ('Priya Nair',       'seller3@nexomart.com', '$2a$12$sS.8BwD5OyozOVuhTBNjhep2TX1OLm5ZxCjfQ1exYkEkn2bhL.wGK', 'SELLER', CURRENT_TIMESTAMP),
    ('Divya Menon',      'seller4@nexomart.com', '$2a$12$sS.8BwD5OyozOVuhTBNjhep2TX1OLm5ZxCjfQ1exYkEkn2bhL.wGK', 'SELLER', CURRENT_TIMESTAMP);

-- ── SKIN CARE (8 products) ──────────────────────────────────────────────────
INSERT INTO products (seller_id, name, description, price, stock_qty, category, image_url, created_at) VALUES

    (3, 'Cetaphil Gentle Skin Cleanser 250ml',
     'Cetaphil. Mild, soap-free face wash for sensitive and dry skin. Removes dirt and oil without stripping moisture. Dermatologist recommended.',
     349.00, 80, 'Skin Care',
     'https://upload.wikimedia.org/wikipedia/commons/thumb/4/4e/Cetaphil_gentle_skin_cleanser.jpg/400px-Cetaphil_gentle_skin_cleanser.jpg',
     CURRENT_TIMESTAMP),

    (3, 'Cetaphil Moisturizing Cream 250g',
     'Cetaphil. Rich, non-greasy moisturizing cream for 48-hour hydration for dry to very dry skin. Fragrance-free and non-comedogenic.',
     499.00, 60, 'Skin Care',
     'https://images.unsplash.com/photo-1556228578-8c89e6adf883?w=400&q=80',
     CURRENT_TIMESTAMP),

    (3, 'Dot & Key Watermelon Cooling Toner 200ml',
     'Dot & Key. Hydrating face toner with watermelon extract. Soothes and balances skin. Alcohol-free formula suitable for all skin types.',
     395.00, 50, 'Skin Care',
     'https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=400&q=80',
     CURRENT_TIMESTAMP),

    (3, 'Dot & Key Vitamin C Serum 30ml',
     'Dot & Key. Brightening Vitamin C serum with niacinamide that fades dark spots and evens skin tone. Lightweight and fast-absorbing.',
     595.00, 45, 'Skin Care',
     'https://images.unsplash.com/photo-1608248597279-f99d160bfcbc?w=400&q=80',
     CURRENT_TIMESTAMP),

    (3, 'The Minimalist Niacinamide 10% Serum 30ml',
     'The Minimalist. High-strength niacinamide serum that reduces pores, controls sebum and improves uneven skin tone. For oily and acne-prone skin.',
     599.00, 70, 'Skin Care',
     'https://images.unsplash.com/photo-1601049676869-702ea24cfd58?w=400&q=80',
     CURRENT_TIMESTAMP),

    (3, 'The Minimalist SPF 50 Sunscreen 50ml',
     'The Minimalist. Lightweight, non-greasy sunscreen with broad-spectrum SPF 50 PA+++ protection. Water-resistant with no white cast.',
     349.00, 90, 'Skin Care',
     'https://images.unsplash.com/photo-1526758097130-bab247274f58?w=400&q=80',
     CURRENT_TIMESTAMP),

    (3, 'Mamaearth Ubtan Face Wash 100ml',
     'Mamaearth. Natural ubtan face wash with turmeric and saffron that brightens skin and removes tan. Made with toxin-free, natural ingredients.',
     249.00, 100, 'Skin Care',
     'https://images.unsplash.com/photo-1556228720-195a672e8a03?w=400&q=80',
     CURRENT_TIMESTAMP),

    (3, 'Mamaearth Vitamin C Face Cream 50g',
     'Mamaearth. Daily moisturizer with Vitamin C and turmeric that reduces dark spots and gives skin a healthy glow. SPF 20 protection included.',
     399.00, 75, 'Skin Care',
     'https://images.unsplash.com/photo-1570194065650-d99fb4b8ccb0?w=400&q=80',
     CURRENT_TIMESTAMP);

-- ── BEAUTY (7 products) ─────────────────────────────────────────────────────
INSERT INTO products (seller_id, name, description, price, stock_qty, category, image_url, created_at) VALUES

    (4, 'MAC Matte Lipstick - Ruby Woo',
     'MAC. Iconic retro matte lipstick in cult shade Ruby Woo. Vivid blue-red with a classic matte finish. Long-lasting with full coverage.',
     1850.00, 40, 'Beauty',
     'https://images.unsplash.com/photo-1586495777744-4e6232bf5ef0?w=400&q=80',
     CURRENT_TIMESTAMP),

    (4, 'MAC Studio Fix Powder Plus Foundation',
     'MAC. Matte powder foundation with medium-to-full coverage and a natural finish. Controls oil and minimizes pores for up to 8 hours.',
     3200.00, 30, 'Beauty',
     'https://images.unsplash.com/photo-1631214499235-7b1ac3e8b3e5?w=400&q=80',
     CURRENT_TIMESTAMP),

    (4, 'MAC Prep + Prime Fix+ Setting Spray 100ml',
     'MAC. Lightweight setting spray that hydrates and sets makeup for a longer-lasting finish. Infused with green tea, chamomile and cucumber.',
     2700.00, 25, 'Beauty',
     'https://images.unsplash.com/photo-1512207736890-6ffed8a84e8d?w=400&q=80',
     CURRENT_TIMESTAMP),

    (4, 'Swiss Beauty Ultra Smooth Lip Color',
     'Swiss Beauty. Creamy, hydrating lipstick with smooth glide and rich pigment. Enriched with Vitamin E for soft, moisturized lips.',
     199.00, 120, 'Beauty',
     'https://images.unsplash.com/photo-1596704017254-9b121068fb31?w=400&q=80',
     CURRENT_TIMESTAMP),

    (4, 'Swiss Beauty Waterproof Kajal',
     'Swiss Beauty. Intense black kajal pencil with smooth, smudge-proof formula. Enriched with almond oil for comfortable all-day wear.',
     149.00, 150, 'Beauty',
     'https://images.unsplash.com/photo-1583241475880-083f84372725?w=400&q=80',
     CURRENT_TIMESTAMP),

    (4, 'Swiss Beauty 12-Shade Eyeshadow Palette',
     'Swiss Beauty. Blendable palette with 6 matte and 6 shimmer shades. Perfect for day-to-night looks with long-lasting pigment.',
     399.00, 55, 'Beauty',
     'https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?w=400&q=80',
     CURRENT_TIMESTAMP),

    (4, 'Swiss Beauty Professional Makeup Brush Set',
     'Swiss Beauty. Set of 12 professional makeup brushes for face and eye application. Soft synthetic bristles with zip pouch.',
     599.00, 45, 'Beauty',
     'https://images.unsplash.com/photo-1503236823255-94609f598e71?w=400&q=80',
     CURRENT_TIMESTAMP);