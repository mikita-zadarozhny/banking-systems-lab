package org.mikita.bankingsystemslab.user.domain

data class User (
    val userId: Long,
    val username: String,
    val userStatus: UserStatus
)
