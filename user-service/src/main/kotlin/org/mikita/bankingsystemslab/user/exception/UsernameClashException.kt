package org.mikita.bankingsystemslab.user.exception

class UsernameClashException : RuntimeException {
    constructor(username: String) : super("Username '${username}' already exists")
}
