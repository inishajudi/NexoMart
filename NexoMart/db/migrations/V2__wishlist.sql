CREATE TABLE IF NOT EXISTS wishlist_items (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    product_id  BIGINT NOT NULL,
    created_at  TIMESTAMP NOT NULL,
    CONSTRAINT fk_wishlist_user    FOREIGN KEY (user_id)    REFERENCES users(id),
    CONSTRAINT fk_wishlist_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT uq_wishlist UNIQUE (user_id, product_id)
);
CREATE INDEX IF NOT EXISTS idx_wishlist_user_id ON wishlist_items(user_id);