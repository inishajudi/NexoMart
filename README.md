# NexoMart

Multi-seller e-commerce marketplace. Anna University R2025, Semester 3 Java capstone
(Servlets · JDBC · Apache Tomcat). Sellers list products, buyers browse/cart/checkout,
admin moderates users, listings, and orders.

**Status: Week 1** — authentication (register/login/session), base DAO layer, project
skeleton running locally. Product/cart/order features land in later weeks per the
project timeline.

## Tech stack

| Component | Choice |
|---|---|
| JDK | 17 |
| Servlet container | Tomcat 9.0.x |
| Build tool | Maven |
| Database | H2 (file/embedded locally, server mode when deployed) |
| Connection pool | HikariCP |
| View layer | JSP + JSTL |
| JSON | Gson |
| Password hashing | jBCrypt |
| Testing | JUnit 5 + Mockito |
| Logging | SLF4J + Logback |
| CI | GitHub Actions (`mvn -B clean verify` on every push) |

## Architecture

```
Browser (JSP-rendered pages)
  |
  v
EncodingFilter -> AuthFilter (session check on protected routes)
  |
  v
Servlets (controller) -- thin, no SQL, no business logic
  |
  v
Services (service) -- business rules & validation, no JDBC
  |
  v
DAOs (dao) -- all SQL lives here, PreparedStatement only
  |
  v
HikariCP pool (owned by DataSourceListener, started once at app startup)
  |
  v
H2 database
```

Package layout: `com.nexo.nexomart.{controller,service,dao,model,dto,filter,listener,util,exception}`

## Setup instructions

**Prerequisites:** JDK 17, Maven, Tomcat 9.0.x installed locally (or use `mvn tomcat7:run`-style
plugin of your choice — this skeleton assumes deploying the built `.war` to a Tomcat
instance you already have).

1. Clone the repo and `cd NexoMart`.
2. Copy the config template:
   ```
   cp src/main/resources/config.properties.example src/main/resources/config.properties
   ```
   Defaults work out of the box for local file-mode H2 (`./data/nexomart`).
3. Generate a real admin password hash (don't skip this — `seed.sql` ships with
   placeholders on purpose):
   ```
   mvn -q compile
   mvn -q exec:java -Dexec.mainClass=com.nexo.nexomart.util.GenerateAdminHash -Dexec.args="YourChosenPassword"
   ```
   Paste the printed hash into `src/main/resources/seed.sql` in place of each
   `REPLACE_WITH_BCRYPT_HASH`.
4. Build:
   ```
   mvn clean package
   ```
5. Deploy `target/nexomart.war` to Tomcat's `webapps/` folder (or copy/symlink it there),
   start Tomcat, then visit `http://localhost:8080/nexomart/`.
   - `schema.sql` is applied automatically on first startup.
   - To load `seed.sql`, run it once against the running DB (H2 console, or
     `org.h2.tools.RunScript`) after step 3.
6. Register a Buyer or Seller account from the UI, or log in with the seeded admin
   email (`admin@nexomart.local`) and the password you chose in step 3.

Run tests: `mvn test` (DAO tests run against an isolated in-memory H2 instance,
independent of your local `data/nexomart` file).

## Deployed link

_Not yet deployed — targeted for the Full Build + Deploy checkpoint (Sep 21)._

## Screenshots

_To be added once the register/login UI is running locally._

## Notes

- `config.properties` and `.env` are git-ignored; use `config.properties.example` /
  `.env.example` as templates.
- Admin accounts are never created through public signup — only via `seed.sql`
  (see step 3 above).
