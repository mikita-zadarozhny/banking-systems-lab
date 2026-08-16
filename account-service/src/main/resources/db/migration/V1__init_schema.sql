CREATE TABLE accounts (
    account_id BIGINT PRIMARY KEY,
    currency CHAR(3) NOT NULL CHECK (currency IN ('USD', 'EUR')),
    balance DECIMAL NOT NULL CHECK (balance >= 0),
    version INTEGER NOT NULL
);

CREATE TABLE transactions (
    id VARCHAR(64) PRIMARY KEY,
    idempotency_key VARCHAR(64) NOT NULL UNIQUE,
    transaction_type VARCHAR(16) NOT NULL CHECK (transaction_type IN ('TRANSFER'))
);

CREATE TABLE ledger (
    transaction_id VARCHAR(64) REFERENCES transactions(id),
    account_id BIGINT REFERENCES accounts(account_id),
    currency CHAR(3) NOT NULL CHECK (currency IN ('USD', 'EUR')),
    amount DECIMAL NOT NULL
);

CREATE SEQUENCE account_id_seq START 1000000000;

INSERT INTO accounts (account_id, currency, balance, version) VALUES
(100, 'USD', 1000000000, 0),
(101, 'EUR', 1000000000, 0);
