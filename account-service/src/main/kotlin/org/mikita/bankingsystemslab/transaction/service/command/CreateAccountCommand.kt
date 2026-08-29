package org.mikita.bankingsystemslab.transaction.service.command

import org.mikita.bankingsystemslab.transaction.domain.common.Currency

data class CreateAccountCommand (
    val currency: Currency
)
