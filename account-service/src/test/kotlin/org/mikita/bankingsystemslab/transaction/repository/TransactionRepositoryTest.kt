package org.mikita.bankingsystemslab.transaction.repository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mikita.bankingsystemslab.transaction.domain.transaction.Transaction
import org.mikita.bankingsystemslab.transaction.domain.transaction.TransactionType
import org.mikita.bankingsystemslab.transaction.exception.TransactionDoesNotExistException
import org.mikita.bankingsystemslab.transaction.exception.TransactionIdClashException
import org.mikita.bankingsystemslab.transaction.exception.TransactionIdempotencyKeyClashException
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
class TransactionRepositoryTest : BaseJdbcTest() {

    private lateinit var transactionRepository: TransactionRepository

    @BeforeEach
    override fun setUp() {
        super.setUp()

        transactionRepository = TransactionRepository(jdbcTemplate)
    }

    @Test
    fun shouldSaveNew_whenNoConflictingTransactionExists() {

        // given
        val expected = Transaction(
            "transaction-id",
            "idempotency-key",
            TransactionType.TRANSFER
        )

        // when
        val actual = transactionRepository.saveNew(expected)

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun shouldFindById_whenTransactionExists() {

        // given
        val transaction = Transaction(
            "transaction-id",
            "idempotency-key",
            TransactionType.TRANSFER
        )

        val expected = transactionRepository.saveNew(transaction)

        // when
        val actual = transactionRepository.findById(expected.id)

        // then
        assertTrue(actual.isPresent)
        assertEquals(expected, actual.get())
    }

    @Test
    fun shouldReturnEmptyOptional_whenFindById_andTransactionDoesNotExist() {

        // when
        val actual = transactionRepository.findById("non-existing-transaction-id")

        // then
        assertTrue(actual.isEmpty)
    }

    @Test
    fun shouldGetById_whenTransactionExists() {

        // given
        val transaction = Transaction(
            "transaction-id",
            "idempotency-key",
            TransactionType.TRANSFER
        )

        val expected = transactionRepository.saveNew(transaction)

        // when
        val actual = transactionRepository.getById(expected.id)

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun shouldThrowException_whenGetById_andTransactionDoesNotExist() {

        // when
        val exception = assertThrows<TransactionDoesNotExistException> {
            transactionRepository.getById("non-existing-transaction-id")
        }

        // then
        assertEquals("Transaction 'non-existing-transaction-id' does not exist.", exception.message)
    }

    @Test
    fun shouldThrowException_whenSaveNew_andConflictingIdempotencyKeyExists() {

        // given
        val transaction = Transaction(
            "transaction-id-1",
            "idempotency-key",
            TransactionType.TRANSFER
        )
        val conflictingTransaction = Transaction(
            "transaction-id-2",
            "idempotency-key",
            TransactionType.TRANSFER
        )

        // when
        val actual = transactionRepository.saveNew(transaction)

        // then
        assertEquals(transaction, actual)

        // when
        val exception = assertThrows<TransactionIdempotencyKeyClashException> {
            transactionRepository.saveNew(conflictingTransaction)
        }

        // then
        assertEquals("Idempotency key 'idempotency-key' already exists.", exception.message)
    }


    @Test
    fun shouldThrowException_whenSaveNew_andConflictingTransactionKeyExists() {

        // given
        val transaction = Transaction(
            "transaction-id",
            "idempotency-key-1",
            TransactionType.TRANSFER
        )
        val conflictingTransaction = Transaction(
            "transaction-id",
            "idempotency-key-2",
            TransactionType.TRANSFER
        )

        // when
        val actual = transactionRepository.saveNew(transaction)

        // then
        assertEquals(transaction, actual)

        // when
        val exception = assertThrows<TransactionIdClashException> {
            transactionRepository.saveNew(conflictingTransaction)
        }

        // then
        assertEquals("Transaction id 'transaction-id' already exists.", exception.message)
    }

}