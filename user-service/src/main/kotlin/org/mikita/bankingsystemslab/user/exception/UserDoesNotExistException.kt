package org.mikita.bankingsystemslab.user.exception

class UserDoesNotExistException : RuntimeException {
    constructor(userId: Long) : super("User with ID '${userId}' does not exist")
}
