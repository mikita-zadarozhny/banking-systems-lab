package org.mikita.bankingsystemslab.user.api

import org.mikita.bankingsystemslab.user.api.dto.UserResponseDto
import org.mikita.bankingsystemslab.user.domain.UserStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserController {

    @GetMapping("/{userId}")
    fun getUserById(@PathVariable userId: Long) : ResponseEntity<UserResponseDto> {

        return ResponseEntity<UserResponseDto>.ok(
            UserResponseDto(
                userId = userId,
                username = "TODO",
                userStatus = UserStatus.ENABLED
            )
        )
    }

}
