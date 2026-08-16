package org.mikita.bankingsystemslab.transaction.api.dto

import org.mikita.bankingsystemslab.transaction.domain.Currency

data class CreateAccountRequestDto (
    val currency: Currency,
)
