package org.mikita.bankingsystemslab.transaction.repository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mikita.bankingsystemslab.transaction.domain.common.Currency
import org.mikita.bankingsystemslab.transaction.domain.common.Money
import org.mikita.bankingsystemslab.transaction.domain.ledger.LedgerEntry
import org.mikita.bankingsystemslab.transaction.domain.ledger.LedgerEntryType
import org.mikita.bankingsystemslab.transaction.domain.transaction.Transaction
import org.mikita.bankingsystemslab.transaction.domain.transaction.TransactionType

class LedgerRepositoryTest : BaseJdbcTest() {

    private lateinit var ledgerEntryRepository: LedgerEntryRepository
    private lateinit var transactionRepository: TransactionRepository
    private lateinit var accountRepository: AccountRepository

    @BeforeEach
    override fun setUp() {
        super.setUp()

        ledgerEntryRepository = LedgerEntryRepository(jdbcTemplate)
        transactionRepository = TransactionRepository(jdbcTemplate)
        accountRepository = AccountRepository(jdbcTemplate)
    }

    @Test
    fun shouldFindAllLedgerEntries_whenLedgerEntriesForTransactionIdExists() {

        // given
        val account1 = accountRepository.saveNew(Currency.USD)
        val account2 = accountRepository.saveNew(Currency.USD)
        val transaction = transactionRepository.saveNew(
            Transaction(
                "transaction-id",
                "idempotency-key",
                TransactionType.TRANSFER
            )
        )
        val ledgerEntry1 = LedgerEntry(
            transactionId = transaction.id,
            entryType = LedgerEntryType.DEBIT,
            accountId = account1.accountId,
            money = Money(Currency.USD, 200)
        )
        val ledgerEntry2 = LedgerEntry(
            transactionId = transaction.id,
            entryType = LedgerEntryType.CREDIT,
            accountId = account2.accountId,
            money = Money(Currency.USD, 200)
        )

        val expected = listOf(
            ledgerEntry1,
            ledgerEntry2
        )

        ledgerEntryRepository.saveAll(listOf(ledgerEntry1, ledgerEntry2))

        // when
        val actual = ledgerEntryRepository.findAllByTransactionId(transaction.id)

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun shouldGetSumOfLedgerEntries() {

        // given
        val account1 = accountRepository.saveNew(Currency.USD)
        val account2 = accountRepository.saveNew(Currency.USD)
        val transaction = transactionRepository.saveNew(
            Transaction(
                "transaction-id",
                "idempotency-key",
                TransactionType.TRANSFER
            )
        )
        val ledgerEntry1 = LedgerEntry(
            transactionId = transaction.id,
            entryType = LedgerEntryType.DEBIT,
            accountId = account1.accountId,
            money = Money(Currency.USD, 200)
        )
        val ledgerEntry2 = LedgerEntry(
            transactionId = transaction.id,
            entryType = LedgerEntryType.CREDIT,
            accountId = account2.accountId,
            money = Money(Currency.USD, 200)
        )

        val expectedDebit = Money(
            currency = Currency.USD,
            amount = 200
        )

        val expectedCredit = Money(
            currency = Currency.USD,
            amount = 200
        )

        ledgerEntryRepository.saveAll(listOf(ledgerEntry1, ledgerEntry2))

        // when
        val actualDebit = ledgerEntryRepository.getSumOfLedgerEntries(LedgerEntryType.DEBIT, Currency.USD)
        val actualCredit = ledgerEntryRepository.getSumOfLedgerEntries(LedgerEntryType.CREDIT, Currency.USD)

        // then
        assertEquals(expectedDebit, actualDebit)
        assertEquals(expectedCredit, actualCredit)
        assertEquals(actualDebit, actualCredit)
    }
}