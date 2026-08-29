package org.mikita.bankingsystemslab.transaction.domain.common

import com.fasterxml.jackson.annotation.JsonCreator
import org.mikita.bankingsystemslab.transaction.exception.CurrencyMismatchException
import java.math.BigDecimal

data class Money @JsonCreator constructor (
    val currency: Currency,
    val amount: BigDecimal
) {

    constructor(currency: Currency, amount: Long) : this(currency, BigDecimal.valueOf(amount))

    operator fun plus(other: Money): Money {
        if(other.currency != currency) {
            throw CurrencyMismatchException(currency, other.currency)
        }
        return Money(currency, amount.add(other.amount))
    }

    operator fun minus(other: Money): Money {
        if(other.currency != currency) {
            throw CurrencyMismatchException(currency, other.currency)
        }
        return Money(currency, amount.subtract(other.amount))
    }

    operator fun compareTo(other: Int): Int {
        return amount.compareTo(BigDecimal(other))
    }

    operator fun unaryMinus(): Money {
        return Money(currency, amount.negate())
    }
}
