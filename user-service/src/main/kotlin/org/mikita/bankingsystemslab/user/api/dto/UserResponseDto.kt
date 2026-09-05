package org.mikita.bankingsystemslab.user.api.dto

import org.mikita.bankingsystemslab.user.domain.UserStatus

data class UserResponseDto (
    val userId: Long,
    val username: String,
    val userStatus: UserStatus
)
