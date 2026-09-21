# NexoMart Final Report

## Introduction

NexoMart is a multi-seller online marketplace. Sellers list products. Buyers browse, add to cart, place orders and write reviews. Admins can see a dashboard and remove products. The site also has an AI chatbot that answers questions about how to use the site.

This is the Anna University R2025 Semester 3 capstone project.

- **Stack:** Java 17, Maven, Tomcat 9 (Servlet, JSP, JSTL), JDBC, H2, HikariCP, jBCrypt, SLF4J/Logback, Gson, JUnit 5 and Mockito.
- **Code:** https://github.com/mahalakshmisr301107-arch/NexoMart
- **Live site:** https://nexomart.onrender.com (the first request after idle can take about a minute)

## Technical Decisions

1. **Servlet, JSP and JDBC, no big framework.** The course is about the basics of Java web development. Plain servlets make the request flow easy to see and explain.
2. **H2 database with HikariCP.** H2 is small and needs no separate server. It is a file database (`./data/nexomart`). HikariCP gives a fast shared connection pool. Only one class, `DataSourceListener`, creates connections.
3. **Simple database setup with two SQL files.** `schema.sql` runs at every start and uses `CREATE TABLE IF NOT EXISTS`, so it is safe to repeat. `seed.sql` runs only when the users table is empty, so real data is not overwritten. The file `db/migrations/V1__init_schema.sql` is a versioned record of the schema for developers. The app does not run it.
4. **Passwords use jBCrypt.** Passwords are stored as hashes, never as plain text.
5. **AI chatbot with a provider interface.** `ChatProvider` has two versions: a keyword-based mock and a Gemini version. A factory chooses one from a setting. If the Gemini key is missing, the site uses the mock, so it never breaks.
6. **Chatbot safety.** The server limits input to 500 characters and 10 messages per minute per session. The system prompt is fixed on the server. The API key is only in an environment variable, never in code.
7. **Automatic tests and CI.** 51 unit tests (JUnit 5 and Mockito) check the main logic, including the chatbot. GitHub Actions runs `mvn -B clean verify` with Java 17 on every push and pull request.
8. **Docker on Render.** Every push to main deploys the site automatically.
## Architecture

NexoMart uses a layered design. Each layer has one job.

1. **Filters** (`EncodingFilter`, `AuthFilter`) run before the servlets. `AuthFilter` checks the login. The chat and health endpoints are public.
2. **Controllers** are servlets in the `controller` package, for example `ProductServlet`, `CartServlet`, `CheckoutServlet`, `LoginServlet` and `ChatServlet`. They read the request and choose the page or JSON answer.
3. **Services** hold the business rules: `UserService`, `ProductService`, `CartService`, `OrderService` and `ReviewService`.
4. **DAOs** talk to the database with JDBC: `UserDao`, `ProductDao`, `CartDao`, `OrderDao` and `ReviewDao`.
5. **Database:** H2, with connections from one HikariCP pool. `DataSourceListener` creates the pool when the app starts.
6. **Views** are JSP pages in `WEB-INF/views`. They use JSTL and one shared header and footer.
7. **Chat module** is in the `chat` package: `ChatService` (rules, cache, rate limit) and a `ChatProvider` (mock or Gemini).

Other packages: `model` (data classes), `dto`, `exception` and `util`.

Request flow: Browser, then Filters, then Servlet, then Service, then DAO, then H2. The servlet then sends the result to a JSP page.

Diagrams are in `docs/DIAGRAMS.md`: D1 ER diagram, D2 Use Case diagram, D3 Sequence diagram (Place Order).

## ER Diagram

The database has 6 tables, created by `schema.sql`:

| Table | Purpose |
|-------|---------|
| `users` | Buyers, sellers and admins. The role is BUYER, SELLER or ADMIN. The email is unique. |
| `products` | Products listed by sellers. Each product has a `seller_id`. |
| `orders` | Orders placed by buyers. Status is PENDING, CONFIRMED, SHIPPED, DELIVERED or CANCELLED. |
| `order_items` | The products inside each order. |
| `cart_items` | The products a user has in the cart. |
| `reviews` | Product reviews written by users. |

Main relations: one user can sell many products, place many orders, have many cart items and write many reviews. One order contains many order items. One product can appear in many order items, cart items and reviews.

Money fields use `DECIMAL(10,2)` and every foreign key is indexed.

The full diagram is section D1 in `docs/DIAGRAMS.md`. GitHub shows it as a picture.

## Testing

**Automatic tests.** The project has 51 unit tests (JUnit 5 and Mockito). `mvn clean verify` runs all of them, and GitHub Actions runs the same command on every push.

| Area | Test classes | Tests |
|------|--------------|-------|
| Chatbot | `ChatServiceTest`, `MockChatProviderTest`, `GeminiAndFactoryTest` | 31 |
| DAO (database code) | `JdbcUserDaoTest`, `JdbcProductDaoTest`, `JdbcOrderDaoTest` | 8 |
| Services | `UserServiceTest`, `OrderServiceTest`, `ReviewServiceTest` | 12 |

**Manual tests on the live site.** These flows passed: login, add to cart, checkout, order history, review, seller create/edit/delete product, My Sales, admin dashboard and product removal, new buyer registration, logout, redirect to login for protected pages, home page featured products, the chat button on the Login and Cart pages, live Gemini chatbot answers, custom 404 page, and the health endpoint `/api/v1/health`. The chatbot rate limit was also tested live: messages 1 to 10 were accepted, and messages 11 and 12 returned HTTP 429.

## Known Limitations

1. **Rate limiting is per HTTP session.** If a user deletes cookies, they get a new session and a new limit. We can make it stronger with an IP-based limit.
2. **Cache and rate-limit data are in memory only.** They are lost when the server restarts. They are not shared between many servers.
3. **The mock chatbot uses keywords only.** It is not a real conversation. Its answers are written by hand, so we must update them if the site pages change.
4. **Gemini model names change over time.** The Gemini provider was tested live on Render with the model `gemini-3-flash-preview`. The old default `gemini-2.5-flash` returned HTTP 404, so we set the model in the `GEMINI_MODEL` setting. Unit tests check only the response parsing. A preview model may change or stop working later.
5. **Render free tier has cold starts.** The first request after idle can take about a minute. The disk is temporary, so the H2 database file is lost and goes back to seed data on every restart.
