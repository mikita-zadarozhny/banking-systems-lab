package org.mikita.bankingsystemslab.transaction.api

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.mikita.bankingsystemslab.transaction.api.dto.AccountResponseDto
import org.mikita.bankingsystemslab.transaction.api.dto.ApiErrorResponseDto
import org.mikita.bankingsystemslab.transaction.domain.account.AccountType
import org.mikita.bankingsystemslab.transaction.domain.account.AccountingType
import org.mikita.bankingsystemslab.transaction.domain.common.Currency
import org.springframework.test.web.servlet.client.expectBody
import java.math.BigDecimal

class AccountControllerIntegrationTest : BaseIntegrationTest() {

    @ParameterizedTest
    @EnumSource(Currency::class)
    fun shouldCreateAndGetAccount_whenAccountTypeIsCustomerDeposit_andCurrencyIsSupported(
        desiredCurrency: Currency
    ) {
        // when & then
        val createdAccount = createAndGetResponseSpec(desiredCurrency)
            .expectStatus().isCreated()
            .expectBody<AccountResponseDto>().consumeWith {
                val account = it.responseBody
                assertNotNull(account)
                assertEquals(AccountType.CUSTOMER_DEPOSIT, account.accountType)
                assertEquals(AccountingType.LIABILITY, account.accountingType)
                assertEquals(desiredCurrency, account.balance.currency)
                assertEquals(BigDecimal.ZERO, account.balance.amount)
            }.returnResult().responseBody!!

        getAccountResponseSpec(createdAccount.accountId)
            .expectStatus().isOk()
            .expectBody<AccountResponseDto>()
            .consumeWith {
                val account = it.responseBody
                assertNotNull(account)
                assertEquals(AccountType.CUSTOMER_DEPOSIT, account.accountType)
                assertEquals(AccountingType.LIABILITY, account.accountingType)
                assertEquals(desiredCurrency, account.balance.currency)
                assertEquals(BigDecimal.ZERO, account.balance.amount)
            }
    }

    @Test
    fun shouldThrowException_whenGetAccount_andAccountDoesNotExist() {

        // when & then
        getAccountResponseSpec(-1)
            .expectStatus().isNotFound()
            .expectBody<ApiErrorResponseDto>()
            .consumeWith {
                val errorMessageBody = it.responseBody
                assertNotNull(errorMessageBody)
                assertEquals("Account does not exist.", errorMessageBody.message)
            }
    }
}
