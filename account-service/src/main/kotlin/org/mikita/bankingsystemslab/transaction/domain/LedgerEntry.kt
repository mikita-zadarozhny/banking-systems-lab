package org.mikita.bankingsystemslab.transaction.domain

data class LedgerEntry (
    val transactionId: String,
    val accountId: Long,
    val money: Money
)
