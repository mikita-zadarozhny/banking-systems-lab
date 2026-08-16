package org.mikita.bankingsystemslab.transaction.api

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mikita.bankingsystemslab.transaction.configuration.InternalBankAccountsConfigurationProperties
import org.mikita.bankingsystemslab.transaction.domain.common.Currency
import org.mikita.bankingsystemslab.transaction.domain.common.Money
import org.springframework.beans.factory.annotation.Autowired

class TransactionControllerIntegrationTest : BaseIntegrationTest() {

    @Autowired
    lateinit var properties: InternalBankAccountsConfigurationProperties

    @Test
    fun shouldDeposit() {

        // given
        val customerDepositAccount = createAccount(Currency.USD)
        val usdCashAccountBefore = getAccount(properties.cashAccounts[Currency.USD]!!)

        // when
        deposit(customerDepositAccount.accountId, Money(Currency.USD, 100))

        // then
        val customerDepositAccountAfter = getAccount(customerDepositAccount.accountId)
        val usdCashAccountAfter = getAccount(properties.cashAccounts[Currency.USD]!!)

        checkDebitAndCreditEquality(Currency.USD)
        assertEquals(Money(Currency.USD, 100), customerDepositAccountAfter.balance)
        assertEquals(Money(Currency.USD, 100), usdCashAccountAfter.balance - usdCashAccountBefore.balance)
    }

    @Test
    fun shouldTransfer() {

        // given
        val customerDepositAccount1 = createAccount(Currency.USD)
        val customerDepositAccount2 = createAccount(Currency.USD)
        val usdCashAccountBeforeDeposit = getAccount(properties.cashAccounts[Currency.USD]!!)

        deposit(customerDepositAccount1.accountId, Money(Currency.USD, 1000))

        // when
        transfer(customerDepositAccount1.accountId, customerDepositAccount2.accountId, Money(Currency.USD, 700))

        // then
        val customerDepositAccount1AfterTransfer = getAccount(customerDepositAccount1.accountId)
        val customerDepositAccount2AfterTransfer = getAccount(customerDepositAccount2.accountId)
        val usdCashAccountAfterDeposit = getAccount(properties.cashAccounts[Currency.USD]!!)

        checkDebitAndCreditEquality(Currency.USD)
        assertEquals(Money(Currency.USD, 300), customerDepositAccount1AfterTransfer.balance)
        assertEquals(Money(Currency.USD, 700), customerDepositAccount2AfterTransfer.balance)
        assertEquals(Money(Currency.USD, 1000), usdCashAccountAfterDeposit.balance - usdCashAccountBeforeDeposit.balance)
    }

    @Test
    fun shouldWithdraw() {

        // given
        val customerDepositAccount1 = createAccount(Currency.USD)
        val customerDepositAccount2 = createAccount(Currency.USD)
        val usdCashAccountBeforeDeposit = getAccount(properties.cashAccounts[Currency.USD]!!)

        deposit(customerDepositAccount1.accountId, Money(Currency.USD, 1000))

        transfer(customerDepositAccount1.accountId, customerDepositAccount2.accountId, Money(Currency.USD, 600))

        // when
        withdraw(customerDepositAccount2.accountId, Money(Currency.USD, 300))

        // then
        val customerDepositAccount2AfterWithdraw = getAccount(customerDepositAccount2.accountId)
        val usdCashAccountAfterDeposit = getAccount(properties.cashAccounts[Currency.USD]!!)

        checkDebitAndCreditEquality(Currency.USD)
        assertEquals(Money(Currency.USD, 300), customerDepositAccount2AfterWithdraw.balance)
        assertEquals(Money(Currency.USD, 700), usdCashAccountAfterDeposit.balance - usdCashAccountBeforeDeposit.balance)

    }
}
