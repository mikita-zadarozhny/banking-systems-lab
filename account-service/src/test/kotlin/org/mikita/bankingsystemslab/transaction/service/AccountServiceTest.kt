package org.mikita.bankingsystemslab.transaction.service

import io.mockk.MockKAnnotations
import io.mockk.Ordering
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mikita.bankingsystemslab.transaction.domain.account.Account
import org.mikita.bankingsystemslab.transaction.domain.account.AccountType
import org.mikita.bankingsystemslab.transaction.domain.account.AccountingType
import org.mikita.bankingsystemslab.transaction.domain.common.Currency
import org.mikita.bankingsystemslab.transaction.domain.common.Money
import org.mikita.bankingsystemslab.transaction.exception.OverdraftNotSupportedException
import org.mikita.bankingsystemslab.transaction.repository.AccountRepository
import org.mikita.bankingsystemslab.transaction.service.command.CreateAccountCommand
import org.mikita.bankingsystemslab.transaction.service.command.UpdateAccountBalanceCommand
import java.math.BigDecimal
import java.util.Optional
import java.util.stream.Stream

class AccountServiceTest {

    @MockK
    lateinit var accountRepository: AccountRepository

    lateinit var accountService: AccountService

    @BeforeEach
    fun beforeEach() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        accountService = AccountService(accountRepository)
    }

    @Test
    fun shouldGetAccount()  {

        // given
        val accountId: Long = 1000

        val expectedAccount = Account(
            accountId = 10000,
            accountType = AccountType.CUSTOMER_DEPOSIT,
            accountingType = AccountingType.LIABILITY,
            balance = Money(Currency.USD, 0),
            version = 0
        )

        every { accountRepository.getById(accountId) } returns expectedAccount

        // when
        val account = accountService.getAccount(accountId)

        // then
        assertEquals(expectedAccount, account)
    }

    @Test
    fun shouldCreateAccount()  {

        // given
        val createAccountCommand = CreateAccountCommand(Currency.USD)

        val expectedAccount = Account(
            accountId = 10000,
            accountType = AccountType.CUSTOMER_DEPOSIT,
            accountingType = AccountingType.LIABILITY,
            balance = Money(Currency.USD, 0),
            version = 0
        )

        every { accountRepository.saveNew(Currency.USD) } returns expectedAccount

        // when
        val account = accountService.createAccount(createAccountCommand)

        // then
        assertEquals(expectedAccount, account)
    }

    @ParameterizedTest
    @MethodSource("updateAccountBalanceHappyCases")
    fun shouldUpdateAccountBalance_whenAccountBalanceRemainsPositiveAfterUpdate(
        initialBalance: Money, delta: Money, expectedBalance: Money
    ) {

        // given
        val updateAccountBalanceCommand = UpdateAccountBalanceCommand(10000, delta)

        every { accountRepository.findById(10000) } returns Optional.of(
            Account(
                accountId = 10000,
                accountType = AccountType.CUSTOMER_DEPOSIT,
                accountingType = AccountingType.LIABILITY,
                balance = initialBalance,
                version = 0
            )
        )

        // when
        accountService.updateAccountBalance(updateAccountBalanceCommand)

        // then
        verify (ordering = Ordering.SEQUENCE) {
            accountRepository.findById(10000)
            accountRepository.updateBalanceOptimistically(
                Account(
                    accountId = 10000,
                    accountType = AccountType.CUSTOMER_DEPOSIT,
                    accountingType = AccountingType.LIABILITY,
                    balance = expectedBalance,
                    version = 0
                )
            )
        }
    }

    @ParameterizedTest
    @MethodSource("updateAccountBalanceFailureCases")
    fun shouldThrowException_whenAccountBalanceBecomesNegativeAfterUpdate(
        initialBalance: Money, delta: Money
    ) {
        // given
        val updateAccountBalanceCommand = UpdateAccountBalanceCommand(10000, delta)

        every { accountRepository.findById(10000) } returns Optional.of(
            Account(
                accountId = 10000,
                accountType = AccountType.CUSTOMER_DEPOSIT,
                accountingType = AccountingType.LIABILITY,
                balance = initialBalance,
                version = 0
            )
        )

        // when
        val thrownException = assertThrows<OverdraftNotSupportedException> {
            accountService.updateAccountBalance(updateAccountBalanceCommand)
        }

        // then
        val expectedErrorMessage = "Account '10000' liability is '${initialBalance.amount}'. " +
                "Applying liability delta '${delta.amount}' would result into overdraft."
        assertEquals(
            expectedErrorMessage,
            thrownException.message)
    }

    companion object {
        @JvmStatic
        fun updateAccountBalanceHappyCases(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    Money(Currency.USD, 100),
                    Money(Currency.USD, 100),
                    Money(Currency.USD, 200)
                ),
                Arguments.of(
                    Money(Currency.USD, 100),
                    Money(Currency.USD, -100),
                    Money(Currency.USD, 0)
                ),
                Arguments.of(
                    Money(Currency.EUR, 200),
                    Money(Currency.EUR, 300),
                    Money(Currency.EUR, 500)
                ),
                Arguments.of(
                    Money(Currency.EUR, Long.MAX_VALUE),
                    Money(Currency.EUR, Long.MAX_VALUE),
                    Money(Currency.EUR, BigDecimal.valueOf(Long.MAX_VALUE).add(BigDecimal.valueOf(Long.MAX_VALUE)))
                )
            )
        }

        @JvmStatic
        fun updateAccountBalanceFailureCases(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    Money(Currency.USD, 100),
                    Money(Currency.USD, -200),
                ),
                Arguments.of(
                    Money(Currency.USD, 100),
                    Money(Currency.USD, -101),
                ),
                Arguments.of(
                    Money(Currency.EUR, 200),
                    Money(Currency.EUR, -300),
                ),
                Arguments.of(
                    Money(Currency.EUR, Long.MAX_VALUE),
                    Money(Currency.EUR, BigDecimal.valueOf(Long.MAX_VALUE)
                        .add(BigDecimal.valueOf(Long.MAX_VALUE))
                        .negate())
                )
            )
        }
    }
}
