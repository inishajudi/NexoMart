# NexoMart — Week 3 Deliverable

Window: Aug 10 – Aug 16 · Required output per Section 6: **"Review feedback addressed;
seller dashboard (listing management) started"**

This zip is **cumulative** — it contains the full project state through Week 3, not just
a diff. It builds directly on the Week 1 and Week 2 code (same package structure, same
`api/v1` contract), so copy it over your existing repo checkout rather than merging by hand.

## What's new this week

Week 2 left F2 (seller create/edit/delete listings) only half-built — create existed,
edit/delete didn't. Week 3 finishes it and gives sellers a page to manage it from:

| Change | Where |
|---|---|
| `findBySeller(sellerId)` | `ProductDAO` / `ProductDAOImpl` |
| `sellerListings`, `update`, `delete` — with ownership check (a seller can only touch their own listings; a mismatch reads as 404, not 403, so ids aren't probe-able) | `ProductService` |
| `GET /api/v1/products/mine`, `PUT /api/v1/products/{id}`, `DELETE /api/v1/products/{id}` | `ProductServlet` |
| Seller dashboard page — form to add/edit a listing, table of your listings with Edit/Delete | `jsp/seller-dashboard.jsp`, `js/app.js` (`initSellerDashboard`), `css/style.css` |
| Ownership-check unit tests (DAO mocked, per Section 9) | `src/test/.../service/ProductServiceTest.java` |

F2 is now fully implemented (create, edit, delete, all seller-scoped).

## Ownership rule (the "review feedback" this week addresses)

A common gap after an MVP review is that edit/delete endpoints trust whatever id the
client sends. `ProductService.update`/`delete` now re-fetch the product, compare
`product.sellerId` against the logged-in session user, and throw `NotFoundException`
(not a generic "forbidden") on mismatch — so the response doesn't confirm to an attacker
that a given product id exists but belongs to someone else. Covered by
`ProductServiceTest`.

## API additions this week

| Method | Path | Auth | Notes |
|---|---|---|---|
| GET | `/api/v1/products/mine` | session | seller's own listings, for the dashboard |
| PUT | `/api/v1/products/{id}` | session, must own the product | edit a listing |
| DELETE | `/api/v1/products/{id}` | session, must own the product | delete a listing |

Same envelope as before: `{ success, data, error }`.

## Running it

```bash
mvn clean verify   # runs Week 2's OrderDAOImplTest + this week's ProductServiceTest
mvn clean package  # produces target/nexomart.war
```

Visit `/jsp/seller-dashboard.jsp` while logged in as a seller session to manage listings.

## Before you commit

- [ ] `mvn clean verify` passes locally.
- [ ] Log in as a SELLER-role user (from your Week 1 auth) before hitting the dashboard —
      there's no role gate yet beyond "is logged in"; add a `role == SELLER` check in
      `ProductServlet.requireSeller` if your rubric wants that enforced server-side too.
- [ ] Admin panel (Section 6, Week 4) and search/filter polish (Week 5) are still ahead —
      don't let this week's scope creep into those.
- [ ] Keep committing in small pieces with `feat:`/`test:` prefixes to stay on pace for
      the 3-commits/week minimum (Section 8).
- [ ] Take MVP Review feedback you actually received on Aug 10 and fold it in here too —
      this deliverable only covers the listing-management gap; add your own notes for
      anything else your reviewer flagged.
