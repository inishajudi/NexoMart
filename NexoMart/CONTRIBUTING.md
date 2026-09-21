# Contributing

- Branch from `main` and use conventional commits (`feat:`, `fix:`, `chore:`, `docs:`, `style:`).
- Run `mvn -B clean verify` before pushing. All tests must pass.
- Never commit `config.properties` or `.env`. Use the `.example` files as templates.
- Do not hand-edit `schema.sql` after V1. Add a new migration file in `db/migrations/` instead.