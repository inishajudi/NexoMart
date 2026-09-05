# NexoMart — Week 2 Deliverable (Core Flow)

Window: Aug 3 – Aug 9 · Required output per Section 6: **browse → add to cart → place order**

## What's implemented this week

| Feature | Status |
|---|---|
| F3 — Browse & search/filter by category and keyword | ✅ `ProductServlet` GET, `ProductService.browse` |
| F4 — Cart: add / update / remove, running total | ✅ `CartServlet`, `CartService` |
| F5 — Checkout via mock payment confirmation | ✅ `OrderServlet` POST, `OrderDAOImpl.placeOrderFromCart` (single DB transaction) |
| F6 (partial) — Buyer order history | ✅ `OrderServlet` GET, seller "incoming orders" view included too |
| F2 (partial) — Seller can create a listing | ✅ `ProductServlet` POST (edit/delete still open) |

Not in scope this week: seller dashboard UI polish, admin panel, reviews (F8), order
status transitions beyond PENDING (O2), AI chatbot (Section 11 — Week 9).

## How the checkout transaction works

`OrderDAOImpl.placeOrderFromCart(buyerId)` does the following **in one JDBC transaction**
(`con.setAutoCommit(false)` ... `commit()` / `rollback()`), so a half-completed order can
never be left in the database:

1. Locks and reads the buyer's cart lines joined to current product price/stock (`FOR UPDATE`).
2. Fails fast if requested quantity exceeds stock for any line.
3. Inserts the `orders` row (status `PENDING`) and one `order_items` row per line, at the
   price captured at checkout time (not a live join to `products` later).
4. Decrements `products.stock_qty` per line, using a conditional `WHERE stock_qty >= ?`
   so a concurrent order can't oversell.
5. Clears the cart.
6. Commits; rolls back the whole transaction if any step fails.

This satisfies "place an order from cart contents via mock payment confirmation" (F5) —
"mock" means the client sends `{ "paymentConfirmed": true }` and the server trusts that
flag; there is no external gateway call, per the scope constraints in Section 1.

## Files added this week

```
db/migrations/V1__init_schema.sql       (full schema, checked in per Section 14)
db/migrations/V2__seed_week2_data.sql   (demo products/users, separate from schema)

src/main/java/com/nexo/nexomart/
  model/          Product, CartItem, Order, OrderItem, OrderStatus
  dto/            ProductDTO, CartItemDTO, CartRequestDTO, OrderDTO, ApiResponse
  dao/            ProductDAO(+Impl), CartDAO(+Impl), OrderDAO(+Impl)
  service/        ProductService, CartService, OrderService
  controller/     ProductServlet, CartServlet, OrderServlet
  filter/         AuthFilter (session check on /api/v1/cart/*, /api/v1/orders/*)
  listener/       DataSourceListener (HikariCP, Singleton pattern), DataSourceProvider
  util/           JsonUtil (Gson wiring), ValidationUtil
  exception/      ValidationException, NotFoundException, DataAccessException

src/main/webapp/
  WEB-INF/web.xml
  jsp/browse.jsp, jsp/cart.jsp, jsp/error.jsp
  js/app.js        (vanilla JS + fetch, per Section 3's view-layer spec)
  css/style.css

src/test/java/com/nexo/nexomart/dao/OrderDAOImplTest.java
```

## API surface added this week

All responses use the fixed envelope from Section 13: `{ success, data, error }`.

| Method | Path | Auth | Notes |
|---|---|---|---|
| GET | `/api/v1/products?q=&category=` | none | F3 browse/search |
| GET | `/api/v1/products/{id}` | none | product detail |
| POST | `/api/v1/products` | seller session | F2 create listing |
| GET | `/api/v1/cart` | session | F4 view cart + total |
| POST | `/api/v1/cart` | session | body `{productId, quantity}` |
| PUT | `/api/v1/cart/{id}` | session | body `{quantity}` |
| DELETE | `/api/v1/cart/{id}` | session | remove line |
| POST | `/api/v1/orders` | session | body `{paymentConfirmed: true}` — F5 checkout |
| GET | `/api/v1/orders` | session | buyer history; `?as=seller` for incoming orders |
| GET | `/api/v1/orders/{id}` | session | single order (owner or admin only) |

## Running it

```bash
mvn clean package
# copy target/nexomart.war to Tomcat 9's webapps/ folder, or
mvn clean verify   # runs the DAO tests against embedded H2
```

Local dev uses `jdbc:h2:mem:nexomart` by default (set in `DataSourceListener`). For the
deployed VM, set env vars before starting Tomcat:

```bash
export NEXOMART_JDBC_URL="jdbc:h2:tcp://localhost:9092/./data/nexomart"
export NEXOMART_DB_USER=sa
export NEXOMART_DB_PASSWORD=
```

Apply `db/migrations/V1__init_schema.sql` (and `V2__seed_week2_data.sql` for demo data)
against that H2 instance once before first boot.

## Before you commit — sanity checklist

- [ ] Replace the placeholder bcrypt hashes in `V2__seed_week2_data.sql` with real ones
      from your Week 1 `PasswordUtil.hash(...)` if you want the seed users to log in.
- [ ] Confirm `AuthFilter` reads the same session attribute names (`userId`, `role`) your
      Week 1 login servlet actually sets — adjust if you named them differently.
- [ ] `mvn clean verify` passes locally before pushing (CI runs the same command).
- [ ] Commit in small pieces (model → dao → service → controller → jsp/js) to hit the
      3-commits/week minimum in Section 8, with `feat:`/`test:` conventional prefixes.
- [ ] Update the root `README.md` with this week's screenshots once the JSP pages render.
