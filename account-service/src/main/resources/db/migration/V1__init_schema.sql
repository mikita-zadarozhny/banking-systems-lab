CREATE TABLE accounts (
    account_id BIGINT PRIMARY KEY,
    currency CHAR(3) NOT NULL,
    account_type VARCHAR(64) NOT NULL,
    accounting_type VARCHAR(64) NOT NULL,
    balance DECIMAL NOT NULL,
    version INTEGER NOT NULL
);

CREATE TABLE transactions (
    id VARCHAR(64) PRIMARY KEY,
    idempotency_key VARCHAR(64) NOT NULL UNIQUE,
    transaction_type VARCHAR(64) NOT NULL
);

CREATE TABLE ledger (
    transaction_id VARCHAR(64) REFERENCES transactions(id),
    entry_type VARCHAR(6),
    account_id BIGINT REFERENCES accounts(account_id),
    currency CHAR(3) NOT NULL,
    amount DECIMAL NOT NULL
);

CREATE SEQUENCE account_id_seq START 1000000000;

INSERT INTO accounts (account_id, account_type, accounting_type, currency, balance, version) VALUES
(100000, 'CASH', 'ASSET', 'USD', 0, 0), -- CUSTOMER_DEPOSIT_LIABILITY_USD
(100001, 'CASH', 'ASSET', 'EUR', 0, 0) -- CUSTOMER_DEPOSIT_LIABILITY_EUR
