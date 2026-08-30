package org.mikita.bankingsystemslab.transaction.exception

class TransactionIdClashException : RuntimeException {
    constructor(idempotencyKey: String) : super("Transaction id '${idempotencyKey}' already exists.")
}
