package org.mikita.bankingsystemslab.transaction.domain.ledger

import org.mikita.bankingsystemslab.transaction.domain.common.Money

data class LedgerEntry (
    val transactionId: String,
    val entryType: LedgerEntryType,
    val accountId: Long,
    val money: Money
)
