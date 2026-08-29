package org.mikita.bankingsystemslab.transaction.repository

import org.flywaydb.core.Flyway
import org.junit.jupiter.api.BeforeEach
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.postgresql.PostgreSQLContainer

@Testcontainers
open class BaseJdbcTest {

    protected lateinit var jdbcTemplate: JdbcTemplate

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
    open fun setUp() {
        val dataSource = DriverManagerDataSource()
        dataSource.setDriverClassName(postgres.getDriverClassName())
        dataSource.setUrl(postgres.getJdbcUrl())
        dataSource.setUsername(postgres.getUsername())
        dataSource.setPassword(postgres.getPassword())

        val flyway = Flyway.configure()
            .dataSource(dataSource)
            .locations("classpath:db/migration")
            .cleanDisabled(false)
            .load()

        flyway.clean()
        flyway.migrate()

        this.jdbcTemplate = JdbcTemplate(dataSource)
    }

    fun getCurrentAccountIdSequenceValue(): Long {
        return jdbcTemplate.queryForObject<Long>("SELECT COALESCE(last_value, start_value) " +
                "FROM pg_sequences WHERE sequencename = 'account_id_seq'", Long::class.java)!!
    }

}
