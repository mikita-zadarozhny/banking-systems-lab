package org.mikita.bankingsystemslab.transaction.domain

data class Transaction(
    val id: String,
    val idempotencyKey: String,
    val type: TransactionType
)
