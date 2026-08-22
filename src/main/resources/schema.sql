CREATE TABLE IF NOT EXISTS accounts (
    id BIGINT PRIMARY KEY,
    customer_name VARCHAR(255),
    balance NUMERIC(19,2)
);
