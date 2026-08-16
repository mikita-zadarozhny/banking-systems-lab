package org.mikita.bankingsystemslab.transaction.service

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mikita.bankingsystemslab.transaction.domain.Account
import org.mikita.bankingsystemslab.transaction.domain.Currency
import org.mikita.bankingsystemslab.transaction.domain.Money
import org.mikita.bankingsystemslab.transaction.repository.AccountRepository
import org.mikita.bankingsystemslab.transaction.service.command.UpdateBalanceCommand
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
    fun updateBalance() {

        // given
        val updateBalanceCommand = UpdateBalanceCommand(
            10000,
            Money(
                Currency.USD,
                BigDecimal.valueOf(100)
            )
        )

        every { accountRepository.findById(10000) } returns Optional.of(
            Account(
                accountId = 10000,
                balance = Money(
                    currency = Currency.USD,
                    amount = BigDecimal.valueOf(200)
                ),
                version = 0
            )
        )

        // when
        accountService.updateBalance(updateBalanceCommand)

        // then
        verify {
            accountRepository.findById(10000)
            accountRepository.updateBalanceOptimistically(
                Account(
                    accountId = 10000,
                    balance = Money(
                        currency = Currency.USD,
                        amount = BigDecimal.valueOf(300)
                    ),
                    version = 0
                )
            )
        }

    }
}
