# Changelog

## [1.1.0] - 2026-10-09
### Added
- AI chatbot (GeminiChatProvider + MockChatProvider, Strategy pattern via ChatProvider interface)
- Order status workflow: CONFIRMED → SHIPPED → DELIVERED with seller action buttons
- UserResponseDTO with Builder pattern
- RequestIdFilter: per-request UUID attached to SLF4J MDC
- Checkstyle and SpotBugs Maven plugins
- V3 migration: created_at column on order_items

## [1.0.0] - 2026-09-21
### Added
- Multi-seller marketplace: accounts, product listings, cart, mock checkout, orders
- Reviews, seller order view and admin moderation
- Custom 404 and 500 pages
- Sand theme across all pages, 20 seeded products in 4 categories
- CI with GitHub Actions, deployment on Render
