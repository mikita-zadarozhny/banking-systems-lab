package org.mikita.bankingsystemslab.transaction.repository

import org.mikita.bankingsystemslab.transaction.domain.LedgerEntry
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.sql.PreparedStatement

@Repository
class LedgerEntryRepository (
    private val jdbcTemplate: JdbcTemplate
) {

    @Transactional(propagation = Propagation.MANDATORY)
    fun saveAll(ledgerEntries: Array<LedgerEntry>) {
        val insertQuery = "INSERT INTO ledger (transaction_id, account_id, currency, amount) VALUES (?, ?, ?, ?)"

        jdbcTemplate.batchUpdate(insertQuery, object : BatchPreparedStatementSetter {

            override fun setValues(ps: PreparedStatement, i: Int) {
                val ledgerEntry: LedgerEntry = ledgerEntries[i]
                ps.setString(1, ledgerEntry.transactionId)
                ps.setLong(2, ledgerEntry.accountId)
                ps.setString(3, ledgerEntry.money.currency.name)
                ps.setBigDecimal(4, ledgerEntry.money.amount)
            }

            override fun getBatchSize(): Int {
                return ledgerEntries.size
            }
        })
    }
}
