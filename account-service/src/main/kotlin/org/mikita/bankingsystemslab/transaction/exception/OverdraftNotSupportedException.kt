package org.mikita.bankingsystemslab.transaction.exception

import java.math.BigDecimal

class OverdraftNotSupportedException : RuntimeException {
    constructor(accountId: Long, currentBalance: BigDecimal, delta: BigDecimal)
            : super("Account '${accountId}' liability is '${currentBalance}'. " +
            "Applying liability delta '${delta}' would result into overdraft.")
}
