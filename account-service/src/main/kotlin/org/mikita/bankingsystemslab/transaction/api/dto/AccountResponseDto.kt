package org.mikita.bankingsystemslab.transaction.api.dto

import org.mikita.bankingsystemslab.transaction.domain.Money

data class AccountResponseDto (
    val accountId: Long,
    val balance: Money
)
