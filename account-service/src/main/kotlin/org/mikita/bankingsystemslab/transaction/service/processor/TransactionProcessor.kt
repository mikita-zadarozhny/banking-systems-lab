package org.mikita.bankingsystemslab.transaction.service.processor

import org.mikita.bankingsystemslab.transaction.domain.transaction.TransactionType
import org.mikita.bankingsystemslab.transaction.service.command.CreateTransactionCommand

interface TransactionProcessor {

    fun getType(): TransactionType

    fun process(createTransactionCommand: CreateTransactionCommand): String
}
