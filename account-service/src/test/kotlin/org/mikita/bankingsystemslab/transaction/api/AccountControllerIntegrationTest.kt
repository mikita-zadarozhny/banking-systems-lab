package org.mikita.bankingsystemslab.transaction.api

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.mikita.bankingsystemslab.transaction.api.dto.CreateAccountRequestDto
import org.mikita.bankingsystemslab.transaction.api.dto.AccountResponseDto
import org.mikita.bankingsystemslab.transaction.domain.Currency
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.client.RestTestClient
import org.springframework.test.web.servlet.client.expectBody
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.postgresql.PostgreSQLContainer
import java.math.BigDecimal

@Testcontainers
@ActiveProfiles("integration-tests")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccountControllerIntegrationTest {

    @LocalServerPort
    private var port: Int = 0

    private lateinit var restTestClient: RestTestClient

    companion object {
        @Container
        private val postgres = PostgreSQLContainer("postgres:18.6")

        @JvmStatic
        @DynamicPropertySource
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
        }
    }

    @BeforeEach
    fun init() {
        restTestClient = RestTestClient.bindToServer().baseUrl("http://localhost:${port}").build()
    }

    @Test
    fun shouldCreateAndGetAccount() {
        val createdAccount = restTestClient.post()
            .uri("/api/v1/accounts")
            .body(CreateAccountRequestDto(Currency.USD))
            .exchange()
            .expectStatus().isCreated()
            .expectBody<AccountResponseDto>()
            .returnResult().responseBody

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
