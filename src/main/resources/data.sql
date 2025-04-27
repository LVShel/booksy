-- Customers
INSERT INTO customers (id, name, loyalty_points)
VALUES (1, 'John Doe', 0),
       (2, 'Bob Builder', 5),
       (3, 'John Snow ', 9);

ALTER TABLE customers ALTER COLUMN id RESTART WITH 4;

-- Books
INSERT INTO books (id, isbn, title, base_price, book_type, stock, created_at, status)
VALUES
    (1, '9780000000001', 'Modern Java', 49.99, 'NEW_RELEASE', 5, CURRENT_TIMESTAMP, 'ACTIVE'),
    (2, '9780000000002', 'Spring Boot in Action', 29.99, 'REGULAR', 10, CURRENT_TIMESTAMP, 'ACTIVE'),
    (3, '9780000000003', 'Old Tales', 19.99, 'OLD_EDITION', 8, CURRENT_TIMESTAMP, 'ACTIVE'),
    (4, '9780000000004', 'Clean Arhitecture', 19.99, 'REGULAR', 99, CURRENT_TIMESTAMP, 'ACTIVE');

-- Reset H2 Identity sequence
ALTER TABLE books ALTER COLUMN id RESTART WITH 5;