ALTER TABLE order_items ADD COLUMN created_at TIMESTAMP;
UPDATE order_items SET created_at = NOW() WHERE created_at IS NULL;
ALTER TABLE order_items ALTER COLUMN created_at TIMESTAMP NOT NULL;
