-- Passwords below are bcrypt hashes (jBCrypt-compatible, $2a$ prefix, cost 12).
-- All demo users share the same password: nexomart123

INSERT INTO users (name, email, password_hash, role, created_at) VALUES
    ('Asha Buyer',       'buyer1@nexomart.com',  '$2a$12$sS.8BwD5OyozOVuhTBNjhep2TX1OLm5ZxCjfQ1exYkEkn2bhL.wGK', 'BUYER',  CURRENT_TIMESTAMP),
    ('Kavi Buyer',       'buyer2@nexomart.com',  '$2a$12$sS.8BwD5OyozOVuhTBNjhep2TX1OLm5ZxCjfQ1exYkEkn2bhL.wGK', 'BUYER',  CURRENT_TIMESTAMP),
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
     'https://www.bigbasket.com/media/uploads/p/xxl/40195469_4-cetaphil-gentle-skin-cleanser.jpg',
     CURRENT_TIMESTAMP),

    (3, 'Cetaphil Moisturizing Cream 250g',
     'Cetaphil. Rich, non-greasy moisturizing cream for 48-hour hydration for dry to very dry skin. Fragrance-free and non-comedogenic.',
     499.00, 60, 'Skin Care',
     'https://www.bigbasket.com/media/uploads/p/xxl/40195470_4-cetaphil-moisturizing-cream.jpg',
     CURRENT_TIMESTAMP),

    (3, 'Dot & Key Watermelon Cooling Toner 200ml',
     'Dot & Key. Hydrating face toner with watermelon extract that soothes and balances skin. Alcohol-free formula suitable for all skin types.',
     395.00, 50, 'Skin Care',
     'https://www.bigbasket.com/media/uploads/p/xxl/40215421_2-dot-key-watermelon-hyaluronic-cooling-toner.jpg',
     CURRENT_TIMESTAMP),

    (3, 'Dot & Key Vitamin C Serum 30ml',
     'Dot & Key. Brightening Vitamin C serum with niacinamide that fades dark spots and evens skin tone. Lightweight and fast-absorbing.',
     595.00, 45, 'Skin Care',
     'https://www.bigbasket.com/media/uploads/p/xxl/40215419_2-dot-key-vitamin-c-ceramide-serum.jpg',
     CURRENT_TIMESTAMP),

    (3, 'The Minimalist Niacinamide 10% Serum 30ml',
     'The Minimalist. High-strength niacinamide serum that reduces pores, controls sebum and improves uneven skin tone. For oily and acne-prone skin.',
     599.00, 70, 'Skin Care',
     'https://www.bigbasket.com/media/uploads/p/xxl/40218182_4-minimalist-niacinamide-10-zinc-1-face-serum.jpg',
     CURRENT_TIMESTAMP),

    (3, 'The Minimalist SPF 50 Sunscreen 50ml',
     'The Minimalist. Lightweight, non-greasy sunscreen with broad-spectrum SPF 50 PA+++ protection. Water-resistant with no white cast.',
     349.00, 90, 'Skin Care',
     'https://www.bigbasket.com/media/uploads/p/xxl/40218184_3-minimalist-spf-50-sunscreen.jpg',
     CURRENT_TIMESTAMP),

    (3, 'Mamaearth Ubtan Face Wash 100ml',
     'Mamaearth. Natural ubtan face wash with turmeric and saffron that brightens skin and removes tan. Made with toxin-free, natural ingredients.',
     249.00, 100, 'Skin Care',
     'https://www.bigbasket.com/media/uploads/p/xxl/40168513_11-mamaearth-ubtan-natural-face-wash.jpg',
     CURRENT_TIMESTAMP),

    (3, 'Mamaearth Vitamin C Face Cream 50g',
     'Mamaearth. Daily moisturizer with Vitamin C and turmeric that reduces dark spots and gives skin a healthy glow. SPF 20 protection included.',
     399.00, 75, 'Skin Care',
     'https://www.bigbasket.com/media/uploads/p/xxl/40168511_11-mamaearth-vitamin-c-daily-glow-face-cream.jpg',
     CURRENT_TIMESTAMP);

-- ── BEAUTY (7 products) ─────────────────────────────────────────────────────
INSERT INTO products (seller_id, name, description, price, stock_qty, category, image_url, created_at) VALUES

    (4, 'MAC Matte Lipstick - Ruby Woo',
     'MAC. Iconic retro matte lipstick in cult shade Ruby Woo. Vivid blue-red with a classic matte finish. Long-lasting formula with full coverage.',
     1850.00, 40, 'Beauty',
     'https://www.bigbasket.com/media/uploads/p/xxl/40183804_4-mac-retro-matte-lipstick.jpg',
     CURRENT_TIMESTAMP),

    (4, 'MAC Studio Fix Powder Plus Foundation',
     'MAC. Matte powder foundation with medium-to-full coverage and a natural finish. Controls oil and minimizes pores for up to 8 hours.',
     3200.00, 30, 'Beauty',
     'https://www.bigbasket.com/media/uploads/p/xxl/40183807_3-mac-studio-fix-powder-plus-foundation.jpg',
     CURRENT_TIMESTAMP),

    (4, 'MAC Prep + Prime Fix+ Setting Spray 100ml',
     'MAC. Lightweight setting spray that hydrates and sets makeup for a longer-lasting finish. Infused with green tea, chamomile and cucumber.',
     2700.00, 25, 'Beauty',
     'https://www.bigbasket.com/media/uploads/p/xxl/40183810_3-mac-prep-prime-fix-setting-spray.jpg',
     CURRENT_TIMESTAMP),

    (4, 'Swiss Beauty Ultra Smooth Lip Color',
     'Swiss Beauty. Creamy, hydrating lipstick with smooth glide and rich pigment. Enriched with Vitamin E for soft, moisturized lips.',
     199.00, 120, 'Beauty',
     'https://www.bigbasket.com/media/uploads/p/xxl/40198056_4-swiss-beauty-ultra-smooth-lip-color.jpg',
     CURRENT_TIMESTAMP),

    (4, 'Swiss Beauty Waterproof Kajal',
     'Swiss Beauty. Intense black kajal pencil with a smooth, smudge-proof formula. Enriched with almond oil for comfortable all-day wear.',
     149.00, 150, 'Beauty',
     'https://www.bigbasket.com/media/uploads/p/xxl/40198059_3-swiss-beauty-super-black-kajal.jpg',
     CURRENT_TIMESTAMP),

    (4, 'Swiss Beauty 12-Shade Eyeshadow Palette',
     'Swiss Beauty. Blendable palette with 6 matte and 6 shimmer shades. Perfect for day-to-night looks with long-lasting pigment.',
     399.00, 55, 'Beauty',
     'https://www.bigbasket.com/media/uploads/p/xxl/40198061_3-swiss-beauty-eyeshadow-palette.jpg',
     CURRENT_TIMESTAMP),

    (4, 'Swiss Beauty Professional Makeup Brush Set',
     'Swiss Beauty. Set of 12 professional makeup brushes for face and eye application. Soft synthetic bristles with zip pouch.',
     599.00, 45, 'Beauty',
     'https://www.bigbasket.com/media/uploads/p/xxl/40198063_3-swiss-beauty-makeup-brush-set.jpg',
     CURRENT_TIMESTAMP);