package org.mikita.bankingsystemslab.transaction.domain.account


enum class AccountingType {
    ASSET, // Debits increase it (money in), Credits decrease it (money out).
    LIABILITY // Credits increase it (money in), Debits decrease it (money out).
}
