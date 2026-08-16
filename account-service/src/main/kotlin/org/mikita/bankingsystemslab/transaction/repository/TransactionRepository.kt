package org.mikita.bankingsystemslab.transaction.repository

import org.mikita.bankingsystemslab.transaction.domain.Transaction
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.sql.Types

@Repository
class TransactionRepository (
    private val jdbcTemplate: JdbcTemplate
) {

    @Transactional(propagation = Propagation.MANDATORY)
    fun save(transaction: Transaction) {
        jdbcTemplate.update(
            "INSERT INTO transactions (id, idempotency_key, transaction_type) VALUES (?, ?, ?)",
            arrayOf<Any?>(
                transaction.id,
                transaction.idempotencyKey,
                transaction.type.name
            ),
            intArrayOf(
                Types.VARCHAR,
                Types.VARCHAR,
                Types.CHAR,
            )
        )
    }

}
