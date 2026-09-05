# NexoMart — Week 4 Deliverable

Window: Aug 17 – Aug 23 · Required output per Section 6: **"Seller dashboard complete;
admin panel started"**

This zip is **cumulative** — full project state through Week 4. Copy it over your
existing checkout rather than hand-merging; Week 4 edits files from Weeks 2 and 3.

## What's new this week

### Seller dashboard — completed
Week 3 left the dashboard with listing management only. This week adds the other half
of F6 (seller view of incoming orders for their products) directly into the same page:

- `jsp/seller-dashboard.jsp` — new "Incoming Orders" table
- `js/app.js` — `loadIncomingOrders()`, called from `initSellerDashboard()`, hitting the
  `GET /api/v1/orders?as=seller` endpoint that already existed from Week 2

### Admin panel — started (F7)
| Piece | File |
|---|---|
| `User` model, `Role` enum | `model/User.java`, `model/Role.java` |
| `UserDAO`/`UserDAOImpl` — **read-only**, `SELECT` list never includes `password_hash` | `dao/UserDAO*.java` |
| `UserResponseDTO` — separate from the entity, no password field at all (Section 13 rule 4) | `dto/UserResponseDTO.java` |
| `OrderDAO.findAll()` | `dao/OrderDAO*.java` |
| `ProductService.adminDelete()` — removes any listing, no ownership check (caller must authorize) | `service/ProductService.java` |
| `AdminService` — composes UserDAO/OrderDAO/ProductService; still no JDBC in the service layer | `service/AdminService.java` |
| `AdminServlet` — `GET /users`, `GET /orders`, `DELETE /products/{id}` | `controller/AdminServlet.java` |
| `admin-dashboard.jsp` — users table, all-orders table, listing moderation with search + remove | `jsp/admin-dashboard.jsp` |

## Authorization model for the admin panel

Two layers, deliberately redundant:

1. `AuthFilter` now also covers `/api/v1/admin/*` — rejects unauthenticated requests
   with 401 before the servlet even runs.
2. `AdminServlet.requireAdmin()` additionally checks `session.getAttribute("role")
   == "ADMIN"` and returns 403 if not — because "logged in" isn't the same authorization
   level as "admin", and a route that can delete other people's listings needs its own
   check, not just the shared filter's.

`AdminService.removeListing` calls `ProductService.adminDelete`, which — unlike the
seller-facing `ProductService.delete` — intentionally skips the ownership check.
That's safe *only* because `AdminServlet` gates the call above it; the Javadoc on
`adminDelete` says so explicitly so nobody wires it up elsewhere without the same guard.

## API additions this week

| Method | Path | Auth | Notes |
|---|---|---|---|
| GET | `/api/v1/admin/users` | session, role ADMIN | F7 — all users, no password hashes |
| GET | `/api/v1/admin/orders` | session, role ADMIN | F7 — all orders in the system |
| DELETE | `/api/v1/admin/products/{id}` | session, role ADMIN | F7 — moderate/remove any listing |

## Tests added this week

- `AdminServiceTest` — mocked DAOs; confirms `removeListing` deletes regardless of
  owner, 404s on a missing product, and that the returned DTO shape has no password field.
- `UserDAOImplTest` — embedded H2; confirms `findAll`/`findById` never populate
  `passwordHash` even though the `User` model has the field (the SQL just never selects it).

## Running it

```bash
mvn clean verify   # runs all DAO + service tests from Weeks 2-4
mvn clean package  # target/nexomart.war
```

To actually see the admin panel work, your Week 1 login flow needs to produce a session
with `role = "ADMIN"` for at least one test account — the seed data in
`db/migrations/V2__seed_week2_data.sql` has one (`admin@nexomart.local`), but you'll
need to regenerate its bcrypt hash with your real `PasswordUtil` first.

## Before you commit

- [ ] `mvn clean verify` passes locally.
- [ ] Regenerate the admin seed user's bcrypt hash so you can actually log in and test
      `/jsp/admin-dashboard.jsp` end-to-end.
- [ ] Reconcile `model/User.java` / `model/Role.java` here with whatever Week 1's auth
      code already defined — if Week 1 has its own `User` class, keep one and delete
      the duplicate rather than carrying two.
- [ ] Nav links to the admin panel are currently shown to everyone (simple project,
      no conditional rendering yet) — consider hiding them for non-admins if your
      reviewer cares about that polish.
- [ ] Keep committing in small pieces (`feat:`/`test:` prefixes) toward the 3-commits/week
      minimum (Section 8); this is a good week to land at least 4-5 given the volume of work.
- [ ] Week 5 (search/filter polish, order status workflow) is next — don't scope-creep
      into it yet.
