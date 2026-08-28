package org.mikita.bankingsystemslab.transaction.repository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.mikita.bankingsystemslab.transaction.domain.account.Account
import org.mikita.bankingsystemslab.transaction.domain.account.AccountType
import org.mikita.bankingsystemslab.transaction.domain.account.AccountingType
import org.mikita.bankingsystemslab.transaction.domain.common.Currency
import org.mikita.bankingsystemslab.transaction.domain.common.Money
import org.mikita.bankingsystemslab.transaction.exception.AccountDoesNotExistException
import org.springframework.dao.OptimisticLockingFailureException
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
class AccountRepositoryTest : BaseJdbcTest() {

    lateinit var accountRepository: AccountRepository

    @BeforeEach
    override fun setUp() {
        super.setUp()

        accountRepository = AccountRepository(jdbcTemplate)
    }

    @ParameterizedTest
    @EnumSource(Currency::class)
    fun shouldSaveNew_whenCurrencyIsSupported(currency: Currency) {

        // given
        val expected = Account(
            accountId = getCurrentAccountIdSequenceValue(),
            accountType = AccountType.CUSTOMER_DEPOSIT,
            accountingType = AccountingType.LIABILITY,
            balance = Money(currency, 0),
            version = 0
        )

        // when
        val actual = accountRepository.saveNew(currency)

        // then
        assertEquals(expected, actual)
    }

    @ParameterizedTest
    @EnumSource(Currency::class)
    fun shouldFindById_whenAccountExists(currency: Currency) {

        // given
        val expected = accountRepository.saveNew(currency)

        // when
        val actual = accountRepository.findById(expected.accountId);

        // then
        assertTrue(actual.isPresent)
        assertEquals(expected, actual.get())
    }

    @Test
    fun shouldReturnEmptyOptional_whenFindById_andAccountDoesNotExist() {

        // when
        val actual = accountRepository.findById(-1);

        // then
        assertTrue(actual.isEmpty)
    }

    @ParameterizedTest
    @EnumSource(Currency::class)
    fun shouldGetById_whenAccountExists(currency: Currency) {

        // given
        val expected = accountRepository.saveNew(currency)

        // when
        val actual = accountRepository.getById(expected.accountId);

        // then
        assertEquals(expected, actual)
    }

    @Test
    fun shouldThrowException_whenGetById_andAccountDoesNotExist() {

        // when
        val exception = assertThrows<AccountDoesNotExistException> {
            accountRepository.getById(-1)
        }

        // then
        assertEquals("Account '-1' does not exist.", exception.message)
    }

    @Test
    fun shouldUpdateBalanceOptimistically() {

        // given
        val initialAccount = accountRepository.saveNew(Currency.USD)

        val accountUpdate = initialAccount.copy(
            balance = Money(Currency.USD, 1000),
            version = 0
        )

        val conflictingAccountUpdate = initialAccount.copy(
            balance = Money(Currency.USD, 1000),
            version = 0
        )

        val expected = initialAccount.copy(
            balance = Money(Currency.USD, 1000),
            version = 1
        )

        // when
        accountRepository.updateBalanceOptimistically(accountUpdate)
        val actual = accountRepository.getById(accountUpdate.accountId)

        // then
        assertEquals(expected, actual)

        // when
        val exception = assertThrows<OptimisticLockingFailureException> {
            accountRepository.updateBalanceOptimistically(conflictingAccountUpdate)
        }

        // then
        assertEquals("Unable to update account. Expected version was 0.", exception.message)
    }
}