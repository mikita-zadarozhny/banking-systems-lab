package org.mikita.bankingsystemslab.transaction.api.dto

import org.mikita.bankingsystemslab.transaction.domain.common.Money
import org.mikita.bankingsystemslab.transaction.domain.transaction.TransactionType

data class CreateTransactionRequestDto(
    val idempotencyKey: String,
    val money: Money,
    val sender: Long?,
    val recipient: Long?,
    val type: TransactionType
)
