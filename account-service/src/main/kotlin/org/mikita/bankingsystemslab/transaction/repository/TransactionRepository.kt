package org.mikita.bankingsystemslab.transaction.repository

import org.mikita.bankingsystemslab.transaction.domain.transaction.Transaction
import org.mikita.bankingsystemslab.transaction.domain.transaction.TransactionType
import org.mikita.bankingsystemslab.transaction.exception.TransactionDoesNotExistException
import org.mikita.bankingsystemslab.transaction.exception.TransactionIdClashException
import org.mikita.bankingsystemslab.transaction.exception.TransactionIdempotencyKeyClashException
import org.springframework.dao.DuplicateKeyException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.sql.Types
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

@Repository
class TransactionRepository (
    private val jdbcTemplate: JdbcTemplate
) {

    val pgKeyPattern: Pattern = Pattern.compile("Key \\(([^)]+)\\)=")

    @Transactional(propagation = Propagation.MANDATORY)
    fun findById(id: String): Optional<Transaction> {

        val transactionRowMapper = RowMapper { rs, _ ->
            Transaction(
                id = rs.getString(1),
                idempotencyKey = rs.getString(2),
                type = TransactionType.valueOf(rs.getString(3))
            )
        }

        val query = "SELECT id, idempotency_key, transaction_type " +
                "FROM transactions " +
                "WHERE id = ?"

        val queryForObject: Transaction

        try {
            queryForObject = jdbcTemplate.queryForObject(query, transactionRowMapper, id)
        } catch (_: EmptyResultDataAccessException) {
            return Optional.empty()
        }

        return Optional.of(queryForObject)
    }

    @Transactional(propagation = Propagation.MANDATORY)
    fun getById(id: String): Transaction {
        return findById(id).orElseThrow{ TransactionDoesNotExistException(id) }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    fun saveNew(transaction: Transaction) : Transaction {

        try {
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
                    Types.VARCHAR,
                )
            )
        } catch (ex: DuplicateKeyException) {
            val rootMessage: String = if (ex.rootCause != null) ex.rootCause!!.message!! else ex.message!!
            val matcher: Matcher = pgKeyPattern.matcher(rootMessage)

            if (matcher.find()) {
                val duplicatedField = matcher.group(1)

                if ("idempotency_key" == duplicatedField) {
                    throw TransactionIdempotencyKeyClashException(transaction.idempotencyKey)
                }
                if ("id" == duplicatedField) {
                    throw TransactionIdClashException(transaction.id)
                }
            }
            throw ex
        }

        return transaction
    }

}
