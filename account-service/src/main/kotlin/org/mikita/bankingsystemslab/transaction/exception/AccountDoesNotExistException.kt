package org.mikita.bankingsystemslab.transaction.exception

class AccountDoesNotExistException : RuntimeException {
    constructor(accountId: Long) : super("Account '$accountId' does not exist.")
}
