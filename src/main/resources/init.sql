CREATE TABLE sellers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(16) NOT NULL CHECK (TRIM(name) <> ''),
    contact_info TEXT NOT NULL CHECK (TRIM(contact_info) <> ''),
    registration_date TIMESTAMP NOT NULL
);

CREATE TABLE transactions (
    id SERIAL PRIMARY KEY,
    seller_id INT NOT NULL REFERENCES sellers(id) ON DELETE CASCADE,
    amount NUMERIC(14, 2) NOT NULL CHECK (amount >= 0),
    payment_type VARCHAR(8) NOT NULL CHECK (payment_type IN ('CASH', 'CARD', 'TRANSFER')),
    transaction_date TIMESTAMP NOT NULL
);