package org.mikita.bankingsystemslab.transaction.api.dto

import org.mikita.bankingsystemslab.transaction.domain.account.AccountType
import org.mikita.bankingsystemslab.transaction.domain.account.AccountingType
import org.mikita.bankingsystemslab.transaction.domain.common.Money

data class AccountResponseDto (
    val accountId: Long,
    val accountType: AccountType,
    val accountingType: AccountingType,
    val balance: Money,
)
