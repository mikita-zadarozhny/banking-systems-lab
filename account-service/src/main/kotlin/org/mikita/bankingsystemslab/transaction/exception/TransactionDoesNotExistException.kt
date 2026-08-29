package org.mikita.bankingsystemslab.transaction.exception

class TransactionDoesNotExistException : RuntimeException {
    constructor(accountId: String) : super("Transaction '$accountId' does not exist.")
}
