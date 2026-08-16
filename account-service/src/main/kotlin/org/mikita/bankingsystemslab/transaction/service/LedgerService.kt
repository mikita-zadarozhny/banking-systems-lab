package org.mikita.bankingsystemslab.transaction.service

import org.mikita.bankingsystemslab.transaction.domain.LedgerEntry
import org.mikita.bankingsystemslab.transaction.repository.LedgerEntryRepository
import org.mikita.bankingsystemslab.transaction.service.command.AppendToLedgerCommand
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LedgerService (
    private val ledgerEntryRepository: LedgerEntryRepository
) {
    @Transactional
    fun appendToLedger(appendToLedgerCommand: AppendToLedgerCommand) {

        val senderDelta = -appendToLedgerCommand.money
        val senderLedgerEntry = LedgerEntry(
            appendToLedgerCommand.transactionId,
            appendToLedgerCommand.sender,
            senderDelta
        )

        val recipientLedgerEntry = LedgerEntry(
            appendToLedgerCommand.transactionId,
            appendToLedgerCommand.recipient,
            appendToLedgerCommand.money
        )

        ledgerEntryRepository.saveAll(arrayOf(senderLedgerEntry, recipientLedgerEntry));
    }
}
