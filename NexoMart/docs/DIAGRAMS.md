# NexoMart: Design Diagrams

Diagrams D1 to D5 for the NexoMart project.

## D1. ER Diagram

```mermaid
erDiagram
    USERS ||--o{ PRODUCTS : "sells"
    USERS ||--o{ ORDERS : "places"
    USERS ||--o{ CART_ITEMS : "has"
    USERS ||--o{ REVIEWS : "writes"
    ORDERS ||--|{ ORDER_ITEMS : "contains"
    PRODUCTS ||--o{ ORDER_ITEMS : "included in"
    PRODUCTS ||--o{ CART_ITEMS : "added to"
    PRODUCTS ||--o{ REVIEWS : "receives"

    USERS {
        BIGINT id PK
        VARCHAR name
        VARCHAR email UK
        VARCHAR password_hash
        VARCHAR role
        TIMESTAMP created_at
    }

    PRODUCTS {
        BIGINT id PK
        BIGINT seller_id FK
        VARCHAR name
        CLOB description
        DECIMAL price
        INT stock_qty
        VARCHAR category
        VARCHAR image_url
        TIMESTAMP created_at
    }

    ORDERS {
        BIGINT id PK
        BIGINT buyer_id FK
        VARCHAR status
        DECIMAL total_amount
        TIMESTAMP created_at
    }

    ORDER_ITEMS {
        BIGINT id PK
        BIGINT order_id FK
        BIGINT product_id FK
        INT quantity
        DECIMAL unit_price
    }

    CART_ITEMS {
        BIGINT id PK
        BIGINT user_id FK
        BIGINT product_id FK
        INT quantity
        TIMESTAMP created_at
    }

    REVIEWS {
        BIGINT id PK
        BIGINT product_id FK
        BIGINT user_id FK
        INT rating
        VARCHAR comment
        TIMESTAMP created_at
    }
