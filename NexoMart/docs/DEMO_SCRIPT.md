# NexoMart Demo Script

Total time: about 4 minutes. For the backup video (2-3 minutes), skip the steps marked (optional).

## Before the demo

- Open https://nexomart.onrender.com 2 minutes early. The free server sleeps, and the first request can take about a minute.
- The database goes back to seed data when the server restarts, so the demo data is always the same.
- Keep 2 browser windows: one normal, one private (for a second login).
- Demo logins (password `nexomart123`): `buyer1@nexomart.com`, `seller1@nexomart.com`, `admin@nexomart.com`.

## Steps

1. **Home page (15 s).** Show the featured products. Click one product to open its page.
2. **Buyer flow (60 s).** Log in as `buyer1`. Click **Browse**. Search a product by name. Click **Add to cart**. Open **Cart**, then **Checkout**. Open **Orders** and show the order as CONFIRMED.
3. **Review (20 s).** Open a product from the completed order. Show the review form and the rule: you can only review products from your completed orders, once per product.
4. **Seller flow (40 s).** Log out. Log in as `seller1`. Click **List a Product** and create a product. Open **My Sales**. (optional: Edit the product)
5. **Admin (20 s).** Log out. Log in as `admin`. Open the admin dashboard. Remove a product. (optional)
6. **Chatbot (40 s).** Click the brass chat button. Ask 3 questions:
   - `How do I sell a product?`
   - `How do I leave a review?`
   - `How do I track my order?`
   Say: the answer comes from Gemini. If Gemini is down, the site uses safe fallback text, so it never breaks.
7. **Health check (10 s).** Open `/api/v1/health` and show `{"status":"UP","db":"UP"}`.

## Closing lines (15 s)

- Layered design: Filters, Servlets, Services, DAOs, H2.
- 51 automatic tests, and GitHub Actions runs them on every push.
- Automatic deploy to Render with Docker.

## If something goes wrong

- Page is slow: say "the free server was sleeping" and wait.
- Chatbot shows "temporarily unavailable": say "this is the safe fallback", then show the rate limit or the code.
- Keep the backup video ready on the desktop.