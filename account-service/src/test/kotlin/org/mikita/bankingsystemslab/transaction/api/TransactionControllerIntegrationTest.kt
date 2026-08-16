package org.mikita.bankingsystemslab.transaction.api

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mikita.bankingsystemslab.transaction.domain.Currency
import org.mikita.bankingsystemslab.transaction.domain.Money

class TransactionControllerIntegrationTest : BaseIntegrationTest() {

    @Test
    fun shouldDeposit() {
        val account = createAccountAndGetIt(Currency.USD)

        depositUsd(account, 100)

        val accountAfterDeposit = getAccount(account)

        assertEquals(Money(Currency.USD, -100), accountAfterDeposit.balance)
    }

    @Test
    fun shouldTransfer() {
        val account1 = createAccountAndGetIt(Currency.USD)
        val account2 = createAccountAndGetIt(Currency.USD)

        depositUsd(account1, 1000)
        transfer(account1, account2, Money(Currency.USD, 700))

        val account1AfterTransfer = getAccount(account1)
        val account2AfterTransfer = getAccount(account2)

        assertEquals(Money(Currency.USD, -300), account1AfterTransfer.balance)
        assertEquals(Money(Currency.USD, -700), account2AfterTransfer.balance)
    }
}
