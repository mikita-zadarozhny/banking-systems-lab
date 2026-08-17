package org.mikita.bankingsystemslab.transaction.service

import io.mockk.MockKAnnotations
import io.mockk.Ordering
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mikita.bankingsystemslab.transaction.domain.common.Currency
import org.mikita.bankingsystemslab.transaction.domain.common.Money
import org.mikita.bankingsystemslab.transaction.domain.transaction.TransactionType
import org.mikita.bankingsystemslab.transaction.service.command.CreateTransactionCommand
import org.mikita.bankingsystemslab.transaction.service.processor.TransactionProcessorContainer
import java.util.UUID

class TransactionServiceTest {

    @MockK
    lateinit var transactionProcessorContainer: TransactionProcessorContainer

    lateinit var transactionService: TransactionService

    @BeforeEach
    fun beforeEach() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        transactionService = TransactionService(transactionProcessorContainer)
    }

    @Test
    fun createTransaction() {

        // given
        val createTransactionCommand = CreateTransactionCommand(
            idempotencyKey = UUID.randomUUID().toString(),
            money = Money(Currency.USD, 100),
            sender = 100,
            recipient = 200,
            type = TransactionType.TRANSFER
        )

        val expectedTransactionId = UUID.randomUUID().toString()

        every { transactionProcessorContainer.process(createTransactionCommand) } returns expectedTransactionId

        // when
        val actualTransactionId = transactionService.createTransaction(createTransactionCommand)

        // given
        assertEquals(expectedTransactionId, actualTransactionId)
        verify (ordering = Ordering.SEQUENCE) {
            transactionProcessorContainer.process(createTransactionCommand)
        }
    }
}