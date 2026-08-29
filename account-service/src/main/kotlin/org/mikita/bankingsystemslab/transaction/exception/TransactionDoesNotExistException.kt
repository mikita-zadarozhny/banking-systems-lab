package org.mikita.bankingsystemslab.transaction.exception

class TransactionDoesNotExistException : RuntimeException {
    constructor(accountId: Long) : super("Transaction '$accountId' does not exist.")
}
