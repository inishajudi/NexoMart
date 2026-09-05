# NexoMart — Week 5 Deliverable

Window: Aug 24 – Aug 30 · Required output per Section 6: **"Search/filter, order status
workflow"**

Cumulative zip — full project through Week 5. Copy over your existing checkout; this
week edits `ProductDAO`/`ProductService`/`ProductServlet` (Week 2) and `OrderDAO`/
`OrderService`/`OrderServlet` (Week 2) and touches the seller dashboard (Week 4).

## What's new this week

### Search/filter polish (F3)
Week 2's search only took `q` and `category`. This week adds price range and sorting
without breaking the old call sites:

- `dto/ProductSearchCriteria.java` — a small Builder-style DTO (`keyword().category()
  .minPrice().maxPrice().sortBy()`), satisfying Section 12's Builder pattern requirement
- `ProductDAO.search(ProductSearchCriteria)` — new overload; the old `search(keyword,
  category)` now just delegates to it, so nothing else in the codebase had to change
- `ProductService.browse(ProductSearchCriteria)` — validates `minPrice <= maxPrice`
- `ProductServlet` GET now reads `minPrice`, `maxPrice`, `sort` (`NEWEST` | `PRICE_ASC`
  | `PRICE_DESC` | `NAME_ASC`) query params
- `browse.jsp` / `app.js` — min/max price inputs and a sort dropdown, wired to reload
  on change

### Order status workflow (O2)
`PENDING → CONFIRMED → SHIPPED → DELIVERED`, plus `CANCELLED` as a side branch out of
the two earliest states. Implemented as a small, swappable rules class rather than
if/else scattered through the service (Section 12's Strategy pattern requirement):

- `util/OrderStatusWorkflow.java` — `isValidTransition(from, to)` and `requiresBuyer(to)`
- `OrderDAO.updateStatus()`, `OrderDAO.belongsToSeller()` — the latter authorizes a
  seller's status-change request (do they have at least one item in this order?)
- `OrderService.advanceStatus()` — the actual authorization + transition-validity logic:
  - **Admin**: any valid transition
  - **Seller** (owns ≥1 item in the order): can move it forward (`CONFIRMED`/`SHIPPED`/
    `DELIVERED`)
  - **Buyer** (owns the order): can only request `CANCELLED`, and only while
    `PENDING`/`CONFIRMED`
  - Any other combination → `ForbiddenException` (403); an illegal transition (e.g.
    `PENDING → DELIVERED`, or cancelling a `SHIPPED` order) → `ValidationException` (400)
- New `exception/ForbiddenException.java`, mapped to HTTP 403
- `PUT /api/v1/orders/{id}/status`, body `{ "status": "CONFIRMED" }`, added to `OrderServlet`
- Seller dashboard's "Incoming Orders" table now has an action button that advances
  to the next status
- New buyer-facing `jsp/my-orders.jsp` — order history with a "Cancel order" button
  shown only while the order is still cancellable

## API additions this week

| Method | Path | Auth | Notes |
|---|---|---|---|
| GET | `/api/v1/products?...&minPrice=&maxPrice=&sort=` | none | F3, extended |
| PUT | `/api/v1/orders/{id}/status` | session | body `{status}`; 403 if not authorized for that transition, 400 if the transition itself is illegal |

## Tests added this week

- `OrderServiceTest` (Mockito) — 9 cases covering: seller confirms their own order,
  seller blocked from an order with none of their products, no skipping stages, buyer
  cancels their own pending order, buyer blocked from cancelling someone else's, can't
  cancel a shipped order, admin bypasses the ownership check (not the transition rules),
  unknown status string rejected, missing order → 404.

## Running it

```bash
mvn clean verify   # runs all DAO + service tests from Weeks 2-5
mvn clean package
```

## Before you commit

- [ ] `mvn clean verify` passes locally.
- [ ] Try the full lifecycle by hand once: place an order as a buyer, confirm → ship →
      deliver it as the seller from the dashboard, then try (and expect to fail) cancelling
      it as the buyer once it's shipped.
- [ ] Multi-seller orders: an order can contain items from more than one seller. Right
      now *any* seller with an item in the order can move its status for the *whole*
      order — that's a simplification worth calling out as a known limitation in your
      final report (Section 7) rather than silently glossing over.
- [ ] Week 6 (reviews/ratings, edge cases, validation) is next.
