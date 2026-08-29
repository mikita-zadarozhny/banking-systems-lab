package org.mikita.bankingsystemslab.transaction.exception

import org.mikita.bankingsystemslab.transaction.domain.common.Currency

class CurrencyMismatchException : RuntimeException {

    constructor(expectedCurrency: Currency, actualCurrency: Currency)
            : super("Expected currency is '$expectedCurrency', but was '$actualCurrency'.")
}
