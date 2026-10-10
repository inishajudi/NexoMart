# NexoMart Final Report

## Introduction

NexoMart is a multi-seller online marketplace web application. Sellers can add and manage
products, while buyers can browse products, search and filter products, add products to Cart,
save products to Wishlist, place orders and write reviews. Admins can manage users, products
and orders through the Admin Dashboard.

The application also includes an AI Chat Assistant that helps users with common
marketplace-related questions.

- **Stack:** Java 17, Maven, Tomcat 9, Servlet, JSP, JSTL, JDBC, H2, HikariCP, jBCrypt,
  SLF4J/Logback, Gson, JUnit 5 and Mockito.
- **Code:** https://github.com/inishajudi/NexoMart
- **Live site:** https://nexomart-web.onrender.com

---

## Technical Decisions

1. **Servlet, JSP and JDBC, without a large framework.**
   NexoMart uses basic Java web technologies so that the request flow and application logic
   are simple to understand and maintain.

2. **H2 database with HikariCP.**
   H2 provides a lightweight relational database, while HikariCP manages database connections
   efficiently.

3. **Layered application structure.**
   The application separates Filters, Controllers, Services, DAOs, Models and Views. This
   makes the system easier to maintain and extend.

4. **Secure password storage.**
   Passwords are hashed using jBCrypt instead of being stored as plain text.

5. **Role-based access.**
   NexoMart provides separate functionality for Buyers, Sellers and Admins.

6. **AI Chat Assistant.**
   The application supports Gemini AI integration and also provides a mock chatbot provider
   for testing and fallback.

7. **Automated testing.**
   JUnit 5 and Mockito are used to test important services, DAOs and chatbot components.

8. **Docker and Render deployment.**
   The application includes Docker configuration and is deployed on Render.

---

## Architecture

NexoMart follows a layered architecture where each layer has a specific responsibility.

1. **Filters** handle encoding, authentication and request ID injection.
2. **Controllers** are Java Servlets that process user requests.
3. **Services** contain application business logic.
4. **DAOs** communicate with the H2 database using JDBC.
5. **Database** stores users, products, orders, cart items and reviews.
6. **Views** are JSP pages located inside `WEB-INF/views`.
7. **Chat module** handles the AI Chat Assistant using the chat service and provider system.

The main request flow is:

```text
Browser
   ↓
Filters (EncodingFilter → RequestIdFilter → AuthFilter)
   ↓
Servlet Controllers
   ↓
Service Layer
   ↓
DAO Layer
   ↓
H2 Database
   ↓
JSP Response
```

---

## Design Patterns

### 1. DAO (Data Access Object)
**Where:** `com.nexomart.app.dao` interfaces and `com.nexomart.app.dao.impl` implementations.

Each entity (User, Product, Order, Cart, Review, Wishlist) has a DAO interface that defines
database operations, and a JDBC implementation that executes them using PreparedStatements.
This separates SQL from business logic and makes the DAO layer independently testable.

**Example:** `OrderDao` interface → `JdbcOrderDao` implementation.

---

### 2. Front Controller
**Where:** Every Servlet in `com.nexomart.app.controller`.

Each servlet handles one resource (products, orders, cart, etc.) and delegates all business
logic to the service layer. Servlets contain no SQL and no business rules — they only handle
HTTP orchestration: read the request, call the service, forward to the view.

**Example:** `SellerOrdersServlet.doPost()` reads the `action` parameter, calls
`orderService.markShipped()` or `orderService.markDelivered()`, then redirects.

---

### 3. Singleton (Connection Pool)
**Where:** `com.nexomart.app.listener.DataSourceListener`.

`DataSourceListener` implements `ServletContextListener`. It creates a single HikariCP
`DataSource` at application startup and stores it in the `ServletContext`. All DAOs retrieve
the same `DataSource` instance from the context. There is no `DriverManager.getConnection()`
call anywhere else in the application.

---

### 4. Factory
**Where:** `com.nexomart.app.chat.ChatProviderFactory`.

`ChatProviderFactory.create()` reads the `ai.chatbot.provider` config value and returns
either a `GeminiChatProvider` or a `MockChatProvider` instance, both of which implement the
`ChatProvider` interface. The rest of the application only depends on the interface, not the
concrete class.

---

### 5. Strategy
**Where:** `com.nexomart.app.service.PaymentStrategy` interface,
`com.nexomart.app.service.MockPaymentStrategy` implementation.

`PaymentStrategy` defines a `process(BigDecimal amount)` contract. `MockPaymentStrategy`
implements it by always returning `true` (mock confirmation). This allows a real payment
gateway strategy to be swapped in without changing `OrderService` — the service depends only
on the `PaymentStrategy` interface.

The same pattern is applied in the chat module: `ChatProvider` is a strategy interface
implemented by both `GeminiChatProvider` and `MockChatProvider`.

---

### 6. Builder
**Where:** `com.nexomart.app.dto.UserResponseDTO.Builder`.

`UserResponseDTO` uses a static inner `Builder` class to construct safe user response objects.
The builder allows fields to be set in any order and produces an immutable result. Crucially,
`passwordHash` is never included — the builder only exposes `id`, `name`, `email`, `role`
and `createdAt`.

**Usage example:**
```java
UserResponseDTO dto = new UserResponseDTO.Builder()
        .id(user.getId())
        .name(user.getName())
        .email(user.getEmail())
        .role(user.getRole().name())
        .createdAt(user.getCreatedAt().toString())
        .build();
```

---

## ER Diagram
See `docs/DIAGRAMS.md` — Section D1.

## Use Case Diagram
See `docs/DIAGRAMS.md` — Section D2.

## Sequence Diagram (Place Order Flow)
See `docs/DIAGRAMS.md` — Section D3.

---

## Known Limitations

- No real payment gateway — checkout uses mock confirmation only.
- No real-time features (no WebSockets, no live tracking).
- H2 is a file-based database — not recommended for high-traffic production use.
- AI chatbot is scoped to product/marketplace FAQ only; general queries are out of scope.
- Load testing was performed with a minimum of 10 concurrent users for 60 seconds
  using Apache JMeter.