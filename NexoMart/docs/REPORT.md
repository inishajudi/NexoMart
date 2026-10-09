# NexoMart Final Report

## Introduction

NexoMart is a multi-seller online marketplace web application. Sellers can add and manage products, while buyers can browse products, search and filter products, add products to Cart, save products to Wishlist, place orders and write reviews. Admins can manage users, products and orders through the Admin Dashboard.

The application also includes an AI Chat Assistant that helps users with common marketplace-related questions.

- **Stack:** Java 17, Maven, Tomcat 9, Servlet, JSP, JSTL, JDBC, H2, HikariCP, jBCrypt, SLF4J/Logback, Gson, JUnit 5 and Mockito.
- **Code:** https://github.com/inishajudi/NexoMart
- **Live site:** https://nexomart-web.onrender.com

## Technical Decisions

1. **Servlet, JSP and JDBC, without a large framework.**  
   NexoMart uses basic Java web technologies so that the request flow and application logic are simple to understand and maintain.

2. **H2 database with HikariCP.**  
   H2 provides a lightweight relational database, while HikariCP manages database connections efficiently.

3. **Layered application structure.**  
   The application separates Filters, Controllers, Services, DAOs, Models and Views. This makes the system easier to maintain and extend.

4. **Secure password storage.**  
   Passwords are hashed using jBCrypt instead of being stored as plain text.

5. **Role-based access.**  
   NexoMart provides separate functionality for Buyers, Sellers and Admins.

6. **AI Chat Assistant.**  
   The application supports Gemini AI integration and also provides a mock chatbot provider for testing and fallback.

7. **Automated testing.**  
   JUnit 5 and Mockito are used to test important services, DAOs and chatbot components.

8. **Docker and Render deployment.**  
   The application includes Docker configuration and is deployed on Render.

## Architecture

NexoMart follows a layered architecture where each layer has a specific responsibility.

1. **Filters** handle encoding and authentication.
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
Filters
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
