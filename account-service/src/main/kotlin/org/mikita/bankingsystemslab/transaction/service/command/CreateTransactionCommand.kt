package org.mikita.bankingsystemslab.transaction.service.command

import org.mikita.bankingsystemslab.transaction.domain.common.Money
import org.mikita.bankingsystemslab.transaction.domain.transaction.TransactionType

data class CreateTransactionCommand (
    val idempotencyKey: String,
    val money: Money,
    val sender: Long?,
    val recipient: Long?,
    val type: TransactionType
)
