package org.mikita.bankingsystemslab.transaction.domain

data class Account (
    val accountId: Long,
    val accountType: AccountType,
    val balance: Money,
    val version: Int
)
