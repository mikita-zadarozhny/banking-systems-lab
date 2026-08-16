package org.mikita.bankingsystemslab.transaction.service.command

import org.mikita.bankingsystemslab.transaction.domain.Money

data class UpdateBalanceCommand(val accountId: Long, val delta: Money)
