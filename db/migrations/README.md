# Migrations

Each schema change gets a new numbered file here (spec Section 14):
`V1__init_schema.sql`, `V2__add_reviews_table.sql`, ...

`V1__init_schema.sql` mirrors `src/main/resources/schema.sql` as of Week 1.
Never hand-edit a live table - add a new numbered migration instead.
