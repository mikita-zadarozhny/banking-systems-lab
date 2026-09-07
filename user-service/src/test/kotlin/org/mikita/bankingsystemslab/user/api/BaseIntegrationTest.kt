package org.mikita.bankingsystemslab.user.api

import org.junit.jupiter.api.BeforeEach
import org.mikita.bankingsystemslab.user.api.dto.CreateUserRequestDto
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.client.RestTestClient
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.postgresql.PostgreSQLContainer

@DirtiesContext
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

    protected fun createUser(username: String): RestTestClient.ResponseSpec {
        return restTestClient.post()
            .uri("/api/v1/users")
            .body(CreateUserRequestDto(username))
            .exchange()
    }
}
