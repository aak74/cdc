CREATE TABLE customers (
    id INTEGER NOT NULL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    user_id INTEGER NOT NULL
);

CREATE TABLE history_changes (
    id SERIAL NOT NULL PRIMARY KEY,
    created_at timestamptz NOT NULL,
    table_name text NOT NULL,
    entity_id uuid NOT NULL,
    operation char(1) NOT NULL,
    user_id int NOT NULL,
    before jsonb NOT NULL,
    after jsonb NOT NULL
);
CREATE INDEX created_at ON history_changes(created_at);
CREATE INDEX entity_id ON history_changes USING btree(entity_id);

CREATE TABLE products (
    id SERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price INTEGER NOT NULL,
    user_id INTEGER NOT NULL
);

--CREATE SUBSCRIPTION dbz_subscription CONNECTION 'host=pg-source port=5432 dbname=source_db user=postgres password=postgres' PUBLICATION dbz_publication;

