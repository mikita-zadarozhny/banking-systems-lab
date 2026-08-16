package org.mikita.bankingsystemslab.transaction.exception

import java.math.BigDecimal

class NegativeAccountBalanceException : RuntimeException {
    constructor(accountId: Long, currentBalance: BigDecimal, delta: BigDecimal)
            : super("Account '${accountId}' balance is ${currentBalance}. " +
            "Subtracting '${delta.abs()}' would result into negative balance.")
}
