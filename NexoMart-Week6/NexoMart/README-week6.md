# NexoMart — Week 6 Deliverable

Window: Aug 31 – Sep 6 · Required output per Section 6: **"Reviews/ratings, edge case
handling, input validation"**

Cumulative zip — full project through Week 6. This week touches `ProductService`
(length caps), `CartService` (stock-check fixes), `ProductServlet` (response shape for
single-product GET changes — see below), and adds the review feature end to end.

## F8 — Product reviews and star ratings on completed orders

| Piece | File |
|---|---|
| `Review` model | `model/Review.java` |
| `ReviewDAO`/`ReviewDAOImpl` — insert, list-by-product, duplicate check, **delivered-order eligibility check**, rating aggregate | `dao/Review*.java` |
| `V3__add_review_constraints.sql` — DB-level unique constraint on `(product_id, user_id)` | `db/migrations/` |
| `ReviewService.submitReview` — validates rating 1-5 and comment length, then checks eligibility | `service/ReviewService.java` |
| `ReviewServlet` — `GET /api/v1/reviews?productId=`, `POST /api/v1/reviews` | `controller/ReviewServlet.java` |
| `product-detail.jsp` — product info, star rating, review list, review submission form | `jsp/product-detail.jsp` |
| Browse page product cards now link to the detail page | `js/app.js` |

**"On completed orders" is enforced at the query level**, not just trusted from the
client: `ReviewDAO.hasDeliveredOrderForProduct(userId, productId)` joins `orders` to
`order_items` and requires `status = 'DELIVERED'`. A user who never bought the product,
or whose order hasn't been marked delivered yet (see Week 5's status workflow), gets a
400, not a silently-accepted review. The one-review-per-user-per-product rule is
enforced twice — once in `ReviewService` (friendly 400) and once by the DB's unique
constraint (so a race between two concurrent submits can't produce two rows).

### Note: `GET /api/v1/products/{id}` response shape changed

It used to return the `ProductDTO` directly as `data`. This week it returns
`{ product, rating }` so the detail page can show the star average without a second
round trip. Nothing else in the codebase called this endpoint yet (checked before
changing it), but if you've built anything against it since Week 2, update it.

## Edge case handling & input validation

| Fix | Where |
|---|---|
| Cart stock check now accounts for quantity **already in the cart**, not just the newly requested amount — previously, adding 4 then 3 more of a 5-in-stock item silently succeeded (upsert combined them to 7) | `CartService.addItem` |
| `PUT /api/v1/cart/{id}` (update quantity) now checks stock at all — previously it didn't check stock, only `POST` (add) did | `CartService.updateQuantity` |
| Per-line quantity capped at 1000 (`requireRange`) instead of just "positive" — guards against a malformed/abusive request rather than a real business rule | `CartService` (`MAX_QUANTITY_PER_LINE`) |
| Listing `name`/`description`/`category` length-capped to match the schema's `VARCHAR` limits (150/2000/100) — previously an over-length value would 500 at the DB layer instead of 400ing cleanly | `ProductService.create`/`update` |
| New `ValidationUtil.requireRange`, `requireMaxLength` | `util/ValidationUtil.java` |

## API additions this week

| Method | Path | Auth | Notes |
|---|---|---|---|
| GET | `/api/v1/reviews?productId=` | none | F8 — reviews + rating summary for a product |
| POST | `/api/v1/reviews` | session | body `{productId, rating, comment}` — F8 |

## Tests added this week

- `ReviewServiceTest` — delivered-order eligibility, duplicate rejection, rating range,
  unknown product, rating-summary delegation
- `CartServiceTest` — cumulative stock check on add, stock check on update (previously
  missing entirely), absurd-quantity rejection, missing-cart-item 404

## Running it

```bash
mvn clean verify   # all DAO + service tests, Weeks 2-6
mvn clean package
```

Apply `V3__add_review_constraints.sql` against your dev/deployed H2 instance — it's a
new numbered migration on top of `V1`, not a change to it.

## Before you commit

- [ ] `mvn clean verify` passes locally.
- [ ] Manually walk the review flow once: place an order, advance it to `DELIVERED`
      as the seller (Week 5's dashboard), then submit a review from `product-detail.jsp`
      as that buyer — and confirm submitting a *second* review for the same product is
      rejected.
- [ ] The security checklist in Section 9 is due before Sep 21 (Full Build + Deploy) —
      Week 7 covers that explicitly, but reviewing it now while the codebase is fresh
      in your head isn't wasted time.
- [ ] Week 7 (security hardening, test coverage) is next.
