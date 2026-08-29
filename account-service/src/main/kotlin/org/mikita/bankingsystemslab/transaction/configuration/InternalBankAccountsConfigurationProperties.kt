package org.mikita.bankingsystemslab.transaction.configuration

import org.mikita.bankingsystemslab.transaction.domain.common.Currency
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "banking.internal.accounts")
data class InternalBankAccountsConfigurationProperties (
    val cashAccounts: Map<Currency, Long>
)
