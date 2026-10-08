-- Passwords: bcrypt hashes ($2a$, cost 12) for shared demo password: nexomart123
-- Plaintext demo passwords are documented in README.md - never store plaintext.

INSERT INTO users (name, email, password_hash, role, created_at) VALUES
    ('Kavi',      'buyer1@nexomart.com',  '$2a$12$sS.8BwD5OyozOVuhTBNjhep2TX1OLm5ZxCjfQ1exYkEkn2bhL.wGK', 'BUYER',  CURRENT_TIMESTAMP),
    ('Krish',     'buyer2@nexomart.com',  '$2a$12$sS.8BwD5OyozOVuhTBNjhep2TX1OLm5ZxCjfQ1exYkEkn2bhL.wGK', 'BUYER',  CURRENT_TIMESTAMP),
    ('Inisha',    'seller1@nexomart.com', '$2a$12$sS.8BwD5OyozOVuhTBNjhep2TX1OLm5ZxCjfQ1exYkEkn2bhL.wGK', 'SELLER', CURRENT_TIMESTAMP),
    ('Judi',      'seller2@nexomart.com', '$2a$12$sS.8BwD5OyozOVuhTBNjhep2TX1OLm5ZxCjfQ1exYkEkn2bhL.wGK', 'SELLER', CURRENT_TIMESTAMP),
    ('Maha',      'admin@nexomart.com',   '$2a$12$sS.8BwD5OyozOVuhTBNjhep2TX1OLm5ZxCjfQ1exYkEkn2bhL.wGK', 'ADMIN',  CURRENT_TIMESTAMP),
    ('Madhu',     'seller3@nexomart.com', '$2a$12$sS.8BwD5OyozOVuhTBNjhep2TX1OLm5ZxCjfQ1exYkEkn2bhL.wGK', 'SELLER', CURRENT_TIMESTAMP),
    ('Dharshini', 'seller4@nexomart.com', '$2a$12$sS.8BwD5OyozOVuhTBNjhep2TX1OLm5ZxCjfQ1exYkEkn2bhL.wGK', 'SELLER', CURRENT_TIMESTAMP);

-- seller1 (Inisha, id=3) products - Dresses
INSERT INTO products (seller_id, name, description, price, stock_qty, category, image_url, created_at) VALUES
    (3, 'Floral Wrap Dress',        'Lightweight chiffon wrap dress with an all-over floral print, adjustable tie waist and flutter sleeves. Perfect for casual outings or brunches.',                              1299.00, 40, 'Dresses', 'https://images.pexels.com/photos/7672255/pexels-photo-7672255.jpeg',   CURRENT_TIMESTAMP),
    (3, 'Anarkali Kurti',           'Elegant floor-length Anarkali in soft georgette with delicate embroidery at the neckline and hem. Available in festive jewel tones.',                                      1799.00, 35, 'Dresses', 'https://images.pexels.com/photos/3621104/pexels-photo-3621104.jpeg',   CURRENT_TIMESTAMP),
    (3, 'Linen Shirt Dress',        'Relaxed straight-cut shirt dress in breathable linen blend. Features a button placket, side pockets and a self-tie belt for a polished everyday look.',                   1499.00, 30, 'Dresses', 'https://images.pexels.com/photos/5480696/pexels-photo-5480696.jpeg',   CURRENT_TIMESTAMP),
    (3, 'Strappy Sundress',         'Breezy A-line sundress in cotton voile with adjustable spaghetti straps and a smocked bodice. A warm-weather wardrobe essential.',                                         999.00, 50, 'Dresses', 'https://images.pexels.com/photos/6311392/pexels-photo-6311392.jpeg',   CURRENT_TIMESTAMP),
    (3, 'Printed Midi Dress',       'Flattering midi-length dress in a vibrant block print, crafted from soft rayon. Elasticated waist and concealed side zip for a comfortable, neat fit.',                  1599.00, 25, 'Dresses', 'https://images.pexels.com/photos/6311387/pexels-photo-6311387.jpeg',   CURRENT_TIMESTAMP);

-- seller2 (Judi, id=4) products - Accessories
INSERT INTO products (seller_id, name, description, price, stock_qty, category, image_url, created_at) VALUES
    (4, 'Beaded Jhumka Earrings',   'Handcrafted brass jhumkas with colourful seed-bead tassels. Lightweight and hypoallergenic with a secure hook closure.',                                                    349.00, 80, 'Accessories', 'https://images.pexels.com/photos/10199883/pexels-photo-10199883.jpeg', CURRENT_TIMESTAMP),
    (4, 'Leather Tote Bag',         'Spacious vegan-leather tote with an interior zip pocket, magnetic snap closure and sturdy handles. Fits a 14-inch laptop with ease.',                                     1999.00, 30, 'Accessories', 'https://images.pexels.com/photos/1152077/pexels-photo-1152077.jpeg',   CURRENT_TIMESTAMP),
    (4, 'Silk Printed Scrunchie Set','Set of five oversized scrunchies in vibrant silk-satin prints. Gentle on hair, strong hold — from gym to evening in seconds.',                                            299.00, 90, 'Accessories', 'https://images.pexels.com/photos/7755179/pexels-photo-7755179.jpeg',   CURRENT_TIMESTAMP),
    (4, 'Oxidised Silver Cuff',     'Wide oxidised silver-finish cuff bracelet with intricate floral engraving. A bold statement piece that pairs with both ethnic and western outfits.',                      599.00, 45, 'Accessories', 'https://images.pexels.com/photos/9428214/pexels-photo-9428214.jpeg',   CURRENT_TIMESTAMP),
    (4, 'Woven Straw Hat',          'Wide-brim straw sun hat with an adjustable inner band and a grosgrain ribbon trim. UPF 40+ protection for beach days and outdoor festivals.',                             799.00, 55, 'Accessories', 'https://images.pexels.com/photos/984619/pexels-photo-984619.jpeg',     CURRENT_TIMESTAMP);

-- seller3 (Madhu, id=6) products - Skincare
INSERT INTO products (seller_id, name, description, price, stock_qty, category, image_url, created_at) VALUES
    (6, 'Vitamin C Brightening Serum',  'Lightweight 15% Vitamin C serum that fades dark spots and boosts radiance. Alcohol-free formula suitable for all skin types.',                                       899.00, 60, 'Skincare', 'https://images.pexels.com/photos/28255122/pexels-photo-28255122.jpeg',   CURRENT_TIMESTAMP),
    (6, 'Hydrating Aloe Gel Moisturiser','Gel-cream moisturiser with pure aloe vera and hyaluronic acid for deep, non-greasy hydration. Absorbs quickly, leaving skin plump and calm.',                       649.00, 55, 'Skincare', 'https://images.pexels.com/photos/14798340/pexels-photo-14798340.jpeg',   CURRENT_TIMESTAMP),
    (6, 'Rice Water Gentle Cleanser',   'Mild foam cleanser enriched with fermented rice water and niacinamide. Removes impurities while maintaining the skin barrier. Fragrance-free.',                      499.00, 70, 'Skincare', 'https://images.pexels.com/photos/33343234/pexels-photo-33343234.png',   CURRENT_TIMESTAMP),
    (6, 'SPF 50 Sunscreen Lotion',      'Broad-spectrum UVA/UVB sunscreen with a lightweight, no-white-cast finish. Water-resistant for up to 80 minutes. PA++++ rated.',                                     549.00, 80, 'Skincare', 'https://images.pexels.com/photos/16378486/pexels-photo-16378486.jpeg',   CURRENT_TIMESTAMP),
    (6, 'Overnight Repair Face Mask',   'Rich sleeping mask with retinol, ceramides and bakuchiol that works overnight to improve skin texture and reduce fine lines. Wake up to softer, smoother skin.',    799.00, 40, 'Skincare', 'https://images.pexels.com/photos/27462655/pexels-photo-27462655.jpeg',   CURRENT_TIMESTAMP);

-- seller4 (Dharshini, id=7) products - Beauty
INSERT INTO products (seller_id, name, description, price, stock_qty, category, image_url, created_at) VALUES
    (7, 'Velvet Matte Lip Colour',      'Highly pigmented matte lip colour with a velvety, featherweight finish. Long-wearing formula that does not dry out lips. Available in 12 shades.',                   399.00, 80, 'Beauty', 'https://images.pexels.com/photos/10037325/pexels-photo-10037325.jpeg',   CURRENT_TIMESTAMP),
    (7, 'Lash Boost Mascara',           'Buildable fibre mascara that adds dramatic length and volume without clumping. Smudge-proof and humidity-resistant for all-day wear.',                               349.00, 70, 'Beauty', 'https://images.pexels.com/photos/10207462/pexels-photo-10207462.png',   CURRENT_TIMESTAMP),
    (7, 'Pro Blending Brush Set',       'Set of 8 professional-grade brushes with ultra-soft synthetic bristles for seamless blending of foundation, contour and eyeshadow. Includes a zip-up travel pouch.',799.00, 40, 'Beauty', 'https://images.pexels.com/photos/7712471/pexels-photo-7712471.jpeg',   CURRENT_TIMESTAMP),
    (7, 'Smoky Nude Eyeshadow Palette', '12-shade palette featuring a curated mix of matte and shimmer nudes and neutrals. Highly blendable formula for effortless everyday and evening eye looks.',          899.00, 35, 'Beauty', 'https://images.pexels.com/photos/6662825/pexels-photo-6662825.jpeg', CURRENT_TIMESTAMP),
    (7, 'HD Setting Powder',            'Ultra-fine translucent setting powder that blurs pores, locks makeup in place and controls shine throughout the day. Fragrance-free, suitable for all skin tones.',  299.00, 65, 'Beauty', 'https://images.pexels.com/photos/16212410/pexels-photo-16212410.jpeg', CURRENT_TIMESTAMP);