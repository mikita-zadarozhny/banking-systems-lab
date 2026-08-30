package org.mikita.bankingsystemslab.transaction.domain.account

import org.mikita.bankingsystemslab.transaction.domain.common.Money

data class Account (
    val accountId: Long,
    val accountType: AccountType,
    val accountingType: AccountingType,
    val balance: Money,
    val version: Int
)
