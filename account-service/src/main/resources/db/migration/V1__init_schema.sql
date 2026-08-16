CREATE TABLE accounts (
    account_id BIGINT PRIMARY KEY,
    currency INTEGER NOT NULL,
    account_type INTEGER NOT NULL,
    balance DECIMAL NOT NULL,
    version INTEGER NOT NULL
);

CREATE TABLE transactions (
    id VARCHAR(64) PRIMARY KEY,
    idempotency_key VARCHAR(64) NOT NULL UNIQUE,
    transaction_type INTEGER NOT NULL
);

CREATE TABLE ledger (
    transaction_id VARCHAR(64) REFERENCES transactions(id),
    account_id BIGINT REFERENCES accounts(account_id),
    currency INTEGER NOT NULL,
    amount DECIMAL NOT NULL
);

CREATE SEQUENCE account_id_seq START 1000000000;

INSERT INTO accounts (account_id, account_type, currency, balance, version) VALUES
(100000, 11, 1000, 0, 0), -- CUSTOMER_DEPOSIT_LIABILITY_USD
(100001, 11, 1001, 0, 0) -- CUSTOMER_DEPOSIT_LIABILITY_EUR
