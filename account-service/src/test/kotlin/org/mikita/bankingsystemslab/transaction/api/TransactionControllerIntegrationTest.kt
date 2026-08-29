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
    fun shouldDeposit_whenCreditAccountTypeIsCustomerDeposit_andDepositAccountTypeIsCash() {

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
    fun shouldTransfer_whenCreditAccountTypeIsCustomerDeposit_andDebitAccountTypeIsCustomerDeposit() {

        // given
        val customerDepositAccount1 = createAccount(Currency.USD)
        val customerDepositAccount2 = createAccount(Currency.USD)
        val usdCashAccountBeforeDeposit = getAccount(properties.cashAccounts[Currency.USD]!!)

        deposit(customerDepositAccount1.accountId, Money(Currency.USD, 1000))
        val usdCashAccountAfterDeposit = getAccount(properties.cashAccounts[Currency.USD]!!)

        // when
        transfer(customerDepositAccount1.accountId, customerDepositAccount2.accountId, Money(Currency.USD, 700))

        // then
        val customerDepositAccount1AfterTransfer = getAccount(customerDepositAccount1.accountId)
        val customerDepositAccount2AfterTransfer = getAccount(customerDepositAccount2.accountId)
        val usdCashAccountAfterTransfer = getAccount(properties.cashAccounts[Currency.USD]!!)

        checkDebitAndCreditEquality(Currency.USD)
        assertEquals(Money(Currency.USD, 300), customerDepositAccount1AfterTransfer.balance)
        assertEquals(Money(Currency.USD, 700), customerDepositAccount2AfterTransfer.balance)
        assertEquals(Money(Currency.USD, 1000), usdCashAccountAfterDeposit.balance - usdCashAccountBeforeDeposit.balance)
        assertEquals(usdCashAccountAfterDeposit.balance, usdCashAccountAfterTransfer.balance)
    }

    @Test
    fun shouldWithdraw_whenDebitAccountTypeIsCustomerDeposit_andCreditAccountTypeIsCash() {

        // given
        val customerDepositAccount1 = createAccount(Currency.USD)
        val customerDepositAccount2 = createAccount(Currency.USD)
        val usdCashAccountBeforeDeposit = getAccount(properties.cashAccounts[Currency.USD]!!)

        deposit(customerDepositAccount1.accountId, Money(Currency.USD, 1000))
        val usdCashAccountAfterDeposit = getAccount(properties.cashAccounts[Currency.USD]!!)

        transfer(customerDepositAccount1.accountId, customerDepositAccount2.accountId, Money(Currency.USD, 600))
        val usdCashAccountAfterTransfer = getAccount(properties.cashAccounts[Currency.USD]!!)

        // when
        withdraw(customerDepositAccount2.accountId, Money(Currency.USD, 300))
        val usdCashAccountAfterWithdraw = getAccount(properties.cashAccounts[Currency.USD]!!)

        // then
        val customerDepositAccount2AfterWithdraw = getAccount(customerDepositAccount2.accountId)

        checkDebitAndCreditEquality(Currency.USD)
        assertEquals(Money(Currency.USD, 300), customerDepositAccount2AfterWithdraw.balance)
        assertEquals(Money(Currency.USD, 1000), usdCashAccountAfterDeposit.balance - usdCashAccountBeforeDeposit.balance)
        assertEquals(usdCashAccountAfterDeposit.balance, usdCashAccountAfterTransfer.balance)
        assertEquals(Money(Currency.USD, 300), usdCashAccountAfterTransfer.balance - usdCashAccountAfterWithdraw.balance)

    }
}
