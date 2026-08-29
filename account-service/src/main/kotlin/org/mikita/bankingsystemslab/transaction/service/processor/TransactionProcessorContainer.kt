package org.mikita.bankingsystemslab.transaction.service.processor

import org.mikita.bankingsystemslab.transaction.domain.transaction.TransactionType
import org.mikita.bankingsystemslab.transaction.service.command.CreateTransactionCommand

class TransactionProcessorContainer (
    private val typeToProcessor: MutableMap<TransactionType, TransactionProcessor>
) {

    fun process(createTransactionCommand: CreateTransactionCommand): String {
        return typeToProcessor.get(createTransactionCommand.type)!!.process(createTransactionCommand)
    }
}
