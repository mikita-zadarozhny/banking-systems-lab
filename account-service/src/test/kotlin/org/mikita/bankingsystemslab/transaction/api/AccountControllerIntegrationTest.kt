package org.mikita.bankingsystemslab.transaction.api

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.mikita.bankingsystemslab.transaction.api.dto.AccountResponseDto
import org.mikita.bankingsystemslab.transaction.domain.Currency
import org.springframework.test.web.servlet.client.expectBody
import java.math.BigDecimal

class AccountControllerIntegrationTest : BaseIntegrationTest() {

    @Test
    fun shouldCreateAndGetAccount() {
        val createdAccount = createAccount(Currency.USD)

        assertNotNull(createdAccount)
        assertEquals(Currency.USD, createdAccount.balance.currency)
        assertEquals(BigDecimal.ZERO, createdAccount.balance.amount)

        restTestClient.get().uri("/api/v1/accounts/{accountId}", createdAccount.accountId)
            .exchange()
            .expectStatus().isOk()
            .expectBody<AccountResponseDto>()
            .isEqualTo(createdAccount)
    }
}
