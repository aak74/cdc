CREATE TABLE customers (
    id SERIAL NOT NULL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    user_id INTEGER NOT NULL
);
ALTER SEQUENCE customers_id_seq RESTART WITH 1001;

INSERT INTO customers
VALUES (default,'Sally','Thomas','sally.thomas@acme.com', 10),
       (default,'George','Bailey','gbailey@foobar.com', 11),
       (default,'Edward','Walker','ed@walker.com', 12),
       (default,'Anne','Kretchmar','annek@noanswer.org', 13);

CREATE TABLE products (
    id SERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price INTEGER NOT NULL,
    user_id INTEGER NOT NULL
);

INSERT INTO products
VALUES (default,'Orange', 5, 11),
       (default,'Olive', 4, 12),
       (default,'Tomato', 3, 14),
       (default,'Eggplant', 3, 6),
       (default,'Melon', 5, 18),
       (default,'Grape', 7, 21);

CREATE PUBLICATION dbz_publication FOR TABLE public.customers, public.products;
SELECT pg_create_logical_replication_slot('dbz_publication', 'pgoutput');

CREATE ROLE debezium REPLICATION LOGIN;