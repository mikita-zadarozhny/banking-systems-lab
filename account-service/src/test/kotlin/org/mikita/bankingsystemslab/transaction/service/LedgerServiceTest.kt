package org.mikita.bankingsystemslab.transaction.service

import io.mockk.MockKAnnotations
import io.mockk.Ordering
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mikita.bankingsystemslab.transaction.domain.common.Currency
import org.mikita.bankingsystemslab.transaction.domain.common.Money
import org.mikita.bankingsystemslab.transaction.domain.ledger.LedgerEntry
import org.mikita.bankingsystemslab.transaction.domain.ledger.LedgerEntryType
import org.mikita.bankingsystemslab.transaction.repository.LedgerEntryRepository
import org.mikita.bankingsystemslab.transaction.service.command.AppendToLedgerCommand
import java.util.UUID
import java.util.stream.Stream

class LedgerServiceTest {

    @MockK
    lateinit var ledgerEntryRepository: LedgerEntryRepository

    lateinit var ledgerService: LedgerService

    @BeforeEach
    fun beforeEach() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        ledgerService = LedgerService(ledgerEntryRepository)
    }

    @ParameterizedTest
    @MethodSource("appendToLedgerHappyCases")
    fun shouldAppendToLedger(appendLedgerEntryCommands: Array<AppendToLedgerCommand>) {


        // when
        ledgerService.appendToLedger(appendLedgerEntryCommands)

        // then
        val slot = slot<List<LedgerEntry>>()
        verify(Ordering.SEQUENCE) {
            ledgerEntryRepository.saveAll(capture(slot))
        }

        assertEquals(appendLedgerEntryCommands.size, slot.captured.size)
        for ((index, actual) in appendLedgerEntryCommands.withIndex()) {
            assertEquals(appendLedgerEntryCommands[index].transactionId, actual.transactionId)
            assertEquals(appendLedgerEntryCommands[index].accountId, actual.accountId)
            assertEquals(appendLedgerEntryCommands[index].entryType, actual.entryType)
            assertEquals(appendLedgerEntryCommands[index].money, actual.money)
        }
    }

    companion object {
        @JvmStatic
        fun appendToLedgerHappyCases(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    arrayOf(
                        AppendToLedgerCommand(
                            transactionId = UUID.randomUUID().toString(),
                            entryType = LedgerEntryType.CREDIT,
                            accountId = 1000,
                            money = Money(
                                currency = Currency.USD,
                                amount = 100
                            )
                        ),
                        AppendToLedgerCommand(
                            transactionId = UUID.randomUUID().toString(),
                            entryType = LedgerEntryType.DEBIT,
                            accountId = 2000,
                            money = Money(
                                currency = Currency.USD,
                                amount = 100
                            )
                        ),
                        AppendToLedgerCommand(
                            transactionId = UUID.randomUUID().toString(),
                            entryType = LedgerEntryType.CREDIT,
                            accountId = 3000,
                            money = Money(
                                currency = Currency.EUR,
                                amount = 400
                            )
                        ),
                        AppendToLedgerCommand(
                            transactionId = UUID.randomUUID().toString(),
                            entryType = LedgerEntryType.DEBIT,
                            accountId = 4000,
                            money = Money(
                                currency = Currency.EUR,
                                amount = 400
                            )
                        )
                    ),
                    arrayOf(
                        AppendToLedgerCommand(
                            transactionId = UUID.randomUUID().toString(),
                            entryType = LedgerEntryType.CREDIT,
                            accountId = 1000,
                            money = Money(
                                currency = Currency.USD,
                                amount = 100
                            )
                        )
                    )
                )
            )
        }
    }
}