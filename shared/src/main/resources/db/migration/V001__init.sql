CREATE TABLE customer
(
    id         SERIAL PRIMARY KEY,
    first_name TEXT NOT NULL,
    last_name  TEXT NOT NULL,
    email      TEXT NOT NULL,
    birth_date DATE NOT NULL
);

CREATE TABLE product
(
    id         SERIAL PRIMARY KEY,
    name       TEXT    NOT NULL,
    unit_price INTEGER NOT NULL,
    brand_name TEXT    NOT NULL
);

CREATE TABLE purchase_order
(
    id          SERIAL PRIMARY KEY,
    customer_id INTEGER NOT NULL REFERENCES customer (id),
    total_amount INTEGER NOT NULL,
    discount_amount INTEGER NOT NULL
);

CREATE TABLE purchase_order_item
(
    order_id   INTEGER NOT NULL REFERENCES purchase_order (id),
    rank       INTEGER NOT NULL,
    product_id INTEGER NOT NULL REFERENCES product (id),
    quantity   INTEGER NOT NULL,
    unit_price INTEGER NOT NULL,
    PRIMARY KEY (order_id, rank)
);
