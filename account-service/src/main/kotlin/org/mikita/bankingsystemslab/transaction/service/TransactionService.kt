package org.mikita.bankingsystemslab.transaction.service

import org.mikita.bankingsystemslab.transaction.domain.Transaction
import org.mikita.bankingsystemslab.transaction.repository.TransactionRepository
import org.mikita.bankingsystemslab.transaction.service.command.AppendToLedgerCommand
import org.mikita.bankingsystemslab.transaction.service.command.CreateTransactionCommand
import org.mikita.bankingsystemslab.transaction.service.command.UpdateBalanceCommand
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class TransactionService (
    private val transactionRepository: TransactionRepository,
    private val ledgerService: LedgerService,
    private val accountService: AccountService
) {
    @Transactional
    fun createTransaction(createTransactionCommand: CreateTransactionCommand): String {

        val transactionId = UUID.randomUUID().toString()

        val transaction = Transaction(
            transactionId,
            createTransactionCommand.idempotencyKey,
            createTransactionCommand.type
        )

        transactionRepository.save(transaction)

        accountService.updateBalance(UpdateBalanceCommand(
            createTransactionCommand.sender,
            -createTransactionCommand.money
        ))

        accountService.updateBalance(UpdateBalanceCommand(
            createTransactionCommand.recipient,
            createTransactionCommand.money
        ))

        ledgerService.appendToLedger(AppendToLedgerCommand(
            transactionId,
            createTransactionCommand.idempotencyKey,
            createTransactionCommand.money,
            createTransactionCommand.sender,
            createTransactionCommand.recipient,
            createTransactionCommand.type
        ))

        return transactionId
    }
}
