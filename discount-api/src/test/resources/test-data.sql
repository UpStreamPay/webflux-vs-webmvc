TRUNCATE product CASCADE;
TRUNCATE customer CASCADE;

INSERT INTO product(id, name, unit_price, brand_name)
VALUES (1, 'Product 1', 4299, 'ACME'),
       (2, 'Product 2', 99, 'FOO');

INSERT INTO customer(id, first_name, last_name, email, birth_date)
VALUES (1, 'John', 'Smith', 'john.smith@example.com', '1980-01-01'),
       (2, 'Jane', 'Doe', 'jane.doe@example.com', '1981-02-02');