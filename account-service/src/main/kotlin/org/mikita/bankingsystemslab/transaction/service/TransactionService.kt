package org.mikita.bankingsystemslab.transaction.service

import org.mikita.bankingsystemslab.transaction.service.command.CreateTransactionCommand
import org.mikita.bankingsystemslab.transaction.service.processor.TransactionProcessorContainer
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TransactionService (
    private val transactionProcessorContainer: TransactionProcessorContainer
) {
    @Transactional
    fun createTransaction(createTransactionCommand: CreateTransactionCommand): String {
        return transactionProcessorContainer.process(createTransactionCommand)
    }
}
