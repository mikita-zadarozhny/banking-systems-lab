package org.mikita.bankingsystemslab.transaction.service

import org.mikita.bankingsystemslab.transaction.domain.ledger.LedgerEntry
import org.mikita.bankingsystemslab.transaction.repository.LedgerEntryRepository
import org.mikita.bankingsystemslab.transaction.service.command.AppendToLedgerCommand
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LedgerService (
    private val ledgerEntryRepository: LedgerEntryRepository
) {
    @Transactional
    fun appendToLedger(appendToLedgerCommands: Array<AppendToLedgerCommand>) {

        val ledgerEntries = arrayListOf<LedgerEntry>()
        for (command in appendToLedgerCommands) {
            val senderLedgerEntry = LedgerEntry(
                transactionId = command.transactionId,
                entryType = command.entryType,
                accountId = command.accountId,
                money = command.money,
            )
            ledgerEntries.add(senderLedgerEntry)
        }

        ledgerEntryRepository.saveAll(ledgerEntries)
    }
}
