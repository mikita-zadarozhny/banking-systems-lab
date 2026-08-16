package org.mikita.bankingsystemslab.transaction.repository

import org.mikita.bankingsystemslab.transaction.domain.common.Currency
import org.mikita.bankingsystemslab.transaction.domain.common.Money
import org.mikita.bankingsystemslab.transaction.domain.ledger.LedgerEntry
import org.mikita.bankingsystemslab.transaction.domain.ledger.LedgerEntryType
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.sql.PreparedStatement
import java.sql.Types

@Repository
class LedgerEntryRepository (
    private val jdbcTemplate: JdbcTemplate
) {

    @Transactional(propagation = Propagation.MANDATORY)
    fun saveAll(ledgerEntries: List<LedgerEntry>) {
        val insertQuery = "INSERT INTO ledger (transaction_id, entry_type, account_id, currency, amount) " +
                "VALUES (?, ?, ?, ?, ?)"

        jdbcTemplate.batchUpdate(insertQuery, object : BatchPreparedStatementSetter {

            override fun setValues(ps: PreparedStatement, i: Int) {
                val ledgerEntry: LedgerEntry = ledgerEntries[i]
                ps.setString(1, ledgerEntry.transactionId)
                ps.setString(2, ledgerEntry.entryType.name)
                ps.setLong(3, ledgerEntry.accountId)
                ps.setString(4, ledgerEntry.money.currency.name)
                ps.setBigDecimal(5, ledgerEntry.money.amount)
            }

            override fun getBatchSize(): Int {
                return ledgerEntries.size
            }
        })
    }

    fun getSumOfLedgerEntries(entry_type: LedgerEntryType, currency: Currency): Money {
        val query = "SELECT SUM(amount) " +
                "FROM ledger " +
                "WHERE entry_type = ? and currency = ?"
        val amount = jdbcTemplate.queryForObject(
            query,
            arrayOf<Any?>(
                entry_type.name,
                currency.name
            ),
            intArrayOf(
                Types.VARCHAR,
                Types.CHAR,
            ),
            BigDecimal::class.java
        )

        return Money(currency, amount!!)
    }
}
