package org.mikita.bankingsystemslab.transaction.repository

import org.mikita.bankingsystemslab.transaction.domain.Account
import org.mikita.bankingsystemslab.transaction.domain.Currency
import org.mikita.bankingsystemslab.transaction.domain.Money
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.sql.Types
import java.util.Optional

@Repository()
class AccountRepository (
    private val jdbcTemplate: JdbcTemplate
) {
    @Transactional(propagation = Propagation.MANDATORY)
    fun findById(id: Long): Optional<Account> {

        val accountRowMapper = RowMapper { rs, _ ->
            Account(
                accountId = rs.getLong(1),
                balance = Money(Currency.valueOf(rs.getString(2)), rs.getBigDecimal(3)),
                version = rs.getInt(4)
            )
        }

        val query = "SELECT account_id, currency, balance, version " +
                "FROM accounts " +
                "WHERE account_id = ?"

        val queryForObject: Account

        try {
            queryForObject = jdbcTemplate.queryForObject(query, accountRowMapper, id)
        } catch (_: EmptyResultDataAccessException) {
            return Optional.empty()
        }

        return Optional.of(queryForObject)
    }

    @Transactional(propagation = Propagation.MANDATORY)
    fun createAccount(currency: Currency): Long {
        val insertQuery = "INSERT INTO accounts (account_id, currency, balance, version) " +
                "VALUES (nextval('account_id_seq'), ?, 0, 0) RETURNING account_id"

        val accountId: Long? = jdbcTemplate.queryForObject(
            insertQuery,
            arrayOf(currency.name),
            intArrayOf(Types.CHAR),
            Long::class.java
        )

        return accountId!!
    }

    @Transactional(propagation = Propagation.MANDATORY)
    fun updateBalanceOptimistically(account: Account) {

        val updateQuery = "UPDATE accounts " +
                "SET balance = ?, version = version + 1 " +
                "WHERE account_id = ? and version = ?"

        val updatedRows = jdbcTemplate.update(
            updateQuery,
            arrayOf<Any?>(
                account.balance.amount,
                account.accountId,
                account.version
            ),
            intArrayOf(
                Types.DECIMAL,
                Types.BIGINT,
                Types.INTEGER
            )
        )

        if (updatedRows == 0) {
            throw OptimisticLockingFailureException("Unable to update account. Expected version was ${account.version}")
        }
    }
}
