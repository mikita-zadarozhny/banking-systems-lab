package org.mikita.bankingsystemslab.transaction.service.command

import org.mikita.bankingsystemslab.transaction.domain.common.Money
import org.mikita.bankingsystemslab.transaction.domain.ledger.LedgerEntryType

data class AppendToLedgerCommand (
    val transactionId: String,
    val entryType: LedgerEntryType,
    val accountId: Long,
    val money: Money
)
