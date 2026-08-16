package org.mikita.bankingsystemslab.transaction.api

import org.junit.jupiter.api.BeforeEach
import org.mikita.bankingsystemslab.transaction.api.dto.CreateAccountRequestDto
import org.mikita.bankingsystemslab.transaction.api.dto.AccountResponseDto
import org.mikita.bankingsystemslab.transaction.api.dto.CreateTransactionRequestDto
import org.mikita.bankingsystemslab.transaction.api.dto.CreateTransactionResponseDto
import org.mikita.bankingsystemslab.transaction.domain.Currency
import org.mikita.bankingsystemslab.transaction.domain.Money
import org.mikita.bankingsystemslab.transaction.domain.TransactionType
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
import java.util.UUID

@Testcontainers
@ActiveProfiles("integration-tests")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class BaseIntegrationTest {

    @LocalServerPort
    private var port: Int = 0

    lateinit var restTestClient: RestTestClient

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

    protected fun createAccount(currency: Currency): AccountResponseDto {
        return restTestClient.post()
            .uri("/api/v1/accounts")
            .body(CreateAccountRequestDto(currency))
            .exchange()
            .expectStatus().isCreated()
            .expectBody<AccountResponseDto>()
            .returnResult().responseBody!!
    }

    protected fun createAccountAndGetIt(currency: Currency): Long {
        return createAccount(currency).accountId
    }

    protected fun getAccount(accountId: Long): AccountResponseDto {
        return restTestClient.get()
            .uri("/api/v1/accounts/{accountId}", accountId)
            .exchange()
            .expectStatus().isOk()
            .expectBody<AccountResponseDto>()
            .returnResult().responseBody!!
    }

    protected fun depositUsd(recipient: Long, amount: Long): CreateTransactionResponseDto {
        return restTestClient.post()
            .uri("/api/v1/transactions")
            .body(CreateTransactionRequestDto(
                idempotencyKey = UUID.randomUUID().toString(),
                money = Money(Currency.USD, amount),
                sender = 100000,
                recipient = recipient,
                type = TransactionType.DEPOSIT
            ))
            .exchange()
            .expectStatus().isCreated()
            .expectBody<CreateTransactionResponseDto>()
            .returnResult().responseBody!!
    }

    protected fun transfer(sender: Long, recipient: Long, money: Money): CreateTransactionResponseDto {
        return restTestClient.post()
            .uri("/api/v1/transactions")
            .body(CreateTransactionRequestDto(
                idempotencyKey = UUID.randomUUID().toString(),
                money = money,
                sender = sender,
                recipient = recipient,
                type = TransactionType.DEPOSIT
            ))
            .exchange()
            .expectStatus().isCreated()
            .expectBody<CreateTransactionResponseDto>()
            .returnResult().responseBody!!
    }
}
