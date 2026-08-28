package org.mikita.bankingsystemslab.transaction.repository

import org.mikita.bankingsystemslab.transaction.domain.account.Account
import org.mikita.bankingsystemslab.transaction.domain.account.AccountType
import org.mikita.bankingsystemslab.transaction.domain.account.AccountingType
import org.mikita.bankingsystemslab.transaction.domain.common.Currency
import org.mikita.bankingsystemslab.transaction.domain.common.Money
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
                accountType = AccountType.valueOf(rs.getString(2)),
                accountingType = AccountingType.valueOf(rs.getString(3)),
                balance = Money(Currency.valueOf(rs.getString(4)), rs.getBigDecimal(5)),
                version = rs.getInt(6)
            )
        }

        val query = "SELECT account_id, account_type, accounting_type, currency, balance, version " +
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
        val insertQuery = "INSERT INTO accounts (account_id, account_type, accounting_type, currency, balance, version) " +
                "VALUES (nextval('account_id_seq'), ?, ?, ?, 0, 0) RETURNING account_id"

        val accountId: Long? = jdbcTemplate.queryForObject(
            insertQuery,
            arrayOf<Any?>(
                AccountType.CUSTOMER_DEPOSIT.name,
                AccountingType.LIABILITY.name,
                currency.name
            ),
            intArrayOf(
                Types.VARCHAR,
                Types.VARCHAR,
                Types.VARCHAR
            ),
            Long::class.java
        )

        return Account(
            accountId = accountId!!,
            accountType = AccountType.CUSTOMER_DEPOSIT,
            accountingType = AccountingType.LIABILITY,
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
            throw OptimisticLockingFailureException("Unable to update account. Expected version was ${account.version}.")
        }
    }
}
