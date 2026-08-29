package org.mikita.bankingsystemslab.transaction.domain.transaction

data class Transaction(
    val id: String,
    val idempotencyKey: String,
    val type: TransactionType
)
