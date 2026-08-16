package org.mikita.bankingsystemslab.transaction.service.command

import org.mikita.bankingsystemslab.transaction.domain.Currency

data class CreateAccountCommand (
    val currency: Currency
)
