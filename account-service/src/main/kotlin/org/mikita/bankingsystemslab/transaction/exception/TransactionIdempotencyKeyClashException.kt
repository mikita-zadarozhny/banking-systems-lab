package org.mikita.bankingsystemslab.transaction.exception

class TransactionIdempotencyKeyClashException : RuntimeException {
    constructor(idempotencyKey: String) : super("Idempotency key '${idempotencyKey}' already exists.")
}
