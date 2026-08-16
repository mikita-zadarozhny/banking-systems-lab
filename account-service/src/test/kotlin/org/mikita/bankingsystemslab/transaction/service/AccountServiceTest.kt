package org.mikita.bankingsystemslab.transaction.service

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mikita.bankingsystemslab.transaction.domain.account.Account
import org.mikita.bankingsystemslab.transaction.domain.account.AccountType
import org.mikita.bankingsystemslab.transaction.domain.account.AccountingType
import org.mikita.bankingsystemslab.transaction.domain.common.Currency
import org.mikita.bankingsystemslab.transaction.domain.common.Money
import org.mikita.bankingsystemslab.transaction.repository.AccountRepository
import org.mikita.bankingsystemslab.transaction.service.command.UpdateAccountBalanceCommand
import java.math.BigDecimal
import java.util.Optional

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
    fun updateAccountBalance() {

        // given
        val updateAccountBalanceCommand = UpdateAccountBalanceCommand(
            10000,
            Money(
                Currency.USD,
                BigDecimal.valueOf(-200)
            )
        )

        every { accountRepository.findById(10000) } returns Optional.of(
            Account(
                accountId = 10000,
                accountType = AccountType.CUSTOMER_DEPOSIT,
                accountingType = AccountingType.LIABILITY,
                balance = Money(
                    currency = Currency.USD,
                    amount = BigDecimal.valueOf(-100)
                ),
                version = 0
            )
        )

        // when
        accountService.updateAccountBalance(updateAccountBalanceCommand)

        // then
        verify {
            accountRepository.findById(10000)
            accountRepository.updateBalanceOptimistically(
                Account(
                    accountId = 10000,
                    accountType = AccountType.CUSTOMER_DEPOSIT,
                    accountingType = AccountingType.LIABILITY,
                    balance = Money(
                        currency = Currency.USD,
                        amount = BigDecimal.valueOf(-300)
                    ),
                    version = 0
                )
            )
        }

    }
}
