package org.mikita.bankingsystemslab.transaction.api.dto

import org.mikita.bankingsystemslab.transaction.domain.common.Currency

data class CreateAccountRequestDto (
    val currency: Currency,
)
