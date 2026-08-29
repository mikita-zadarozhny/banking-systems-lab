package org.mikita.bankingsystemslab.transaction.service.processor.impl

import org.mikita.bankingsystemslab.transaction.domain.ledger.LedgerEntryType
import org.mikita.bankingsystemslab.transaction.domain.transaction.Transaction
import org.mikita.bankingsystemslab.transaction.domain.transaction.TransactionType
import org.mikita.bankingsystemslab.transaction.repository.TransactionRepository
import org.mikita.bankingsystemslab.transaction.service.AccountService
import org.mikita.bankingsystemslab.transaction.service.LedgerService
import org.mikita.bankingsystemslab.transaction.service.command.AppendToLedgerCommand
import org.mikita.bankingsystemslab.transaction.service.command.CreateTransactionCommand
import org.mikita.bankingsystemslab.transaction.service.command.UpdateAccountBalanceCommand
import org.mikita.bankingsystemslab.transaction.service.processor.TransactionProcessor
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class TransferTransactionProcessor (
    private val transactionRepository: TransactionRepository,
    private val ledgerService: LedgerService,
    private val accountService: AccountService
) : TransactionProcessor {

    override fun getType(): TransactionType {
        return TransactionType.TRANSFER
    }

    override fun process(createTransactionCommand: CreateTransactionCommand): String {
        validate(createTransactionCommand)

        val transactionId = UUID.randomUUID().toString()

        val transaction = Transaction(
            transactionId,
            createTransactionCommand.idempotencyKey,
            createTransactionCommand.type
        )

        transactionRepository.saveNew(transaction)

        accountService.updateAccountBalance(UpdateAccountBalanceCommand(
            accountId = createTransactionCommand.sender!!,
            delta = -createTransactionCommand.money
        ))

        accountService.updateAccountBalance(UpdateAccountBalanceCommand(
            accountId = createTransactionCommand.recipient!!,
            delta = createTransactionCommand.money
        ))

        ledgerService.appendToLedger(
            arrayOf<AppendToLedgerCommand>(
                AppendToLedgerCommand(
                    transactionId = transactionId,
                    entryType = LedgerEntryType.DEBIT,
                    accountId = createTransactionCommand.sender,
                    money = createTransactionCommand.money
                ),
                AppendToLedgerCommand(
                    transactionId = transactionId,
                    entryType = LedgerEntryType.CREDIT,
                    accountId = createTransactionCommand.recipient,
                    money = createTransactionCommand.money
                )
            )
        )


        return transactionId
    }

    private fun validate(createTransactionCommand: CreateTransactionCommand) {
        if (createTransactionCommand.sender == null) {
            throw RuntimeException("For transfer transaction sender should not be null")
        }
        if (createTransactionCommand.recipient == null) {
            throw RuntimeException("For transfer transaction recipient should not be null")
        }
    }
}
