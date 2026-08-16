package org.mikita.bankingsystemslab.transaction.api

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.mikita.bankingsystemslab.transaction.api.dto.AccountResponseDto
import org.mikita.bankingsystemslab.transaction.domain.account.AccountingType
import org.mikita.bankingsystemslab.transaction.domain.common.Currency
import org.springframework.test.web.servlet.client.expectBody
import java.math.BigDecimal

class AccountControllerIntegrationTest : BaseIntegrationTest() {

    @Test
    fun shouldCreateAndGetAccount() {

        // given
        val desiredCurrency = Currency.USD

        // when & then
        val createdAccount = createAndGetResponseSpec(desiredCurrency)
            .expectStatus().isCreated()
            .expectBody<AccountResponseDto>().consumeWith {
                val account = it.responseBody
                assertNotNull(account)
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
                assertEquals(AccountingType.LIABILITY, account.accountingType)
                assertEquals(desiredCurrency, account.balance.currency)
                assertEquals(BigDecimal.ZERO, account.balance.amount)
            }
    }
}
