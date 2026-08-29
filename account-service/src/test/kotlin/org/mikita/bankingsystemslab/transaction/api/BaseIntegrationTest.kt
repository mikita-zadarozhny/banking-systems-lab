package org.mikita.bankingsystemslab.transaction.api

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.mikita.bankingsystemslab.transaction.api.dto.CreateAccountRequestDto
import org.mikita.bankingsystemslab.transaction.api.dto.AccountResponseDto
import org.mikita.bankingsystemslab.transaction.api.dto.CreateTransactionRequestDto
import org.mikita.bankingsystemslab.transaction.api.dto.CreateTransactionResponseDto
import org.mikita.bankingsystemslab.transaction.domain.common.Currency
import org.mikita.bankingsystemslab.transaction.domain.common.Money
import org.mikita.bankingsystemslab.transaction.domain.ledger.LedgerEntryType
import org.mikita.bankingsystemslab.transaction.domain.transaction.TransactionType
import org.mikita.bankingsystemslab.transaction.repository.LedgerEntryRepository
import org.springframework.beans.factory.annotation.Autowired
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

    @Autowired
    lateinit var ledgerEntryRepository: LedgerEntryRepository

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

    protected fun createAndGetResponseSpec(currency: Currency): RestTestClient.ResponseSpec {
        return restTestClient.post()
            .uri("/api/v1/accounts")
            .body(CreateAccountRequestDto(currency))
            .exchange()
    }

    protected fun createAccount(currency: Currency): AccountResponseDto {
        return createAndGetResponseSpec(currency)
            .expectStatus().isCreated()
            .expectBody<AccountResponseDto>()
            .returnResult().responseBody!!
    }

    protected fun getAccountResponseSpec(accountId: Long): RestTestClient.ResponseSpec {
        return restTestClient.get()
            .uri("/api/v1/accounts/{accountId}", accountId)
            .exchange()
    }

    protected fun getAccount(accountId: Long): AccountResponseDto {
        return getAccountResponseSpec(accountId)
            .expectStatus().isOk()
            .expectBody<AccountResponseDto>()
            .returnResult().responseBody!!
    }

    protected fun createTransactionAndGetSpec(
        idempotencyKey: String,
        money: Money,
        sender: Long?,
        recipient: Long?,
        type: TransactionType
    ): RestTestClient.ResponseSpec {
        return restTestClient.post()
            .uri("/api/v1/transactions")
            .body(
                CreateTransactionRequestDto(
                    idempotencyKey = idempotencyKey,
                    money = money,
                    sender = sender,
                    recipient = recipient,
                    type = type
                )
            )
            .exchange()
    }

    protected fun deposit(recipient: Long, money: Money): CreateTransactionResponseDto {
        return deposit(recipient, money, UUID.randomUUID().toString())
    }

    protected fun deposit(recipient: Long, money: Money, idempotencyKey: String): CreateTransactionResponseDto {
        return createTransactionAndGetSpec(
            idempotencyKey = idempotencyKey,
            money = money,
            sender = null,
            recipient = recipient,
            type = TransactionType.DEPOSIT
        )
            .expectStatus().isCreated()
            .expectBody<CreateTransactionResponseDto>()
            .returnResult().responseBody!!
    }

    protected fun transfer(sender: Long, recipient: Long, money: Money): CreateTransactionResponseDto {
        return transfer(sender, recipient, money, UUID.randomUUID().toString())
    }

    protected fun transfer(sender: Long, recipient: Long, money: Money, idempotencyKey: String): CreateTransactionResponseDto {
        return createTransactionAndGetSpec(
            idempotencyKey = idempotencyKey,
            money = money,
            sender = sender,
            recipient = recipient,
            type = TransactionType.TRANSFER
        )
            .expectStatus().isCreated()
            .expectBody<CreateTransactionResponseDto>()
            .returnResult().responseBody!!
    }

    protected fun withdraw(sender: Long, money: Money): CreateTransactionResponseDto {
        return withdraw(sender, money, UUID.randomUUID().toString())
    }

    protected fun withdraw(sender: Long, money: Money, idempotencyKey: String): CreateTransactionResponseDto {
        return createTransactionAndGetSpec(
            idempotencyKey = idempotencyKey,
            money = money,
            sender = sender,
            recipient = null,
            type = TransactionType.WITHDRAW
        )
            .expectStatus().isCreated()
            .expectBody<CreateTransactionResponseDto>()
            .returnResult().responseBody!!
    }

    protected fun checkDebitAndCreditEquality(currency: Currency) {
        val totalDebit = ledgerEntryRepository.getSumOfLedgerEntries(LedgerEntryType.DEBIT, currency)
        val totalCredit = ledgerEntryRepository.getSumOfLedgerEntries(LedgerEntryType.CREDIT, currency)

        assertEquals(totalDebit, totalCredit)
    }
}
