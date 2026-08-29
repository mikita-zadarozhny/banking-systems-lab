package org.mikita.bankingsystemslab.transaction.repository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mikita.bankingsystemslab.transaction.domain.transaction.Transaction
import org.mikita.bankingsystemslab.transaction.domain.transaction.TransactionType
import org.mikita.bankingsystemslab.transaction.exception.TransactionIdClashException
import org.mikita.bankingsystemslab.transaction.exception.TransactionIdempotencyKeyClashException
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
class TransactionRepositoryTest : BaseJdbcTest() {

    lateinit var transactionRepository: TransactionRepository

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
    @Tag("NotImplemented")
    @Disabled("Not Implemented")
    fun shouldFindById_whenTransactionExists() {
    }

    @Test
    @Tag("NotImplemented")
    @Disabled("Not Implemented")
    fun shouldReturnEmptyOptional_whenFindById_andTransactionDoesNotExist() {
    }

    @Test
    @Tag("NotImplemented")
    @Disabled("Not Implemented")
    fun shouldGetById_whenTransactionExists() {
    }

    @Test
    @Tag("NotImplemented")
    @Disabled("Not Implemented")
    fun shouldThrowException_whenGetById_andTransactionDoesNotExist() {
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