-- V3__add_review_constraints.sql
-- F8 (reviews/ratings): one review per user per product. Applied after V1's reviews
-- table already exists; a new, numbered file rather than hand-editing V1, per Section 14.

ALTER TABLE reviews ADD CONSTRAINT uq_review_user_product UNIQUE (product_id, user_id);
