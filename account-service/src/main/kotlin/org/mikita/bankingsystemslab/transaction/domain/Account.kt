package org.mikita.bankingsystemslab.transaction.domain

data class Account (
    val accountId: Long,
    val balance: Money,
    val version: Int
)
