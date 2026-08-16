package org.mikita.bankingsystemslab.transaction.domain

import org.mikita.bankingsystemslab.transaction.exception.CurrencyMismatchException
import java.math.BigDecimal

data class Money (
    val currency: Currency,
    val amount: BigDecimal
) {
    operator fun plus(other: Money): Money {
        if(other.currency != currency) {
            throw CurrencyMismatchException(currency, other.currency)
        }
        return Money(currency, amount.add(other.amount))
    }

    operator fun compareTo(other: Int): Int {
        return amount.compareTo(BigDecimal(other))
    }

    operator fun unaryMinus(): Money {
        return Money(currency, amount.negate())
    }
}
