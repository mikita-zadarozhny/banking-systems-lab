package org.mikita.bankingsystemslab.transaction.api

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mikita.bankingsystemslab.transaction.api.dto.CreateAccountRequestDto
import org.mikita.bankingsystemslab.transaction.api.dto.CreateAccountResponseDto
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

@Testcontainers
@ActiveProfiles("integration-tests")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccountServiceIntegrationTest {

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
    fun shouldCreateAccount() {
        restTestClient.post().uri("/api/v1/accounts").body(
            CreateAccountRequestDto(
                currency = Currency.USD
            )
        ).exchange()
            .expectStatus().isCreated()
            .expectBody<CreateAccountResponseDto>()
    }
}