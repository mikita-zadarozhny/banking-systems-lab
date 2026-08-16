package org.mikita.bankingsystemslab.transaction.repository

import org.mikita.bankingsystemslab.transaction.domain.Account
import org.mikita.bankingsystemslab.transaction.domain.AccountType
import org.mikita.bankingsystemslab.transaction.domain.Currency
import org.mikita.bankingsystemslab.transaction.domain.Money
import org.mikita.bankingsystemslab.transaction.exception.AccountDoesNotExistException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
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
                accountType = AccountType.ofCode(rs.getInt(2)),
                balance = Money(Currency.ofCode(rs.getInt(3)), rs.getBigDecimal(4)),
                version = rs.getInt(5)
            )
        }

        val query = "SELECT account_id, account_type, currency, balance, version " +
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
    fun getById(id: Long): Account {
        return findById(id).orElseThrow{ AccountDoesNotExistException(id) }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    fun saveNew(currency: Currency): Account {
        val insertQuery = "INSERT INTO accounts (account_id, account_type, currency, balance, version) " +
                "VALUES (nextval('account_id_seq'), ?, ?, 0, 0) RETURNING account_id"

        val accountId: Long? = jdbcTemplate.queryForObject(
            insertQuery,
            arrayOf<Any?>(
                AccountType.CUSTOMER_DEPOSIT.code,
                currency.code
            ),
            intArrayOf(
                Types.INTEGER,
                Types.INTEGER
            ),
            Long::class.java
        )

        return Account(
            accountId = accountId!!,
            accountType = AccountType.CUSTOMER_DEPOSIT,
            balance = Money(currency, BigDecimal.ZERO),
            version = 0
        )
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
